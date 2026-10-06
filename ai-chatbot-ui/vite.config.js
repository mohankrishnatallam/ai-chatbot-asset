import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

const apiTarget = 'http://127.0.0.1:8082'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  optimizeDeps: {
    exclude: ['@mongodb-js/charts-embed-dom', 'bson'],
    esbuildOptions: {
      target: 'esnext',
    },
  },
  build: {
    target: 'esnext',
  },
  server: {
    proxy: {
      '/assistant': {
        target: apiTarget,
        changeOrigin: true,
      },
      '/auth': {
        target: apiTarget,
        changeOrigin: true,
      },
      '/prompts': {
        target: apiTarget,
        changeOrigin: true,
      },
      '/actuator': {
        target: apiTarget,
        changeOrigin: true,
      },
      '/charts': {
        target: apiTarget,
        changeOrigin: true,
      },
    },
  },
})
