import { defineConfig } from 'vite';
import vue from '@vitejs/plugin-vue';

const apiTarget = process.env.VITE_API_PROXY_TARGET || 'http://localhost:8080';

export default defineConfig({
  plugins: [vue()],
  test: {
    environment: 'jsdom',
    globals: true
  },
  server: {
    port: 5173,
    proxy: {
      '/auth': {
        target: apiTarget,
        changeOrigin: true
      },
      '/psukim': {
        target: apiTarget,
        changeOrigin: true
      }
    }
  }
});
