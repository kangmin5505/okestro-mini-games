<template>
  <v-container class="pt-0 w-50">
    <v-row class="h-100">
      <v-col
        class="d-flex flex-column justify-center align-center rounded-xl position-relative border-xl"
        :class="player1StoneColor"
      >
        <Transition>
          <v-container
            v-if="turnRef && turnRef.id === gameRoom.player1?.id"
            class="position-absolute player-timer d-flex justify-center align-center"
          >
            <v-icon icon="mdi-timer-play-outline" size="40" />
            <v-card-text class="pl-1 text-h4 font-weight-black text-red"
              >{{ timeoutRef }}
            </v-card-text>
          </v-container>
        </Transition>
        <v-row
          class="text-h5 font-italic font-weight-black align-center"
          :class="player1FontColor"
        >
          {{ gameRoom.player1.nickname }}
        </v-row>
      </v-col>
      <v-col
        class="d-flex flex-column justify-center align-center rounded-xl position-relative border-xl"
        :class="player2StoneColor"
      >
        <Transition>
          <v-container
            v-if="turnRef && turnRef.id === gameRoom.player2?.id"
            class="position-absolute player-timer d-flex justify-center align-center"
          >
            <v-icon icon="mdi-timer-play-outline" size="40" />
            <v-card-text class="pl-1 text-h4 font-weight-black text-red">
              {{ timeoutRef }}
            </v-card-text>
          </v-container>
        </Transition>
        <v-row
          class="text-h5 font-italic font-weight-black align-center"
          :class="player2FontColor"
        >
          {{ gameRoom.player2?.nickname }}
        </v-row>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup lang="ts">
import { useGameRoomStore } from "@/store/gameRoom";
import { CurrTurn } from "@/types/cardMatching";
import { GameRoom } from "@/types/game";
import { computed, toRefs } from "vue";
import { GomokuStoneColor } from "@/types/gomoku";

const props = defineProps<{
  turnRef: CurrTurn | null;
  timeoutRef: number;
  userStoneColor: GomokuStoneColor | null;
}>();

const { gameRoom } = useGameRoomStore() as {
  gameRoom: GameRoom;
};
const { turnRef, userStoneColor } = toRefs(props);

const player1StoneColor = computed(() => {
  return userStoneColor?.value?.blackUserId === gameRoom.player1?.id
    ? "bg-black"
    : "bg-white";
});
const player1FontColor = computed(() => {
  return userStoneColor?.value?.blackUserId === gameRoom.player1?.id
    ? "text-white"
    : "text-black";
});
const player2StoneColor = computed(() => {
  return userStoneColor?.value?.blackUserId === gameRoom.player2?.id
    ? "bg-black"
    : "bg-white";
});
const player2FontColor = computed(() => {
  return userStoneColor?.value?.blackUserId === gameRoom.player2?.id
    ? "text-white"
    : "text-black";
});
</script>

<style scoped>
.player-timer {
  left: 0;
}

.v-enter-active {
  transition: opacity 0.5s ease;
}

.v-enter-from {
  opacity: 0;
}
</style>
