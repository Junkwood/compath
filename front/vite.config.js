import { defineConfig } from "vite";
import vue from "@vitejs/plugin-vue";

const server = "http://localhost:8080";

import ckeditor5 from "@ckeditor/vite-plugin-ckeditor5";
import { createRequire } from "node:module";
const require = createRequire(import.meta.url);

export default defineConfig({
  define: {
    "process.env": process.env,
  },
  plugins: [vue()],
  optimizeDeps: {
    exclude: ["@bryntum/gantt"],
  },
  build: {
    commonjsOptions: {
      transformMixedEsModules: true,
    },
  },
  server: {
    proxy: {
      "^/api": {
        target: server,
        changeOrigin: true,
        ws: true,
      },
    },
  },
});
