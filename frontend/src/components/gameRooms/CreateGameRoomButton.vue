<template>
  <v-btn
    color="btn-primary"
    class="w-25 text-h4 h-100 font-weight-black"
    @click="createRoomToggleDialog"
    >{{ TITLE }}
  </v-btn>
  <default-dialog
    :dialog="dialog"
    @click-yes="createRoomYes"
    @toggle-dialog="createRoomToggleDialog"
  >
    <template v-slot:title>{{ TITLE }}</template>
    <template v-slot:text>
      <v-container>
        <v-row class="d-flex">
          <v-text-field
            density="compact"
            bg-color="tertiary"
            variant="solo"
            :rules="titleRules"
            :counter="`${TITLE_MAX_LENGTH}`"
            placeholder="방제목을 입력하세요"
            v-model="gameRoomTitle"
            autofocus
          />
        </v-row>
      </v-container>
    </template>
  </default-dialog>
</template>

<script setup lang="ts">
import DefaultDialog from "@/components/common/DefaultDialog.vue";
import { computed, ref } from "vue";
import { useRoute, useRouter } from "vue-router";
import { useGameRoomStore } from "@/store/gameRoom";
import { createGameRoom } from "@/api/game";
import { GameRoom } from "@/types/game";

const TITLE_MAX_LENGTH = 10;
const TITLE = "방만들기";

const router = useRouter();
const gameTitle = useRoute().params.gameTitle as string;
const gameRoomStore = useGameRoomStore();
const gameRoomTitle = ref("");
const dialog = ref(false);

const createRoomToggleDialog = () => {
  dialog.value = !dialog.value;
  gameRoomTitle.value = "";
};

const createRoomYes = async () => {
  try {
    const gameRoom: GameRoom = await createGameRoom(
      gameTitle,
      gameRoomTitle.value,
    );
    gameRoomStore.setGameRoom(gameRoom);
    createRoomToggleDialog();
    await router.push({
      name: "GameRoom",
      params: {
        gameTitle: gameTitle,
        gameRoomId: gameRoomStore.gameRoom?.id,
      },
    });
  } catch (e) {
    /* empty */
  }
};

const titleRules = computed(() => [
  (value: string) => {
    if (value) return true;

    return "방제목은 필수입니다.";
  },
  (value: string) => {
    if (value?.length <= TITLE_MAX_LENGTH) return true;

    return `방제목은 ${TITLE_MAX_LENGTH}글자 이하여야 합니다.`;
  },
]);
</script>
