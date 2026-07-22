import { describe, it, afterEach } from 'node:test';
import assert from 'node:assert/strict';
import {
  googleCallbackHandler,
  googleLoginHandler,
  meHandler,
  readSession
} from '../auth.js';

const config = {
  clientId: 'client-id',
  clientSecret: 'client-secret',
  redirectUri: 'http://localhost:8080/auth/google/callback',
  scopes: ['openid', 'email', 'profile'],
  postLoginRedirect: '/after-login',
  sessionSecret: 'test-session-secret'
};
const originalFetch = globalThis.fetch;

afterEach(() => {
  globalThis.fetch = originalFetch;
});

describe('google oauth handlers', () => {
  it('redirects to Google with state and default OpenID scopes', () => {
    const req = makeRequest('/auth/google');
    const res = makeResponse();

    googleLoginHandler(req, res, config);

    assert.equal(res.statusCode, 302);
    const location = new URL(res.headers.Location);
    assert.equal(location.origin + location.pathname, 'https://accounts.google.com/o/oauth2/v2/auth');
    assert.equal(location.searchParams.get('client_id'), config.clientId);
    assert.equal(location.searchParams.get('redirect_uri'), config.redirectUri);
    assert.equal(location.searchParams.get('response_type'), 'code');
    assert.equal(location.searchParams.get('scope'), 'openid email profile');
    assert.ok(location.searchParams.get('state'));
    assert.match(res.headers['Set-Cookie'], /google_oauth_state=/);
  });

  it('exchanges an authorization code and creates a signed session cookie', async () => {
    const state = 'state-value';
    const req = makeRequest(`/auth/google/callback?code=auth-code&state=${state}`, {
      cookie: `google_oauth_state=${state}`
    });
    const res = makeResponse();
    const requests = [];
    globalThis.fetch = async (url, options) => {
      requests.push({ url, options });
      if (url === 'https://oauth2.googleapis.com/token') {
        return jsonResponse(200, { access_token: 'access-token' });
      }
      if (url === 'https://openidconnect.googleapis.com/v1/userinfo') {
        return jsonResponse(200, {
          sub: 'google-user-id',
          email: 'reader@example.com',
          name: 'Reader',
          picture: 'https://example.com/avatar.png'
        });
      }
      throw new Error(`unexpected fetch ${url}`);
    };

    await googleCallbackHandler(req, res, config);

    assert.equal(res.statusCode, 302);
    assert.equal(res.headers.Location, '/after-login');
    assert.equal(requests.length, 2);
    assert.equal(requests[1].options.headers.Authorization, 'Bearer access-token');

    const sessionCookie = res.headers['Set-Cookie']
      .find((value) => value.startsWith('heb_bible_session='))
      .match(/heb_bible_session=([^;]+)/)[1];
    const user = readSession(makeRequest('/auth/me', { cookie: `heb_bible_session=${sessionCookie}` }), config);
    assert.deepEqual(user, {
      id: 'google-user-id',
      email: 'reader@example.com',
      name: 'Reader',
      picture: 'https://example.com/avatar.png'
    });
  });

  it('rejects callback requests with an invalid state', async () => {
    const req = makeRequest('/auth/google/callback?code=auth-code&state=actual', {
      cookie: 'google_oauth_state=expected'
    });
    const res = makeResponse();

    await googleCallbackHandler(req, res, config);

    assert.equal(res.statusCode, 400);
    assert.deepEqual(JSON.parse(res.body), { error: 'invalid oauth state' });
  });

  it('reports an unauthenticated user from /auth/me', () => {
    const req = makeRequest('/auth/me');
    const res = makeResponse();

    meHandler(req, res, config);

    assert.equal(res.statusCode, 401);
    assert.deepEqual(JSON.parse(res.body), { authenticated: false });
  });
});

function makeRequest(url, headers = {}) {
  return {
    url,
    headers: {
      host: 'localhost:8080',
      ...headers
    },
    socket: {}
  };
}

function makeResponse() {
  return {
    statusCode: null,
    headers: null,
    body: '',
    writeHead(statusCode, headers = {}) {
      this.statusCode = statusCode;
      this.headers = headers;
    },
    end(body = '') {
      this.body = body;
    }
  };
}

function jsonResponse(status, body) {
  return {
    ok: status >= 200 && status < 300,
    status,
    async json() {
      return body;
    }
  };
}
