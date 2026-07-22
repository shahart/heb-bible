import crypto from 'node:crypto';

const GOOGLE_AUTH_URL = 'https://accounts.google.com/o/oauth2/v2/auth';
const GOOGLE_TOKEN_URL = 'https://oauth2.googleapis.com/token';
const GOOGLE_USERINFO_URL = 'https://openidconnect.googleapis.com/v1/userinfo';
const STATE_COOKIE = 'google_oauth_state';
const SESSION_COOKIE = 'heb_bible_session';
const STATE_MAX_AGE_SECONDS = 10 * 60;
const SESSION_MAX_AGE_SECONDS = 7 * 24 * 60 * 60;

const fallbackSessionSecret = crypto.randomBytes(32).toString('hex');

function getConfig() {
  return {
    clientId: process.env.GOOGLE_OAUTH_CLIENT_ID,
    clientSecret: process.env.GOOGLE_OAUTH_CLIENT_SECRET,
    redirectUri: process.env.GOOGLE_OAUTH_REDIRECT_URI,
    scopes: splitScopes(process.env.GOOGLE_OAUTH_SCOPES) || ['openid', 'email', 'profile'],
    postLoginRedirect: process.env.OAUTH_POST_LOGIN_REDIRECT || '/',
    sessionSecret: process.env.OAUTH_SESSION_SECRET || process.env.GOOGLE_OAUTH_SESSION_SECRET || fallbackSessionSecret
  };
}

function splitScopes(scopes) {
  if (!scopes) return null;
  const parsed = scopes.split(/[,\s]+/).map((scope) => scope.trim()).filter(Boolean);
  return parsed.length ? parsed : null;
}

function getRequestUrl(req) {
  return new URL(req.url, `${getProtocol(req)}://${req.headers.host}`);
}

function getRedirectUri(req, config) {
  if (config.redirectUri) return config.redirectUri;
  return `${getProtocol(req)}://${req.headers.host}/auth/google/callback`;
}

function getProtocol(req) {
  const forwardedProto = req.headers['x-forwarded-proto'];
  if (typeof forwardedProto === 'string' && forwardedProto.length > 0) {
    return forwardedProto.split(',')[0].trim();
  }
  return req.socket.encrypted ? 'https' : 'http';
}

function isSecureRequest(req) {
  return getProtocol(req) === 'https';
}

function parseCookies(req) {
  const header = req.headers.cookie;
  if (!header) return {};
  const cookies = {};
  for (const part of header.split(';')) {
    const separator = part.indexOf('=');
    if (separator === -1) continue;
    const name = part.slice(0, separator).trim();
    const value = part.slice(separator + 1).trim();
    cookies[name] = decodeURIComponent(value);
  }
  return cookies;
}

function cookie(name, value, req, options = {}) {
  const parts = [
    `${name}=${encodeURIComponent(value)}`,
    'Path=/',
    'HttpOnly',
    `SameSite=${options.sameSite || 'Lax'}`
  ];
  if (options.maxAge !== undefined) parts.push(`Max-Age=${options.maxAge}`);
  if (isSecureRequest(req)) parts.push('Secure');
  return parts.join('; ');
}

function clearCookie(name, req) {
  return cookie(name, '', req, { maxAge: 0 });
}

function sendJson(res, status, body, headers = {}) {
  res.writeHead(status, { 'Content-Type': 'application/json', ...headers });
  res.end(JSON.stringify(body));
}

function redirect(res, location, headers = {}) {
  res.writeHead(302, { Location: location, ...headers });
  res.end();
}

function sign(value, secret) {
  return crypto.createHmac('sha256', secret).update(value).digest('base64url');
}

function timingSafeEqual(left, right) {
  const leftBuffer = Buffer.from(left);
  const rightBuffer = Buffer.from(right);
  return leftBuffer.length === rightBuffer.length && crypto.timingSafeEqual(leftBuffer, rightBuffer);
}

function createSessionCookie(user, config) {
  const payload = Buffer.from(JSON.stringify({
    user,
    exp: Math.floor(Date.now() / 1000) + SESSION_MAX_AGE_SECONDS
  })).toString('base64url');
  return `${payload}.${sign(payload, config.sessionSecret)}`;
}

