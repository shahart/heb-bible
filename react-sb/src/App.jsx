import { useEffect, useRef, useState } from 'react';
import {
  authenticate,
  countPsukim,
  currentUser,
  googleLoginUrl,
  setToken,
  signOut
} from './api.js';

function EmailAuthDialog({ onAuthenticated, onClose }) {
  const [mode, setMode] = useState('login');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [submitting, setSubmitting] = useState(false);
  const emailInput = useRef(null);

  useEffect(() => {
    emailInput.current?.focus();

    function closeOnEscape(event) {
      if (event.key === 'Escape') onClose();
    }

    window.addEventListener('keydown', closeOnEscape);
    return () => window.removeEventListener('keydown', closeOnEscape);
  }, [onClose]);

  async function handleSubmit(event) {
    event.preventDefault();
    setError('');
    setSubmitting(true);

    try {
      const user = await authenticate(mode, email.trim(), password);
      onAuthenticated(user);
    } catch (requestError) {
      setError(requestError.message);
    } finally {
      setSubmitting(false);
    }
  }

  function changeMode(nextMode) {
    setMode(nextMode);
    setError('');
  }

  return (
    <div className="dialog-backdrop" onMouseDown={onClose}>
      <section
        aria-labelledby="auth-title"
        aria-modal="true"
        className="auth-dialog"
        onMouseDown={(event) => event.stopPropagation()}
        role="dialog"
      >
        <button
          aria-label="Close email sign in"
          className="dialog-close"
          onClick={onClose}
          type="button"
        >
          ×
        </button>

        <p className="eyebrow">Your account</p>
        <h2 id="auth-title">
          {mode === 'login' ? 'Welcome back' : 'Create an account'}
        </h2>
        <p className="dialog-copy">
          {mode === 'login'
            ? 'Sign in with the email you registered.'
            : 'Use an email and a password of at least 8 characters.'}
        </p>

        <div className="mode-switch" aria-label="Email authentication option">
          <button
            aria-pressed={mode === 'login'}
            onClick={() => changeMode('login')}
            type="button"
          >
            Sign in
          </button>
          <button
            aria-pressed={mode === 'signup'}
            onClick={() => changeMode('signup')}
            type="button"
          >
            Register
          </button>
        </div>

        <form className="auth-form" onSubmit={handleSubmit}>
          <label htmlFor="email">Email</label>
          <input
            autoComplete="email"
            id="email"
            onChange={(event) => setEmail(event.target.value)}
            ref={emailInput}
            required
            type="email"
            value={email}
          />

          <label htmlFor="password">Password</label>
          <input
            autoComplete={mode === 'login' ? 'current-password' : 'new-password'}
            id="password"
            maxLength="100"
            minLength="8"
            onChange={(event) => setPassword(event.target.value)}
            required
            type="password"
            value={password}
          />

          {error && <p className="form-error" role="alert">{error}</p>}

          <button className="primary-button auth-submit" disabled={submitting} type="submit">
            {submitting
              ? 'Please wait…'
              : mode === 'login'
                ? 'Sign in'
                : 'Create account'}
          </button>
        </form>
      </section>
    </div>
  );
}

export default function App() {
  const [user, setUser] = useState(null);
  const [checkingAuth, setCheckingAuth] = useState(true);
  const [showEmailAuth, setShowEmailAuth] = useState(false);
  const [name, setName] = useState('');
  const [count, setCount] = useState(null);
  const [error, setError] = useState('');
  const [searching, setSearching] = useState(false);

  useEffect(() => {
    let active = true;

    currentUser()
      .then((signedInUser) => {
        if (active) setUser(signedInUser);
      })
      .catch(() => {
        setToken(null);
      })
      .finally(() => {
        if (active) setCheckingAuth(false);
      });

    return () => {
      active = false;
    };
  }, []);

  async function handleSearch(event) {
    event.preventDefault();
    const trimmedName = name.trim();
    setCount(null);
    setError('');

    if (trimmedName.length < 2) {
      setError('Enter at least two characters.');
      return;
    }
    if (!user) {
      setError('Please sign in before searching.');
      return;
    }

    setSearching(true);
    try {
      setCount(await countPsukim(trimmedName));
    } catch (requestError) {
      setError(requestError.message);
    } finally {
      setSearching(false);
    }
  }

  async function handleSignOut() {
    await signOut();
    setUser(null);
    setCount(null);
    setError('');
  }

  const userLabel = user?.email || user?.name;

  return (
    <main className="page-shell">
      <div className="ambient ambient-one" />
      <div className="ambient ambient-two" />

      <section className="app-card">
        <header className="topbar">
          <a className="brand" href="/" aria-label="Heb Bible home">
            <span className="brand-mark" aria-hidden="true">א</span>
            <span>Heb Bible</span>
          </a>

          {!checkingAuth && user && (
            <div className="signed-in">
              <span title={userLabel}>{userLabel}</span>
              <button onClick={handleSignOut} type="button">Sign out</button>
            </div>
          )}
        </header>

        <div className="hero">
          <div className="ornament" aria-hidden="true">
            <span />
            <b>׃</b>
            <span />
          </div>
          <p className="eyebrow">A name in the text</p>
          <h1>Count the verses.</h1>
          <p className="intro">
            Enter a Hebrew name to discover how many biblical verses match it.
          </p>

          {!checkingAuth && !user && (
            <div className="auth-actions" aria-label="Sign in options">
              <a className="google-button" href={googleLoginUrl()}>
                <span className="google-mark" aria-hidden="true">G</span>
                Continue with Google
              </a>
              <button
                className="email-button"
                onClick={() => setShowEmailAuth(true)}
                type="button"
              >
                Sign in or register
              </button>
            </div>
          )}

          {checkingAuth && <p className="checking-auth">Checking your account…</p>}

          <form className="search-form" onSubmit={handleSearch}>
            <label htmlFor="name">Hebrew name</label>
            <div className="search-row">
              <input
                autoComplete="name"
                dir="auto"
                id="name"
                onChange={(event) => setName(event.target.value)}
                placeholder="למשל, שחר"
                type="text"
                value={name}
              />
              <button className="primary-button" disabled={searching} type="submit">
                {searching ? 'Counting…' : 'Count verses'}
              </button>
            </div>
          </form>

          <div className="result" aria-live="polite">
            {error && <p className="result-error" role="alert">{error}</p>}
            {count !== null && !error && (
              <>
                <strong>{count.toLocaleString()}</strong>
                <span>{count === 1 ? 'matching verse' : 'matching verses'}</span>
              </>
            )}
            {count === null && !error && (
              <p className="result-hint">
                {user ? 'Your result will appear here.' : 'Sign in, then enter a name to begin.'}
              </p>
            )}
          </div>
        </div>

        <footer>
          <span aria-hidden="true">✦</span>
          <p>Search the Hebrew Bible by name</p>
          <span aria-hidden="true">✦</span>
        </footer>
      </section>

      {showEmailAuth && (
        <EmailAuthDialog
          onAuthenticated={(signedInUser) => {
            setUser(signedInUser);
            setShowEmailAuth(false);
            setError('');
          }}
          onClose={() => setShowEmailAuth(false)}
        />
      )}
    </main>
  );
}
