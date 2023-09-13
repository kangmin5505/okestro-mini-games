<template>
  <v-navigation-drawer width="350" color="tertiary" permanent>
    <v-container class="h-100 d-flex flex-column pa-0">
      <game-room-title class="game-room-title" />
      <game-room-user-profile class="users-container" />

      <game-room-chat class="chat-container" />

      <game-room-buttons class="btn-container" />
    </v-container>
  </v-navigation-drawer>
</template>

<script setup lang="ts">
import GameRoomChat from "@/components/gameRoom/GameRoomChat.vue";
import GameRoomUserProfile from "@/components/gameRoom/GameRoomUserProfile.vue";
import GameRoomButtons from "@/components/gameRoom/GameRoomButtons.vue";
import GameRoomTitle from "@/components/gameRoom/GameRoomTitle.vue";
import { useStompStore } from "@/store/stomp";
import { useRoute } from "vue-router";
import { useUserStore } from "@/store/user";

const { client } = useStompStore();
const userStore = useUserStore();
const { gameTitle, gameRoomId } = useRoute().params;

// let readyToggleSubscription: StompSubscription;
// onMounted(() => {
//   readyToggleSubscription = client.subscribe(
//     `/topic/ready-toggle/${gameTitle}/${gameRoomId}`,
//     (_) => {
//       userStore.isReady ? userStore.offReady() : userStore.onReady();
//     },
//   );
// });
//
// onBeforeUnmount(() => {
//   readyToggleSubscription.unsubscribe();
// });
</script>

<style scoped lang="scss">
.game-room-title {
  height: 7%;
}

.users-container {
  height: 14%;
}

.chat-container {
  height: 64%;
}

.btn-container {
  height: 15%;
}
</style>
