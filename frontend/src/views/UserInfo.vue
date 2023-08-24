<template>
  <v-main>
    <default-app-bar>유저 정보</default-app-bar>

    <v-container class="bg-primary h-100 px-0" fluid>
      <v-container class="user-container d-flex align-center" fluid>
        <v-row class="h-100 d-flex align-center justify-center">
          <v-col cols="7" class="h-100 pa-2 d-flex align-center">
            <user-basic-info
              @send-user-nickname="getUserNickname"
              :userId="userIdRef"
            />
          </v-col>
          <v-col cols="5" class="h-100 d-flex flex-column pa-2">
            <user-game-stat :userId="userIdRef" />
          </v-col>
        </v-row>
      </v-container>

      <v-container
        class="record-container d-flex flex-column align-center px-0"
        fluid
      >
        <user-game-record
          v-if="isGameRecords"
          :gameRecords="gameRecordsRef"
          :userNickname="userNicknameRef"
        />
        <v-card-text
          v-else
          class="text-h4 d-flex justify-center align-center h-100 text-grey font-weight-black font-italic"
          >게임 기록이 없습니다.
        </v-card-text>
      </v-container>

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
import { computed, onMounted, onUpdated, ref } from "vue";
import UserBasicInfo from "@/components/userProfile/UserBasicInfo.vue";
import UserGameStat from "@/components/userProfile/UserGameStat.vue";
import { useRoute } from "vue-router";
import { getUserGameRecords } from "@/api/user";
import { GameRecord } from "@/types/user";
import UserGameRecord from "@/components/userProfile/UserGameRecord.vue";
import DbPagination from "@/components/common/DbPagination.vue";

const PER_PAGE = 5;
const gameRecordsRef = ref<GameRecord[]>([]);
const userNicknameRef = ref<string>("");
const userIdRef = ref<string>(useRoute().params.userId as string);
const totalElementsRef = ref<number>(0);
const currentPageRef = ref(0);

onMounted(async () => {
  try {
    await setGameRecords(userIdRef.value, 1);
  } catch (e) {
    /* empty */
  }
});

onUpdated(async () => {
  userIdRef.value = useRoute().params.userId as string;
  try {
    await setGameRecords(userIdRef.value, 1);
  } catch (e) {
    /* empty */
  }
});

const setGameRecords = async (userId: string, page: number) => {
  // eslint-disable-next-line no-useless-catch
  try {
    const gameRecordData = await getUserGameRecords(userId, {
      page: page - 1,
      limit: PER_PAGE,
    });
    gameRecordsRef.value = gameRecordData.userGameRecords;
    totalElementsRef.value = gameRecordData.totalElements;
  } catch (e) {
    throw e;
  }
};

const changePage = async (currentPage: number) => {
  try {
    await setGameRecords(userIdRef.value, currentPage);
    currentPageRef.value = currentPage;
  } catch (e) {
    /* empty */
  }
};

const isGameRecords = computed(() => gameRecordsRef.value.length > 0);

const getUserNickname = (userNickname: string) =>
  (userNicknameRef.value = userNickname);
</script>

<style scoped lang="scss">
.user-container {
  height: 40%;
}

.record-container {
  height: 55%;
}

.pagination-container {
  height: 5%;
}
</style>
