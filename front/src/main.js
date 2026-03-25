import { createApp } from "vue";
import router from "./router/router";
import App from "./App.vue";
import ElementPlus from "element-plus";
import "element-plus/dist/index.css";

import "./css/style.css";

const app = createApp(App);
app.use(ElementPlus);
app.use(router);
app.mount("#app");
