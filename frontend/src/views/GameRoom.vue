<template>
  <game-room-drawer />
  <v-main class="bg-primary">
    <card-matching-board v-if="gameTitle == 'card-matching'" />
    <gomoku-board v-else-if="gameTitle == 'gomoku'" />
    <pong-board v-else-if="gameTitle == 'pong'" />
  </v-main>
</template>

<script setup lang="ts">
import GameRoomDrawer from "@/components/gameRoom/GameRoomDrawer.vue";
import { useStompStore } from "@/store/stomp";
import { onBeforeUnmount, onMounted } from "vue";
import { StompSubscription } from "@stomp/stompjs";
import CardMatchingBoard from "@/components/game/cardMatching/CardMatchingBoard.vue";
import GomokuBoard from "@/components/game/gomoku/GomokuBoard.vue";
import PongBoard from "@/components/game/pong/PongBoard.vue";
import { useGameRoomStore } from "@/store/gameRoom";
import { onBeforeRouteLeave, useRoute, useRouter } from "vue-router";
import { useUserStore } from "@/store/user";
import { alertBeforeUnload, deleteAlertBeforeUnload } from "@/utils/utils";
import { RoomMaintainPayload } from "@/types/game";
import { User } from "@/types/user";
import { leaveGameRoom } from "@/api/game";

const { client } = useStompStore();
const gameRoomStore = useGameRoomStore();
const userStore = useUserStore();
const router = useRouter();
const route = useRoute();
const { gameTitle, gameRoomId } = route.params as {
  gameTitle: string;
  gameRoomId: string;
};

let roomMaintainSubscription: StompSubscription;
onMounted(() => {
  roomMaintainSubscription = client.subscribe(
    `/topic/room-maintain/${gameTitle}/${gameRoomId}`,
    (payload) => {
      const message = JSON.parse(payload.body) as RoomMaintainPayload;

      switch (message.type) {
        case "JOIN": {
          const content: User = message.content;
          gameRoomStore.joinPlayer(content);
          break;
        }
        case "LEAVE": {
          gameRoomStore.leavePlayer();
          break;
        }
        case "DESTROY": {
          gameRoomStore.destroyGameRoom();
          router.replace(`/games/${gameTitle}`);
          break;
        }
      }
    },
  );
  alertBeforeUnload();
});

onBeforeUnmount(() => {
  roomMaintainSubscription.unsubscribe();
  deleteAlertBeforeUnload();
});

if (!gameRoomStore.gameRoom || !userStore.user) {
  alert("잘못된 접근입니다. 로비로 이동합니다.");
  window.location.replace(`/games/${gameTitle}`);
}

userStore.joinGameRoom();
onBeforeRouteLeave(async (to, from) => {
  if (userStore.isGameRoom) {
    const answer = window.confirm("게임방을 나가시겠습니까?");
    if (!answer) return false;

    gameRoomStore.destroyGameRoom();
    try {
      await leaveGameRoom(gameTitle, gameRoomId);
    } catch (e) {
      /* empty */
    }
  }
  return true;
});
</script>
