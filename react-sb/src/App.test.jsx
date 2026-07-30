import { fireEvent, render, screen, waitFor } from '@testing-library/react';
import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest';
import App from './App.jsx';

function jsonResponse(data, status = 200) {
  return Promise.resolve({
    ok: status >= 200 && status < 300,
    status,
    headers: new Headers({ 'content-type': 'application/json' }),
    json: () => Promise.resolve(data)
  });
}

describe('App', () => {
  beforeEach(() => {
    window.localStorage.clear();
    vi.stubGlobal('fetch', vi.fn(() => jsonResponse({}, 401)));
  });

  afterEach(() => {
    vi.unstubAllGlobals();
  });

  it('shows the two requested sign-in options', async () => {
    render(<App />);

    expect(await screen.findByRole('link', { name: /continue with google/i })).toBeVisible();
    expect(screen.getByRole('button', { name: /sign in or register/i })).toBeVisible();
  });

  it('opens registration and stores the returned token', async () => {
    fetch
      .mockImplementationOnce(() => jsonResponse({}, 401))
      .mockImplementationOnce(() => jsonResponse({
        token: 'jwt-value',
        tokenType: 'Bearer',
        email: 'reader@example.com',
        name: 'reader'
      }, 201));

    render(<App />);
    fireEvent.click(await screen.findByRole('button', { name: /sign in or register/i }));
    fireEvent.click(screen.getByRole('button', { name: 'Register' }));
    fireEvent.change(screen.getByLabelText('Email'), {
      target: { value: 'reader@example.com' }
    });
    fireEvent.change(screen.getByLabelText('Password'), {
      target: { value: 'long-enough-password' }
    });
    fireEvent.click(screen.getByRole('button', { name: 'Create account' }));

    await waitFor(() => {
      expect(window.localStorage.getItem('hebBibleJwt')).toBe('jwt-value');
    });
    expect(screen.getByText('reader@example.com')).toBeVisible();
  });

  it('posts the name and displays the array length', async () => {
    window.localStorage.setItem('hebBibleJwt', 'jwt-value');
    fetch
      .mockImplementationOnce(() => jsonResponse({
        email: 'reader@example.com',
        name: 'reader'
      }))
      .mockImplementationOnce(() => jsonResponse([{ pasuk: 1 }, { pasuk: 2 }]));

    render(<App />);
    await screen.findByText('reader@example.com');

    fireEvent.change(screen.getByLabelText('Hebrew name'), {
      target: { value: 'שחר' }
    });
    fireEvent.click(screen.getByRole('button', { name: 'Count verses' }));

    expect(await screen.findByText('2')).toBeVisible();
    expect(screen.getByText('matching verses')).toBeVisible();
    expect(fetch).toHaveBeenLastCalledWith('/psukim', expect.objectContaining({
      method: 'POST',
      body: 'שחר'
    }));
  });
});
