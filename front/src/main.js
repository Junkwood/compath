import { createApp } from "vue";
import router from "./router/router";
import App from "./App.vue";
import ElementPlus from "element-plus";
import "element-plus/dist/index.css";
import { createVfm } from "vue-final-modal";
import "vue-final-modal/style.css";
import "./css/style.css";

const app = createApp(App);
const vfm = createVfm();

app.use(ElementPlus);
app.use(router);
app.use(vfm);
app.mount("#app");
