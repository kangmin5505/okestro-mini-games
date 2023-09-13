<template>
  <v-row
    class="w-100 text-h5 font-weight-black record-row d-flex align-center text-center"
  >
    <v-col> 게임</v-col>
    <v-col> 결과</v-col>
    <v-col> 닉네임(점수)</v-col>
    <v-col> 시작시간</v-col>
    <v-col> 게임시간</v-col>
  </v-row>
  <v-row v-for="index in 5" :key="index" class="record-row w-100">
    <v-col class="py-1">
      <v-card
        class="h-100 bg-tertiary bg-blue d-flex justify-center align-center text-h6"
      >
        <v-row class="h-100">
          <v-col
            v-for="(value, key) in formattedGameRecords[index - 1]"
            :key="key"
            class="d-flex justify-center align-center"
            :class="
              key === '결과'
                ? value === '승리'
                  ? 'text-green'
                  : 'text-red'
                : ''
            "
          >
            {{ value }}
          </v-col>
        </v-row>
      </v-card>
    </v-col>
  </v-row>
</template>

<script setup lang="ts">
import { onMounted, onUpdated, ref, toRef, watch } from "vue";
import { convertTitle } from "@/utils/utils";
import dayjs from "dayjs";
import { FormattedGameRecord, GameRecord } from "@/types/user";

const props = defineProps<{
  gameRecords: GameRecord[];
  userNickname: string;
}>();

const currPageGameRecordsRef = toRef(props, "gameRecords");
const formattedGameRecords = ref<FormattedGameRecord[]>([]);

onMounted(() => {
  formattedGameRecords.value = getFormattedGameRecords(
    currPageGameRecordsRef.value,
  );
});

onUpdated(() => {
  formattedGameRecords.value = getFormattedGameRecords(
    currPageGameRecordsRef.value,
  );
});

watch(currPageGameRecordsRef, () => {
  formattedGameRecords.value = getFormattedGameRecords(
    currPageGameRecordsRef.value,
  );
});

const getFormattedGameRecords = (gameRecords: GameRecord[]) => {
  return gameRecords.map((gameRecord) => {
    const {
      gameTitle,
      winUserNickname,
      loseUserNickname,
      winUserScore,
      loseUserScore,
      startTime,
      finishTime,
    } = gameRecord;

    return {
      게임: convertTitle(gameTitle),
      결과: winUserNickname === props.userNickname ? "승리" : "패배",
      "닉네임(점수)": `${winUserNickname}(${winUserScore}) vs ${loseUserNickname}(${loseUserScore})`,
      시작시간: dayjs(startTime).format("YYYY-MM-DD HH:mm:ss"),
      게임시간: dayjs(dayjs(finishTime).diff(startTime)).format("mm분 ss초"),
    };
  });
};
</script>

<style scoped>
.record-row {
  height: 16.6%;
}
</style>
