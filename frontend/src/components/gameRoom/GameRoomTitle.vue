<template>
  <v-row align="center" justify="center">
    <v-col cols="auto">
      <v-card class="bg-tertiary text-center" flat>
        <v-card-title class="text-h4 font-weight-black font-italic">
          {{ convertTitle(gameTitle) }}
        </v-card-title>
        <v-card-title class="text-h6 font-weight-black font-italic pa-0">
          {{ gameRoom.title }}
        </v-card-title>
      </v-card>
    </v-col>
  </v-row>
</template>

<script setup lang="ts">
import { useGameRoomStore } from "@/store/gameRoom";
import { GameRoom } from "@/types/game";
import { useRoute } from "vue-router";
import { convertTitle } from "@/utils/utils";

const route = useRoute();
const gameTitle = route.params.gameTitle as string;

const { gameRoom } = useGameRoomStore() as { gameRoom: GameRoom };
</script>
