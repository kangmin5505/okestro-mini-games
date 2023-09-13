<template>
  <v-main>
    <default-app-bar>{{ convertTitle(gameTitle) }}</default-app-bar>

    <v-container class="bg-primary h-100" fluid>
      <v-container class="game-rooms-container" fluid>
        <game-rooms-header
          class="game-rooms-header"
          @refresh="refreshGameRooms"
        />
        <game-room-list
          v-if="isGameRooms"
          :gameTitle="gameTitle"
          :currPageGameRooms="currPageGameRoomsRef"
          class="game-rooms-list"
        />
        <v-container v-else class="h-75">
          <v-card-text
            class="text-h4 d-flex justify-center align-center h-100 text-grey font-weight-black font-italic"
            >게임방이 없습니다.
          </v-card-text>
        </v-container>
      </v-container>

      <memory-pagination
        v-if="isGameRooms"
        @change-page="getCurrPageGameRooms"
        :per-page="PER_PAGE"
        :items="gameRoomsRef"
        class="pagination-container"
      />
    </v-container>
  </v-main>
</template>

<script setup lang="ts">
import MemoryPagination from "@/components/common/MemoryPagination.vue";
import DefaultAppBar from "@/layouts/DefaultAppBar.vue";
import GameRoomsHeader from "@/components/gameRooms/GameRoomsHeader.vue";
import GameRoomList from "@/components/gameRooms/GameRoomList.vue";
import { useRoute } from "vue-router";
import { computed, onMounted, ref } from "vue";
import { GameRoom } from "@/types/game";
import { convertTitle } from "@/utils/utils";
import { getGameRooms } from "@/api/game";

const PER_PAGE = 6;
const gameTitle = useRoute().params.gameTitle as string;
const gameRoomsRef = ref<GameRoom[]>([]);
const currPageGameRoomsRef = ref<GameRoom[]>([]);

onMounted(() => {
  setGameRooms();
});

const refreshGameRooms = () => {
  // gameRoomsRef.value = [];
  setGameRooms();
};

const setGameRooms = async () => {
  try {
    const data = await getGameRooms(gameTitle);
    gameRoomsRef.value = data.gameRooms;
  } catch (e) {
    /* empty */
  }
};

const getCurrPageGameRooms = (currPageGameRooms: GameRoom[]) =>
  (currPageGameRoomsRef.value = currPageGameRooms);

const isGameRooms = computed(() => gameRoomsRef.value.length > 0);
</script>

<style scoped lang="scss">
.game-rooms-container {
  height: 95%;
}

.game-rooms-header {
  height: 10%;
}

.game-rooms-list {
  height: 90%;
}

.pagination-container {
  height: 5%;
}
</style>
