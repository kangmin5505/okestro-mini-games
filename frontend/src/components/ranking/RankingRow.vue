<template>
  <v-row
    class="w-100 text-h6 font-weight-black rankingDto-row d-flex align-center text-center"
  >
    <v-col> 순위</v-col>
    <v-col> 닉네임</v-col>
    <v-col> 승</v-col>
    <v-col> 패</v-col>
    <v-col> 승률</v-col>
    <v-col> 게임수</v-col>
  </v-row>

  <template v-for="row in 10" :key="row">
    <v-row class="ranking-row">
      <v-col>
        <v-card
          v-if="row < rankings.length + 1"
          color="tertiary"
          class="h-100 text-h5 d-flex align-center text-center ranking-row"
          :to="{
            name: 'UserInfo',
            params: { userId: rankings[row - 1].userId },
          }"
        >
          <v-col>
            {{ rankings[row - 1].rank }}
          </v-col>
          <v-col>
            {{ rankings[row - 1].nickname }}
          </v-col>
          <v-col>
            {{ rankings[row - 1].wins }}
          </v-col>
          <v-col>
            {{ rankings[row - 1].loses }}
          </v-col>
          <v-col> {{ rankings[row - 1].winPercentage }} %</v-col>
          <v-col>
            {{ rankings[row - 1].totalGames }}
          </v-col>
        </v-card>
      </v-col>
    </v-row>
  </template>
</template>

<script setup lang="ts">
import { RankingContent } from "@/types/ranking";

defineProps<{
  rankings: RankingContent[];
}>();
</script>

<style scoped>
.ranking-row {
  height: 8.9%;
}
</style>
