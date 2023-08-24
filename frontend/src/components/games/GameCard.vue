<template>
  <v-container class="game-container" fluid>
    <v-row class="game-card-container">
      <v-col
        class="h-100"
        cols="6"
        v-for="game in currPageGames"
        :key="game.title"
      >
        <v-card
          color="tertiary"
          class="h-100"
          :to="`/games/${game.title}`"
          variant="outlined"
        >
          <v-img
            class="align-end text-white h-100"
            :src="`/images/${game.title}/${game.title}.png`"
            cover
          >
            <v-card-title class="text-end text-h4 font-weight-black"
              >{{ convertTitle(game.title) }}
            </v-card-title>
          </v-img>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup lang="ts">
import { Game } from "@/types/game";
import { convertTitle } from "@/utils/utils";

defineProps<{
  currPageGames: Game[];
}>();
</script>

<style scoped>
.game-container {
  height: 95%;
}

.game-card-container {
  height: 95%;
}
</style>
