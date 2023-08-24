<template>
  <v-btn
    v-if="isHost"
    class="w-100 h-100 bg-green text-h6 font-weight-black"
    @click="clickStartGame"
    :disabled="userStore.isOnGame"
    >게임시작
  </v-btn>
  <v-btn
    v-else
    class="w-100 h-100 text-h6 font-weight-black"
    :class="isReady"
    :disabled="userStore.isOnGame"
    @click="clickReadyToggle"
    >{{ readyButtonText }}
  </v-btn>
</template>

<script setup lang="ts">
import { useGameRoomStore } from "@/store/gameRoom";
import { useUserStore } from "@/store/user";
import { computed } from "vue";
import { useStompStore } from "@/store/stomp";
import { useRoute } from "vue-router";
import { startGame } from "@/api/game";

const { gameRoom } = useGameRoomStore();
const userStore = useUserStore();
const { client } = useStompStore();
const { gameTitle, gameRoomId } = useRoute().params as {
  gameTitle: string;
  gameRoomId: string;
};

const isHost = computed(() => {
  return gameRoom?.hostId === userStore.user?.id;
});
const isReady = computed(() => {
  return !userStore.isReady ? "bg-green" : "bg-yellow";
});
const readyButtonText = computed(() => {
  return !userStore.isReady ? "게임준비" : "준비취소";
});

const clickReadyToggle = () => {
  client.publish({
    destination: `/app/ready-toggle/${gameTitle}/${gameRoomId}`,
    body: JSON.stringify(userStore.user),
  });
};

const clickStartGame = async () => {
  try {
    await startGame(gameTitle, gameRoomId);
  } catch (e) {
    /* empty */
  }
};
</script>

<style scoped></style>
