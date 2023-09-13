import { defineStore } from "pinia";
import { computed, ref } from "vue";
import { getSelfUserInfo } from "@/api/user";
import { User, UserLocation } from "@/types/user";

export const useUserStore = defineStore("user", () => {
  const user = ref<User | null>(null);
  const location = ref<UserLocation | null>(null);
  const ready = ref<boolean>(false);

  async function setUser() {
    try {
      user.value = await getSelfUserInfo();
      location.value = "LOBBY";
    } catch (e) {
      /* empty */
    }
  }

  function startGame() {
    location.value = "ON_GAME";
  }

  function onReady() {
    ready.value = true;
  }

  function offReady() {
    ready.value = false;
  }

  function finishGame() {
    location.value = "GAME_ROOM";
    offReady();
  }

  function leaveGameRoom() {
    location.value = "LOBBY";
    offReady();
  }

  function joinGameRoom() {
    location.value = "GAME_ROOM";
    ready.value = false;
  }

  const isOnGame = computed(() => location.value === "ON_GAME");
  const isReady = computed(() => ready.value);
  const isGameRoom = computed(
    () => location.value === "GAME_ROOM" || location.value === "ON_GAME",
  );

  return {
    user,
    location,
    ready,
    setUser,
    startGame,
    finishGame,
    leaveGameRoom,
    onReady,
    offReady,
    joinGameRoom,
    isOnGame,
    isReady,
    isGameRoom,
  };
});
