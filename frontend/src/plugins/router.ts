// Composables
import { createRouter, createWebHistory } from "vue-router";
import { useUserStore } from "@/store/user";
import { useStompStore } from "@/store/stomp";

const routes = [
  {
    path: "/login",
    name: "Login",
    component: () => import("@/views/Login.vue"),
  },
  {
    path: "/",
    component: () => import("@/layouts/DefaultLayout.vue"),
    redirect: { path: "login" },
    meta: { auth: true },
    children: [
      {
        path: "games",
        name: "Games",
        component: () => import("@/views/Games.vue"),
      },
      {
        path: "games/:gameTitle",
        name: "GameRooms",
        component: () => import("@/views/GameRooms.vue"),
      },
      {
        path: "ranking",
        name: "Ranking",
        component: () => import("@/views/Ranking.vue"),
      },
      {
        path: "users/:userId",
        name: "UserInfo",
        component: () => import("@/views/UserInfo.vue"),
      },
    ],
  },
  {
    path: "/games/:gameTitle/:gameRoomId",
    name: "GameRoom",
    component: () => import("@/views/GameRoom.vue"),
    meta: { auth: true },
  },
  {
    path: "/already-login",
    name: "AlreadyLogin",
    component: () => import("@/views/AlreadyLogin.vue"),
  },
  {
    path: "/:catchAll(.*)",
    name: "NotFoundPage",
    component: () => import("@/views/NotFound.vue"),
  },
];

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes,
});

router.beforeEach(async (to) => {
  const userStore = useUserStore();
  const stompStore = useStompStore();

  if (to.meta.auth && !stompStore.client.connected) {
    stompStore.connect();
  }

  if (to.meta.auth && !userStore.user) {
    await userStore.setUser();
  }
});

export default router;