function readSession(req, config = getConfig()) {
  const value = parseCookies(req)[SESSION_COOKIE];
  if (!value) return null;

  const separator = value.lastIndexOf('.');
  if (separator === -1) return null;

  const payload = value.slice(0, separator);
  const signature = value.slice(separator + 1);
  if (!timingSafeEqual(signature, sign(payload, config.sessionSecret))) return null;

  try {
    const session = JSON.parse(Buffer.from(payload, 'base64url').toString('utf-8'));
    if (!session.exp || session.exp < Math.floor(Date.now() / 1000)) return null;
    return session.user || null;
  } catch {
    return null;
  }
}

function requireGoogleConfig(config, res) {
  if (config.clientId && config.clientSecret) return true;
  sendJson(res, 500, {
    error: 'google oauth is not configured',
    requiredEnv: ['GOOGLE_OAUTH_CLIENT_ID', 'GOOGLE_OAUTH_CLIENT_SECRET']
  });
  return false;
}

function googleLoginHandler(req, res, config = getConfig()) {
  if (!requireGoogleConfig(config, res)) return;

  const state = crypto.randomBytes(32).toString('base64url');
  const authUrl = new URL(GOOGLE_AUTH_URL);
  authUrl.searchParams.set('client_id', config.clientId);
  authUrl.searchParams.set('redirect_uri', getRedirectUri(req, config));
  authUrl.searchParams.set('response_type', 'code');
  authUrl.searchParams.set('scope', config.scopes.join(' '));
  authUrl.searchParams.set('state', state);
  authUrl.searchParams.set('include_granted_scopes', 'true');

  redirect(res, authUrl.toString(), {
    'Set-Cookie': cookie(STATE_COOKIE, state, req, { maxAge: STATE_MAX_AGE_SECONDS })
  });
}

async function googleCallbackHandler(req, res, config = getConfig()) {
  if (!requireGoogleConfig(config, res)) return;

  const url = getRequestUrl(req);
  const error = url.searchParams.get('error');
  if (error) {
    sendJson(res, 400, { error });
    return;
  }

  const code = url.searchParams.get('code');
  const state = url.searchParams.get('state');
  const expectedState = parseCookies(req)[STATE_COOKIE];
  if (!code) {
    sendJson(res, 400, { error: "missing 'code' query parameter" });
    return;
  }
  if (!state || !expectedState || !timingSafeEqual(state, expectedState)) {
    sendJson(res, 400, { error: 'invalid oauth state' });
    return;
  }

  const tokens = await exchangeCodeForTokens(code, getRedirectUri(req, config), config);
  const user = await fetchGoogleUser(tokens.access_token);
  const sessionValue = createSessionCookie(normalizeGoogleUser(user), config);

  redirect(res, config.postLoginRedirect, {
    'Set-Cookie': [
      clearCookie(STATE_COOKIE, req),
      cookie(SESSION_COOKIE, sessionValue, req, { maxAge: SESSION_MAX_AGE_SECONDS })
    ]
  });
}

async function exchangeCodeForTokens(code, redirectUri, config) {
  const response = await fetch(GOOGLE_TOKEN_URL, {
    method: 'POST',
    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
    body: new URLSearchParams({
      code,
      client_id: config.clientId,
      client_secret: config.clientSecret,
      redirect_uri: redirectUri,
      grant_type: 'authorization_code'
    })
  });

  const body = await response.json();
  if (!response.ok) {
    throw new Error(`Google token exchange failed: ${body.error || response.status}`);
  }
  if (!body.access_token) {
    throw new Error('Google token exchange did not return an access token');
  }
  return body;
}

async function fetchGoogleUser(accessToken) {
  const response = await fetch(GOOGLE_USERINFO_URL, {
    headers: { Authorization: `Bearer ${accessToken}` }
  });
  const body = await response.json();
  if (!response.ok) {
    throw new Error(`Google userinfo failed: ${body.error || response.status}`);
  }
  return body;
}

function normalizeGoogleUser(user) {
  return {
    id: user.sub,
    email: user.email,
    name: user.name,
    picture: user.picture
  };
}

function meHandler(req, res, config = getConfig()) {
  const user = readSession(req, config);
  if (!user) {
    sendJson(res, 401, { authenticated: false });
    return;
  }
  sendJson(res, 200, { authenticated: true, user });
}

function logoutHandler(req, res) {
  sendJson(res, 200, { ok: true }, {
    'Set-Cookie': clearCookie(SESSION_COOKIE, req)
  });
}

export {
  googleLoginHandler,
  googleCallbackHandler,
  logoutHandler,
  meHandler,
  readSession
};
