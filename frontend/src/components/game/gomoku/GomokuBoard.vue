<template>
  <v-container class="h-100 w-100 flex-column justify-center">
    <v-container style="height: 10%">
      <Transition>
        <score-board
          v-if="turnRef"
          :turn-ref="turnRef"
          :timeout-ref="timeoutRef"
          :user-stone-color="userStoneColorRef"
        />
      </Transition>
    </v-container>
    <v-container class="game-board pa-0 d-flex flex-column">
      <template v-if="boardRef.length > 0">
        <v-row v-for="i in 15" :key="i">
          <v-col
            v-for="j in 15"
            :key="j"
            class="pa-0"
            :style="[`cursor:${pointer(i, j)}`]"
          >
            <gomoku-square
              :row="getRowNum(i, j)"
              :col="getColumnNum(i, j)"
              :COLUMNS="COLUMNS"
              :state="boardRef[getIndex(i, j)]"
              :turn="turnRef"
              :user-stone-color="userStoneColorRef"
              @click="putStone(i, j)"
              :prev-stone="prevStone"
            />
          </v-col>
        </v-row>
      </template>
    </v-container>
  </v-container>
  <alert
    :dialog="alertRef"
    @toggle-dialog="alertToggle"
    :message="alertMessage"
  />
  <winner-dialog
    v-model:dialog="dialogRef"
    :winner-nickname="winnerNicknameRef"
  />
</template>

<script setup lang="ts">
import { onBeforeUnmount, onMounted, ref, watch } from "vue";
import GomokuSquare from "@/components/game/gomoku/GomokuSquare.vue";
import {
  BoardState,
  CurrTurn,
  GomokuGamePayload,
  GomokuStoneColor,
  Stone,
} from "@/types/gomoku";
import Alert from "@/components/common/Alert.vue";
import { StompSubscription } from "@stomp/stompjs";
import { useStompStore } from "@/store/stomp";
import { launchConfetti } from "@/plugins/confetti";
import { useRoute } from "vue-router";
import { useUserStore } from "@/store/user";
import { useGameRoomStore } from "@/store/gameRoom";
import ScoreBoard from "@/components/game/gomoku/ScoreBoard.vue";
import WinnerDialog from "@/components/common/WinnerDialog.vue";

const ROWS = 15;
const COLUMNS = 15;
const TIMEOUT = 20;
const timeoutRef = ref(TIMEOUT);
const turnRef = ref<CurrTurn | null>(null);
const prevStone = ref<Stone | null>(null);
const alertMessage = ref("");
const { client } = useStompStore();
const userStore = useUserStore();
const { gameRoomId } = useRoute().params;
const boardRef = ref<BoardState[]>([]);
const userStoneColorRef = ref<GomokuStoneColor | null>(null);
const { findNicknameById } = useGameRoomStore();
const winnerNicknameRef = ref("");
const alertRef = ref(false);
const dialogRef = ref(false);

const alertToggle = () => (alertRef.value = !alertRef.value);
const dialogToggle = () => (dialogRef.value = !dialogRef.value);

let gameSubscription: StompSubscription;
onMounted(() => {
  gameSubscription = client.subscribe(
    `/topic/gomoku/${gameRoomId}`,
    (payload) => {
      const message = JSON.parse(payload.body) as GomokuGamePayload;

      switch (message.type) {
        case "INIT_BOARD": {
          boardRef.value = Array.from(
            { length: ROWS * COLUMNS },
            () => BoardState.Empty,
          );
          userStoneColorRef.value = message.content;
          userStore.startGame();
          break;
        }
        case "PUT_STONE": {
          if (!turnRef.value || !boardRef.value) return;

          const { row, col, putResult, stoneColor } = message.content;
          alertMessage.value = "";

          switch (putResult) {
            case "FAIL":
              alertMessage.value = "그곳에 돌을 착수할 수 없습니다.";
              break;
            case "DOUBLE_THREE":
              alertMessage.value = "3-3은 금지된 수입니다.";
              break;
            case "DOUBLE_FOUR":
              alertMessage.value = "4-4는 금지된 수입니다.";
              break;
            case "OVER_FIVE":
              alertMessage.value = "6목 이상은 금지된 수입니다.";
              break;
            case "FINISH":
            case "SUCCESS":
              prevStone.value = { row, col };
              boardRef.value[getIndex(row, col)] =
                stoneColor === "WHITE" ? BoardState.White : BoardState.Black;
              break;
          }
          if (
            alertMessage.value.length > 0 &&
            turnRef.value.id === userStore.user?.id
          ) {
            alertToggle();
          }
          break;
        }
        case "TURN_CHANGE": {
          turnRef.value = message.content;
          clearInterval(timeoutIntervalId);
          break;
        }
        case "FINISH_GAME": {
          clearInterval(timeoutIntervalId);
          dialogToggle();

          const { winnerId } = message.content;
          winnerNicknameRef.value = findNicknameById(winnerId);

          if (winnerId === userStore.user?.id) {
            launchConfetti();
          }

          setTimeout(() => {
            dialogRef.value = false;
            userStore.finishGame();
          }, 5000);
          break;
        }
      }
    },
  );
});

onBeforeUnmount(() => {
  gameSubscription.unsubscribe();
  clearInterval(timeoutIntervalId);
});

let timeoutIntervalId: NodeJS.Timer;
watch(
  turnRef,
  () => {
    if (!turnRef.value) return;
    timeoutRef.value = TIMEOUT;
    timeoutIntervalId = setInterval(timeoutInterval, 1000);
  },
  { deep: true },
);

const timeoutInterval = () => {
  if (timeoutRef.value > 0) {
    timeoutRef.value--;
  } else if (turnRef?.value?.id === userStore.user?.id) {
    client.publish({
      destination: `/app/gomoku/${gameRoomId}/timeout`,
      body: JSON.stringify({
        type: "TIMEOUT",
      }),
    });
  }
};

const getRowNum = (i: number, j: number) => {
  return Math.floor(getIndex(i, j) / COLUMNS);
};
const getColumnNum = (i: number, j: number) => {
  return getIndex(i, j) % COLUMNS;
};

const getIndex = (i: number, j: number) => {
  return (i - 1) * COLUMNS + (j - 1);
};
const putStone = (i: number, j: number) => {
  if (
    !boardRef.value ||
    !userStore.isOnGame ||
    !turnRef.value ||
    !(boardRef.value[getIndex(i, j)] === BoardState.Empty)
  ) {
    return;
  }

  if (turnRef.value.id === userStore.user?.id) {
    client.publish({
      destination: `/app/gomoku/${gameRoomId}/put-stone`,
      body: JSON.stringify({
        row: i,
        col: j,
      }),
    });
  }
};

const pointer = (i: number, j: number) => {
  if (boardRef.value === null) return;
  return boardRef.value[getIndex(i, j)] === BoardState.Empty
    ? "pointer"
    : "not-allowed";
};
</script>

<style scoped lang="scss">
.game-board {
  height: 90%;
  width: 70%;
}

.row {
  height: calc(100 / 15 * 1%);
}
</style>
