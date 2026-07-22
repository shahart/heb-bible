<script setup>
import { computed, onMounted, ref } from 'vue';

const apiBase = import.meta.env.VITE_API_BASE_URL || '';

const authLoading = ref(true);
const authError = ref('');
const user = ref(null);
const name = ref('');
const containsName = ref(false);
const searchLoading = ref(false);
const searchError = ref('');
const count = ref(null);

const isSignedIn = computed(() => Boolean(user.value));
const displayName = computed(() => user.value?.name || user.value?.email || 'Signed in');

onMounted(() => {
  loadCurrentUser();
});

async function loadCurrentUser() {
  authLoading.value = true;
  authError.value = '';

  try {
    const response = await fetch(`${apiBase}/auth/me`, {
      credentials: 'include'
    });
    const data = await response.json();
    user.value = response.ok && data.authenticated ? data.user : null;
  } catch (err) {
    authError.value = 'Could not load sign-in state.';
    user.value = null;
  } finally {
    authLoading.value = false;
  }
}

function signInWithGoogle() {
  window.location.href = `${apiBase}/auth/google`;
}

async function logout() {
  authLoading.value = true;
  authError.value = '';

  try {
    const response = await fetch(`${apiBase}/auth/logout`, {
      method: 'POST',
      credentials: 'include'
    });
    if (!response.ok) throw new Error(`HTTP ${response.status}`);
    user.value = null;
  } catch (err) {
    authError.value = 'Could not sign out.';
  } finally {
    authLoading.value = false;
  }
}

async function findPasuk() {
  const trimmedName = name.value.trim();
  count.value = null;
  searchError.value = '';

  if (trimmedName.length <= 1) {
    searchError.value = 'Enter at least two characters.';
    return;
  }

  searchLoading.value = true;
  try {
    const url = new URL(`${apiBase}/psukim`, window.location.origin);
    url.searchParams.set('name', trimmedName);
    url.searchParams.set('containsName', String(containsName.value));

    const response = await fetch(url, {
      credentials: 'include'
    });
    const data = await response.json();
    if (!response.ok) throw new Error(data.error || `HTTP ${response.status}`);
    count.value = data.count;
  } catch (err) {
    searchError.value = 'Could not search psukim.';
  } finally {
    searchLoading.value = false;
  }
}
</script>

<template>
  <main class="app-shell">
    <section class="topbar" aria-label="Account">
      <div>
        <p class="eyebrow">Heb Bible</p>
        <h1>Pasuk Finder</h1>
      </div>

      <div class="account">
        <div v-if="authLoading" class="status-text">Checking account...</div>
        <template v-else-if="isSignedIn">
          <img v-if="user.picture" class="avatar" :src="user.picture" alt="">
          <div class="account-name">
            <span>{{ displayName }}</span>
            <small v-if="user.email">{{ user.email }}</small>
          </div>
          <button class="secondary-button" type="button" @click="logout">Sign out</button>
        </template>
        <button v-else class="google-button" type="button" @click="signInWithGoogle">
          <span class="google-mark" aria-hidden="true">G</span>
          Sign in with Google
        </button>
      </div>
    </section>

    <p v-if="authError" class="error-message">{{ authError }}</p>

    <section class="tool-panel" aria-label="Pasuk search">
      <form class="search-form" @submit.prevent="findPasuk">
        <label for="name">Name</label>
        <div class="input-row">
          <input
            id="name"
            v-model="name"
            type="text"
            autocomplete="name"
            dir="auto"
            placeholder="Enter a Hebrew name"
          >
          <button type="submit" :disabled="searchLoading">
            {{ searchLoading ? 'Searching...' : 'Find Pasuk' }}
          </button>
        </div>

        <label class="checkbox-row">
          <input v-model="containsName" type="checkbox">
          <span>Match verses containing the full name</span>
        </label>
      </form>

      <div class="result-area" aria-live="polite">
        <p v-if="searchError" class="error-message">{{ searchError }}</p>
        <p v-else-if="count !== null" class="result-count">
          {{ count }} matching psukim
        </p>
        <p v-else class="muted">Search results will appear here.</p>
      </div>
    </section>
  </main>
</template>
