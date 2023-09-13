<template>
  <v-main>
    <default-app-bar>
      <v-container class="w-50 d-flex align-center">
        <v-select
          v-model="titleRef"
          :items="gamesTitleRef"
          label="Game"
          variant="solo-filled"
          bg-color="primary"
        />
        <v-menu>
          <v-list>
            <v-list-item
              v-for="(item, index) in gamesTitleRef"
              :key="index"
              :model-value="index"
            >
              <v-list-item-title>{{ item }}</v-list-item-title>
            </v-list-item>
          </v-list>
        </v-menu>
        <v-container class="w-25 pa-1">
          <v-select
            v-model="filterRef"
            :items="filters"
            label="Filter"
            variant="solo-filled"
            bg-color="primary"
          />
          <v-menu>
            <v-list>
              <v-list-item
                v-for="(item, index) in filters"
                :key="index"
                :model-value="index"
              >
                <v-list-item-title>{{ item }}</v-list-item-title>
              </v-list-item>
            </v-list>
          </v-menu>
        </v-container>
      </v-container>
    </default-app-bar>

    <v-container class="bg-primary h-100" fluid>
      <ranking-row :rankings="rankingsRef" />
      <db-pagination
        :per-page="PER_PAGE"
        :total-elements="totalElementsRef"
        @current-page="changePage"
        class="pagination-container"
      />
    </v-container>
  </v-main>
</template>

<script setup lang="ts">
import DefaultAppBar from "@/layouts/DefaultAppBar.vue";
import { onMounted, ref, watch } from "vue";
import { convertTitle } from "@/utils/utils";
import DbPagination from "@/components/common/DbPagination.vue";
import { getGames } from "@/api/game";
import { RankingContent } from "@/types/ranking";
import { getRanking } from "@/api/ranking";
import RankingRow from "@/components/ranking/RankingRow.vue";

const PER_PAGE = 10;

const gamesTitleRef = ref<string[]>([]);
const titleRef = ref("카드 맞추기");
const filterRef = ref("승 많은 순");
const rankingsRef = ref<RankingContent[]>([]);
const totalElementsRef = ref(0);
const currentPageRef = ref(0);

onMounted(async () => {
  try {
    gamesTitleRef.value = (await getGames()).map((record) =>
      convertTitle(record.title),
    );
    await setRankings(titleRef.value, filterRef.value, 1);
  } catch (e) {
    /* empty */
  }
});
const changePage = (currentPage: number) => {
  try {
    setRankings(titleRef.value, filterRef.value, currentPage);
    currentPageRef.value = currentPage;
  } catch (e) {
    /* empty */
  }
};

watch([titleRef, filterRef], ([title, filter]) => {
  try {
    setRankings(title, filter, currentPageRef.value);
  } catch (e) {
    /* empty */
  }
});

const setRankings = async (title: string, filter: string, page: number) => {
  const convertedValue = convertFilter(filter);
  // eslint-disable-next-line no-useless-catch
  try {
    const rankingData = await getRanking({
      gameTitle: title,
      filter: convertedValue.filter,
      desc: convertedValue.desc,
      page: page - 1,
      limit: PER_PAGE,
    });
    rankingsRef.value = rankingData.rankings;
    totalElementsRef.value = rankingData.totalElements;
  } catch (e) {
    throw e;
  }
};

const filters = [
  { title: "승 많은 순" },
  { title: "승 적은 순" },
  { title: "패 많은 순" },
  { title: "패 적은 순" },
  { title: "승률 높은 순" },
  { title: "승률 낮은 순" },
  { title: "게임수 많은 순" },
  { title: "게임수 적은 순" },
];
const convertFilter = (filter: string): { filter: string; desc: string } => {
  switch (filter) {
    case "승 많은 순":
      return { filter: "wins", desc: "true" };
    case "승 적은 순":
      return { filter: "wins", desc: "false" };
    case "패 많은 순":
      return { filter: "loses", desc: "true" };
    case "패 적은 순":
      return { filter: "loses", desc: "false" };
    case "승률 높은 순":
      return { filter: "winPercentage", desc: "true" };
    case "승률 낮은 순":
      return { filter: "winPercentage", desc: "false" };
    case "게임수 많은 순":
      return { filter: "totalGames", desc: "true" };
    case "게임수 적은 순":
      return { filter: "totalGames", desc: "false" };
    default:
      return { filter: "", desc: "" };
  }
};
</script>

<style scoped lang="scss">
.rankingDto-game-name {
  height: 80%;
}

.rankingDto-container {
  height: 95%;
}

.pagination-container {
  height: 5%;
}
</style>
