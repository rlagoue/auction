import {createRouter, createWebHistory} from "vue-router";
import Login from "../views/Login.vue";

const routes = [
    {
        path: "/",
        name: "login",
        component: Login
    },
    {
      path: "/desktop",
      name: "desktop",
      component: () => import("../views/Desktop.vue"),
      children: [
          {
              path: "/items-list",
              name: "itemsList",
              component: () => import("../views/item/List.vue"),
          },
      ],
    },
];


const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes
});

export default router;