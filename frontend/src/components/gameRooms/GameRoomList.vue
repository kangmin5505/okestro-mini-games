<template>
  <v-container fluid>
    <v-row v-for="row in rowLength" :key="row" class="game-room-card">
      <v-col cols="4" v-for="col in PER_COL" :key="col">
        <game-room-card
          v-if="currPageGameRooms[(row - 1) * PER_COL + (col - 1)]"
          :game-room="currPageGameRooms[(row - 1) * PER_COL + (col - 1)]"
          @join-room="joinRoom"
        />
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup lang="ts">
import GameRoomCard from "@/components/gameRooms/GameRoomCard.vue";
import { computed, toRefs } from "vue";
import { useRouter } from "vue-router";
import { useGameRoomStore } from "@/store/gameRoom";
import { joinGameRoom } from "@/api/game";
import { GameRoom } from "@/types/game";

const PER_COL = 3;
const props = defineProps<{
  gameTitle: string;
  currPageGameRooms: GameRoom[];
}>();

const { gameTitle, currPageGameRooms } = toRefs(props);
const router = useRouter();
const gameRoomStore = useGameRoomStore();

const joinRoom = async (gameRoomId: string) => {
  try {
    const gameRoom = await joinGameRoom(gameTitle.value, gameRoomId);

    gameRoomStore.setGameRoom(gameRoom);

    await router.push({
      name: "GameRoom",
      params: {
        gameTitle: gameTitle.value,
        gameRoomId: gameRoomId,
      },
    });
  } catch (e) {
    /* empty */
  }
};

const rowLength = computed(() => {
  return Math.ceil(currPageGameRooms.value.length / PER_COL);
});
</script>

<style scoped>
.game-room-card {
  height: 50%;
}
</style>
