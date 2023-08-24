<template>
  <v-container class="pt-0 w-50">
    <v-row>
      <v-col
        class="d-flex flex-column justify-center align-center bg-indigo-darken-4 rounded-xl position-relative border-xl"
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
        <v-row class="text-h5 font-italic font-weight-black">
          {{ gameRoom.player1.nickname }}
        </v-row>
        <v-row class="text-h6 font-italic font-weight-black">
          {{ scoreRef.player1 }}
        </v-row>
      </v-col>
      <v-col
        class="d-flex flex-column justify-center align-center bg-white rounded-xl text-indigo-darken-4 position-relative border-xl"
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
        <v-row class="text-h5 font-italic font-weight-black">
          {{ gameRoom.player2?.nickname }}
        </v-row>
        <v-row class="text-h6 font-italic font-weight-black">
          {{ gameRoom.player2 ? scoreRef.player2 : "" }}
        </v-row>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup lang="ts">
import { useGameRoomStore } from "@/store/gameRoom";
import { CurrTurn } from "@/types/cardMatching";
import { GameRoom } from "@/types/game";
import { toRef } from "vue";

const props = defineProps<{
  turnRef: CurrTurn | null;
  scoreRef: { player1: number; player2: number };
  timeoutRef: number;
}>();

const { gameRoom } = useGameRoomStore() as {
  gameRoom: GameRoom;
};
const turnRef = toRef(props, "turnRef");
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
