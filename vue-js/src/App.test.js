import { mount } from '@vue/test-utils';
import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest';
import App from './App.vue';

beforeEach(() => {
  vi.stubGlobal('fetch', vi.fn());
});

afterEach(() => {
  vi.unstubAllGlobals();
});

describe('App', () => {
  it('shows Google sign-in when no authenticated user exists', async () => {
    fetch.mockResolvedValueOnce(jsonResponse(401, { authenticated: false }));

    const wrapper = mount(App);
    await flushPromises();

    expect(wrapper.text()).toContain('Sign in with Google');
    expect(wrapper.text()).not.toContain('Sign out');
    expect(fetch).toHaveBeenCalledWith('/auth/me', { credentials: 'include' });
  });

  it('shows the authenticated user and signs out', async () => {
    fetch
      .mockResolvedValueOnce(jsonResponse(200, {
        authenticated: true,
        user: {
          id: 'google-user-id',
          email: 'reader@example.com',
          name: 'Reader',
          picture: 'https://example.com/avatar.png'
        }
      }))
      .mockResolvedValueOnce(jsonResponse(200, { ok: true }));

    const wrapper = mount(App);
    await flushPromises();

    expect(wrapper.text()).toContain('Reader');
    expect(wrapper.text()).toContain('reader@example.com');

    await wrapper.find('button.secondary-button').trigger('click');
    await flushPromises();

    expect(fetch).toHaveBeenLastCalledWith('/auth/logout', {
      method: 'POST',
      credentials: 'include'
    });
    expect(wrapper.text()).toContain('Sign in with Google');
  });

  it('validates the pasuk search input before calling the backend', async () => {
    fetch.mockResolvedValueOnce(jsonResponse(401, { authenticated: false }));
    const wrapper = mount(App);
    await flushPromises();

    await wrapper.find('input#name').setValue('א');
    await wrapper.find('form').trigger('submit');

    expect(wrapper.text()).toContain('Enter at least two characters.');
    expect(fetch).toHaveBeenCalledTimes(1);
  });

  it('searches psukim with name and containsName query parameters', async () => {
    fetch
      .mockResolvedValueOnce(jsonResponse(401, { authenticated: false }))
      .mockResolvedValueOnce(jsonResponse(200, { count: 7 }));

    const wrapper = mount(App);
    await flushPromises();

    await wrapper.find('input#name').setValue('משה');
    await wrapper.find('input[type="checkbox"]').setValue(true);
    await wrapper.find('form').trigger('submit');
    await flushPromises();

    expect(fetch).toHaveBeenCalledTimes(2);
    const [url, options] = fetch.mock.calls[1];
    expect(url).toBeInstanceOf(URL);
    expect(url.pathname).toBe('/psukim');
    expect(url.searchParams.get('name')).toBe('משה');
    expect(url.searchParams.get('containsName')).toBe('true');
    expect(options).toEqual({ credentials: 'include' });
    expect(wrapper.text()).toContain('7 matching psukim');
  });

  it('shows a search error when the backend request fails', async () => {
    fetch
      .mockResolvedValueOnce(jsonResponse(401, { authenticated: false }))
      .mockResolvedValueOnce(jsonResponse(500, { error: 'server error' }));

    const wrapper = mount(App);
    await flushPromises();

    await wrapper.find('input#name').setValue('דוד');
    await wrapper.find('form').trigger('submit');
    await flushPromises();

    expect(wrapper.text()).toContain('Could not search psukim.');
  });
});

function jsonResponse(status, body) {
  return {
    ok: status >= 200 && status < 300,
    status,
    async json() {
      return body;
    }
  };
}

function flushPromises() {
  return new Promise((resolve) => setTimeout(resolve, 0));
}
