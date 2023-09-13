/**
 * plugins/router.ts
 *
 * Automatically included in `./src/main.ts`
 */

// Plugins
import { loadFonts } from "./webfontloader";
import vuetify from "./vuetify";
import pinia from "../store";
import router from "./router";
import type { App } from "vue";
import axios from "axios";
import VueAxios from "vue-axios";

export function registerPlugins(app: App) {
  loadFonts();
  app.use(vuetify).use(router).use(pinia).use(VueAxios, axios);
  app.provide("axios", app.config.globalProperties.axios);
}
