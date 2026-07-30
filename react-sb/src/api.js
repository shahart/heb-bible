const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || '';
const TOKEN_KEY = 'hebBibleJwt';

function apiUrl(path) {
  return `${API_BASE_URL}${path}`;
}

export function getToken() {
  return window.localStorage.getItem(TOKEN_KEY);
}

export function setToken(token) {
  if (token) {
    window.localStorage.setItem(TOKEN_KEY, token);
  } else {
    window.localStorage.removeItem(TOKEN_KEY);
  }
}

function authHeaders(headers = {}) {
  const token = getToken();
  return token
    ? { ...headers, Authorization: `Bearer ${token}` }
    : headers;
}

async function readJson(response) {
  const contentType = response.headers.get('content-type') || '';
  if (!contentType.includes('application/json')) {
    return null;
  }
  return response.json();
}

export async function currentUser() {
  const response = await fetch(apiUrl('/user'), {
    headers: authHeaders(),
    credentials: 'include'
  });

  if (!response.ok) {
    throw new Error('Not signed in');
  }

  return response.json();
}

export async function authenticate(mode, email, password) {
  const response = await fetch(apiUrl(`/auth/${mode}`), {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ email, password }),
    credentials: 'include'
  });
  const data = await readJson(response);

  if (!response.ok) {
    throw new Error(data?.message || 'Authentication failed. Please try again.');
  }
  if (!data?.token) {
    throw new Error('The server returned an invalid authentication response.');
  }

  setToken(data.token);
  return data;
}

export async function countPsukim(name) {
  const response = await fetch(apiUrl('/psukim'), {
    method: 'POST',
    headers: authHeaders({ 'Content-Type': 'text/plain; charset=UTF-8' }),
    body: name,
    credentials: 'include'
  });
  const data = await readJson(response);

  if (!response.ok) {
    if (response.status === 401 || response.status === 403) {
      throw new Error('Please sign in before searching.');
    }
    throw new Error(data?.message || 'The verse count could not be loaded.');
  }
  if (!Array.isArray(data)) {
    throw new Error('The server returned an unexpected response.');
  }

  return data.length;
}

export async function signOut() {
  setToken(null);

  try {
    await fetch(apiUrl('/logout'), {
      method: 'POST',
      credentials: 'include'
    });
  } catch {
    // The local JWT is already removed, so the client is signed out.
  }
}

export function googleLoginUrl() {
  return import.meta.env.VITE_GOOGLE_LOGIN_URL || apiUrl('/oauth2/authorization/google');
}
