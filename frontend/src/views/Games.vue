<template>
  <v-main class="main">
    <default-app-bar>Mini Games</default-app-bar>

    <v-container class="bg-primary h-100 d-flex flex-column" fluid>
      <game-card :currPageGames="currPageGamesRef" />
      <memory-pagination
        v-if="isGameList"
        @change-page="getCurrPageGames"
        :per-page="PER_PAGE"
        :items="gamesRef"
      />
    </v-container>
  </v-main>
</template>

<script setup lang="ts">
import DefaultAppBar from "@/layouts/DefaultAppBar.vue";
import { computed, onMounted, ref } from "vue";
import GameCard from "@/components/games/GameCard.vue";
import MemoryPagination from "@/components/common/MemoryPagination.vue";
import { getGames } from "@/api/game";
import { Game } from "@/types/game";

const PER_PAGE = 2;
const gamesRef = ref<Game[]>([]);
const currPageGamesRef = ref<Game[]>([]);

onMounted(async () => {
  try {
    gamesRef.value = await getGames();
  } catch (e) {
    /* empty */
  }
});

const getCurrPageGames = (currPageGameList: Game[]) => {
  currPageGamesRef.value = currPageGameList;
};

const isGameList = computed(() => {
  return gamesRef.value.length > 0;
});
</script>

<style scoped>
.main {
  height: 100vh;
}
</style>
