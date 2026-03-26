import { createApp } from "vue";
import router from "./router/router";
import App from "./App.vue";
import ElementPlus from "element-plus";
import "element-plus/dist/index.css";
import { createVfm } from "vue-final-modal";
import "vue-final-modal/style.css";
import { createPinia } from 'pinia'
import "./css/style.css";
import piniaPluginPersistedstate from 'pinia-plugin-persistedstate'

const app = createApp(App);
const vfm = createVfm();

app.use(ElementPlus);
app.use(router);
app.use(vfm);
const pinia = createPinia()
pinia.use(piniaPluginPersistedstate)
app.use(pinia)
app.mount("#app");
