import { createApp } from "vue"
import App from "./App.vue"
import router from "./router";
import {useStore} from "./store";
import {createPinia} from "pinia";
import "./assets/style/index.css"

router.beforeEach((to, from, next) => {
    const store = useStore();
    const authRequired = !store.publicRoutes.includes(<string>to.name);
    if (authRequired && !store.isLoggedIn) {
        store.nextAfterLogin(to.path);
        router.push("/").then(() => {});
        return;
    }
    next();
});

createApp(App)
    .use(createPinia())
    .use(router)
    .mount('#app')
