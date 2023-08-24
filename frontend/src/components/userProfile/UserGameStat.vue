<template>
  <v-card
    class="d-flex h-100 w-100 text-h4 font-italic flex-column text-center"
    color="tertiary"
  >
    <v-select
      v-model="titleRef"
      :items="gamesTitleRef"
      label="Game"
      class="font-weight-black"
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
    <v-row class="font-weight-black text-h5">
      <v-col></v-col>
      <v-col>유저 전적</v-col>
      <v-col>상대 전적</v-col>
    </v-row>
    <v-row
      v-for="(value, key, index) in userStatRef"
      :key="index"
      class="text-h5 h-100"
    >
      <v-col cols="4" class="font-weight-black">{{ key }}</v-col>
      <v-col cols="4">{{ value[0] }}</v-col>
      <v-col cols="4">{{ value[1] }}</v-col>
    </v-row>
  </v-card>
</template>

<script setup lang="ts">
import { onMounted, onUpdated, ref, toRef, watch } from "vue";
import { convertTitle } from "@/utils/utils";
import { getUserGameStats, getUserRelativeGameStats } from "@/api/user";
import { GameStat } from "@/types/user";

const props = defineProps<{
  userId: string;
}>();

const userId = toRef(props, "userId");
const userGameStatsRef = ref<GameStat[]>([]);
const userRelativeGameStatsRef = ref<GameStat[]>([]);
const gamesTitleRef = ref<string[]>([]);
const titleRef = ref("카드 맞추기");
const userStatRef = ref();

onMounted(() => {
  setGameStats();
});

onUpdated(() => {
  setGameStats();
});

watch(titleRef, () => {
  setGameStats();
});

const setGameStats = async () => {
  try {
    userGameStatsRef.value = await getUserGameStats(userId.value);
    userRelativeGameStatsRef.value = await getUserRelativeGameStats(
      userId.value,
    );

    gamesTitleRef.value = userGameStatsRef.value.map((record) =>
      convertTitle(record.gameTitle),
    );

    const { wins, loses } = userGameStatsRef.value.find(
      (record) => convertTitle(record.gameTitle) === titleRef.value,
    ) as GameStat;
    const { wins: relativeWins, loses: relativeLoses } =
      userRelativeGameStatsRef.value.find(
        (record) => convertTitle(record.gameTitle) === titleRef.value,
      ) as GameStat;

    userStatRef.value = {
      게임수: [wins + loses, relativeWins + relativeLoses],
      승: [wins, relativeWins],
      패: [loses, relativeLoses],
      승률: [getWinRate(wins, loses), getWinRate(relativeWins, relativeLoses)],
    };
  } catch (e) {
    /* empty */
  }
};

const getWinRate = (wins: number, loses: number) =>
  wins + loses === 0 ? "0%" : Math.ceil((wins / (wins + loses)) * 100) + "%";
</script>
