<template>
  <v-container class="d-flex flex-column">
    <v-row class="h-50">
      <v-col>
        <game-room-ready-button />
      </v-col>
    </v-row>
    <v-row class="h-50">
      <v-col>
        <v-btn
          class="w-100 h-100 bg-red text-h6 font-weight-black"
          @click="exitDialog"
        >
          나가기
        </v-btn>

        <default-dialog
          negative
          :dialog="exitToggleRef"
          @toggleDialog="exitDialog"
          @clickYes="exitYes"
        >
          <template v-slot:title>나가기</template>
          <template v-slot:text>게임방에서 나가시겠습니까?</template>
        </default-dialog>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup lang="ts">
import { ref } from "vue";
import DefaultDialog from "@/components/common/DefaultDialog.vue";
import { useRoute, useRouter } from "vue-router";
import GameRoomReadyButton from "@/components/gameRoom/GameRoomGameButton.vue";
import { leaveGameRoom } from "@/api/game";
import { useGameRoomStore } from "@/store/gameRoom";

const exitToggleRef = ref(false);
const { gameTitle, gameRoomId } = useRoute().params as {
  gameTitle: string;
  gameRoomId: string;
};
const router = useRouter();
const gameRoomStore = useGameRoomStore();
const exitDialog = () => (exitToggleRef.value = !exitToggleRef.value);
const exitYes = async () => {
  gameRoomStore.destroyGameRoom();
  try {
    await leaveGameRoom(gameTitle, gameRoomId);
  } catch (e) {
    /* empty */
  } finally {
    await router.replace(`/games/${gameTitle}`);
  }
};
</script>
