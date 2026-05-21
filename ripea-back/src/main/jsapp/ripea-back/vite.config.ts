/// <reference types="vitest" />
import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import tsconfigPaths from 'vite-tsconfig-paths';
import { resolve } from 'path'

// @mui/system v6 has no "exports" field, so Node.js ESM fails on directory imports
// (e.g. @mui/x-date-pickers and @mui/x-data-grid-pro import @mui/system/createStyled
// and @mui/system/RtlProvider as bare sub-paths that resolve to directories).
// The alias below redirects any @mui/system/<subpath> to its index.js so that
// both the dev build and the Vitest test environment resolve them correctly.
const muiSystemAbs = resolve('./node_modules/@mui/system').replace(/\\/g, '/')

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    react(),
    tsconfigPaths(),
  ],
  resolve: {
    alias: [
      { find: /^@mui\/system\/([^/]+)$/, replacement: `${muiSystemAbs}/$1/index.js` },
    ],
  },
  server: {
    proxy: {
      '/ripeaback': 'http://localhost:8080',
    },
  },
  test: {
    globals: true,
    environment: 'jsdom',
    setupFiles: ['./vitest.setup.ts'],
    include: ['vitests/**/*.test.{ts,tsx}'],
    coverage: {
      provider: 'v8',
      reporter: ['text', 'json', 'html'],
      include: ['src/**/*.{ts,tsx}'],
      exclude: ['src/main.tsx', 'src/vite-env.d.ts'],
    },
  },
})
