<template>
  <v-container class="h-100 w-100">
    <v-container class="text-center">스코어 보드</v-container>
    <!--    <v-container class="text-black d-flex justify-center">-->
    <!--      <v-btn @click="turnRef = BoardState.Black">Turn Black</v-btn>-->
    <!--      <v-btn @click="turnRef = BoardState.White">Turn White</v-btn>-->
    <!--    </v-container>-->
    <v-container class="game-board pa-0 d-flex flex-column">
      <v-row v-for="i in 15" :key="i">
        <v-col
          v-for="j in 15"
          :key="j"
          class="pa-0"
          :style="[`cursor:${pointer(i, j)}`]"
        >
          <!--          <gomoku-square-->
          <!--            :row="getRowNum(i, j)"-->
          <!--            :col="getColumnNum(i, j)"-->
          <!--            :COLUMNS="COLUMNS"-->
          <!--            :state="boardRef[getIndex(i, j)]"-->
          <!--            :isHover="uiRef[getIndex(i, j)].isHover"-->
          <!--            :turn="turnRef"-->
          <!--            @click="putStone(i, j)"-->
          <!--            @mouseenter="uiRef[getIndex(i, j)].isHover = true"-->
          <!--            @mouseleave="uiRef[getIndex(i, j)].isHover = false"-->
          <!--          />-->
          <gomoku-square
            :row="getRowNum(i, j)"
            :col="getColumnNum(i, j)"
            :COLUMNS="COLUMNS"
            :state="boardRef[getIndex(i, j)]"
            :turn="turnRef"
            @click="putStone(i, j)"
          />
        </v-col>
      </v-row>
    </v-container>
    <!--    <v-container class="text-black d-flex justify-space-between">-->
    <!--      <v-btn @click="clear">Clear</v-btn>-->
    <!--      <v-btn @click="testCheckDoubleThree">Test Double Three</v-btn>-->
    <!--      <v-btn @click="testCheckDoubleFour">Test Double Four</v-btn>-->
    <!--      <v-btn @click="testCheckOverFive">Test Over Five</v-btn>-->
    <!--    </v-container>-->
  </v-container>
  <alert
    :dialog="alertDialogRef"
    @toggle-dialog="alertDialogToggle"
    :message="alertMessage"
  />
</template>

<script setup lang="ts">
import { ref } from "vue";
import GomokuSquare from "@/components/game/gomoku/GomokuSquare.vue";
import { BoardState } from "@/types/gomoku";
import Alert from "@/components/common/Alert.vue";
//
// // TODO: REMOVE
//
// const clear = () => {
//   boardRef.value = Array.from(
//     { length: ROWS * COLUMNS },
//     () => BoardState.Empty,
//   );
// };
// const testCheckDoubleThree = () => {
//   //1
//   boardRef.value[getIndex(3, 2)] = turnRef.value;
//   boardRef.value[getIndex(3, 3)] = turnRef.value;
//   boardRef.value[getIndex(2, 4)] = turnRef.value;
//   boardRef.value[getIndex(4, 4)] = turnRef.value;
//
//   boardRef.value[getIndex(3, 4)] = turnRef.value;
//   if (checkDoubleThree(3, 4)) {
//     alert("Success : double four test");
//     boardRef.value[getIndex(3, 4)] = BoardState.Empty;
//   } else {
//     alert("Fail : double four test");
//   }
//
//   //2
//   boardRef.value[getIndex(3, 7)] = turnRef.value;
//   boardRef.value[getIndex(3, 8)] = turnRef.value;
//   boardRef.value[getIndex(4, 9)] = turnRef.value;
//   boardRef.value[getIndex(5, 9)] = turnRef.value;
//
//   boardRef.value[getIndex(3, 9)] = turnRef.value;
//   if (checkDoubleThree(3, 9)) {
//     alert("Success : double four test");
//     boardRef.value[getIndex(3, 9)] = BoardState.Empty;
//   } else {
//     alert("Fail : double four test");
//   }
//
//   //3
//   boardRef.value[getIndex(3, 12)] = turnRef.value;
//   boardRef.value[getIndex(3, 14)] = turnRef.value;
//   boardRef.value[getIndex(2, 13)] = turnRef.value;
//   boardRef.value[getIndex(4, 13)] = turnRef.value;
//
//   boardRef.value[getIndex(3, 13)] = turnRef.value;
//   if (checkDoubleThree(3, 13)) {
//     alert("Success : double four test");
//     boardRef.value[getIndex(3, 13)] = BoardState.Empty;
//   } else {
//     alert("Fail : double four test");
//   }
//
//   //4
//   boardRef.value[getIndex(7, 2)] = turnRef.value;
//   boardRef.value[getIndex(7, 4)] = turnRef.value;
//   boardRef.value[getIndex(9, 2)] = turnRef.value;
//   boardRef.value[getIndex(9, 4)] = turnRef.value;
//
//   boardRef.value[getIndex(8, 3)] = turnRef.value;
//   if (checkDoubleThree(8, 3)) {
//     alert("Success : double four test");
//     boardRef.value[getIndex(8, 3)] = BoardState.Empty;
//   } else {
//     alert("Fail : double four test");
//   }
//
//   //5
//   boardRef.value[getIndex(7, 7)] = turnRef.value;
//   boardRef.value[getIndex(8, 8)] = turnRef.value;
//   boardRef.value[getIndex(11, 9)] = turnRef.value;
//   boardRef.value[getIndex(9, 11)] = turnRef.value;
//
//   boardRef.value[getIndex(10, 10)] = turnRef.value;
//   if (checkDoubleThree(10, 10)) {
//     alert("Success : double four test");
//     boardRef.value[getIndex(10, 10)] = BoardState.Empty;
//   } else {
//     alert("Fail : double four test");
//   }
//
//   //6
//   boardRef.value[getIndex(12, 2)] = turnRef.value;
//   boardRef.value[getIndex(12, 3)] = turnRef.value;
//   boardRef.value[getIndex(13, 5)] = turnRef.value;
//   boardRef.value[getIndex(14, 5)] = turnRef.value;
//
//   boardRef.value[getIndex(12, 5)] = turnRef.value;
//   if (checkDoubleThree(12, 5)) {
//     alert("Success : double four test");
//     boardRef.value[getIndex(12, 5)] = BoardState.Empty;
//   } else {
//     alert("Fail : double four test");
//   }
//
//   //7
//   boardRef.value[getIndex(13, 8)] = turnRef.value;
//   boardRef.value[getIndex(13, 10)] = turnRef.value;
//   boardRef.value[getIndex(12, 12)] = turnRef.value;
//   boardRef.value[getIndex(11, 13)] = turnRef.value;
//
//   boardRef.value[getIndex(13, 11)] = turnRef.value;
//   if (checkDoubleThree(13, 11)) {
//     alert("Success : double four test");
//     boardRef.value[getIndex(13, 11)] = BoardState.Empty;
//   } else {
//     alert("Fail : double four test");
//   }
// };
// const testCheckDoubleFour = () => {
//   const i = 2;
//   const j = 2;
//   for (let k = 0; k < 3; ++k) {
//     boardRef.value[getIndex(i, j + k)] = turnRef.value;
//   }
//   for (let k = 0; k < 3; ++k) {
//     boardRef.value[getIndex(i + 1 + k, j + 3)] = turnRef.value;
//   }
//   boardRef.value[getIndex(i, j + 3)] = turnRef.value;
//   if (checkDoubleFour(i, j + 3)) {
//     alert("Success : double four test");
//     boardRef.value[getIndex(i, j + 3)] = BoardState.Empty;
//   } else {
//     alert("Fail : double four test");
//   }
//
//   const i2 = 2;
//   const j2 = 8;
//   for (let k = 0; k < 3; ++k) {
//     boardRef.value[getIndex(i2, j2 + k)] = turnRef.value;
//   }
//   for (let k = 0; k < 3; ++k) {
//     boardRef.value[getIndex(i2 + 1 + k, j2 + 4 + k)] = turnRef.value;
//   }
//   boardRef.value[getIndex(i2, j2 + 3)] = turnRef.value;
//   if (checkDoubleFour(i2, j2 + 3)) {
//     alert("Success : double four test");
//     boardRef.value[getIndex(i2, j2 + 3)] = BoardState.Empty;
//   } else {
//     alert("Fail : double four test");
//   }
//
//   const i3 = 8;
//   const j3 = 2;
//   for (let k = 0; k < 2; ++k) {
//     boardRef.value[getIndex(i3 + k, j3 + k)] = turnRef.value;
//   }
//   boardRef.value[getIndex(i3 + 3, j3 + 3)] = turnRef.value;
//   for (let k = 0; k < 2; ++k) {
//     boardRef.value[getIndex(i3 + k, j3 - k + 4)] = turnRef.value;
//   }
//   boardRef.value[getIndex(i3 + 3, j3 + 1)] = turnRef.value;
//   boardRef.value[getIndex(i3 + 2, j3 + 2)] = turnRef.value;
//   if (checkDoubleFour(i3 + 2, j3 + 2)) {
//     alert("Success : double four test");
//     boardRef.value[getIndex(i3 + 2, j3 + 2)] = BoardState.Empty;
//   } else {
//     alert("Fail : double four test");
//   }
//
//   const i4 = 8;
//   const j4 = 8;
//   for (let k = 0; k < 2; ++k) {
//     boardRef.value[getIndex(i4, j4 + k)] = turnRef.value;
//   }
//   boardRef.value[getIndex(i4, j4 + 3)] = turnRef.value;
//   for (let k = 0; k < 3; ++k) {
//     boardRef.value[getIndex(i4 + 3 - k, j4 - 1 + k)] = turnRef.value;
//   }
//   boardRef.value[getIndex(i4, j4 + 2)] = turnRef.value;
//   if (checkDoubleFour(i4, j4 + 2)) {
//     alert("Success : double four test");
//     boardRef.value[getIndex(i4, j4 + 2)] = BoardState.Empty;
//   } else {
//     alert("Fail : double four test");
//   }
// };
// const testCheckOverFive = () => {
//   const i = 1;
//   const j = 1;
//   for (let k = 0; k < 4; ++k) {
//     boardRef.value[getIndex(i, j + k)] = turnRef.value;
//   }
//   boardRef.value[getIndex(i, j + 5)] = turnRef.value;
//   boardRef.value[getIndex(i, j + 4)] = turnRef.value;
//   if (checkOverFiveAllDirection(i, j + 4)) {
//     alert("Success : over five horizontal test");
//     boardRef.value[getIndex(i, j + 4)] = BoardState.Empty;
//   } else {
//     alert("Fail : over five horizontal test");
//   }
//
//   const i2 = 1;
//   const j2 = 15;
//   for (let k = 0; k < 4; ++k) {
//     boardRef.value[getIndex(i2 + k, j2)] = turnRef.value;
//   }
//   boardRef.value[getIndex(i2 + 5, j2)] = turnRef.value;
//   boardRef.value[getIndex(i2 + 4, j2)] = turnRef.value;
//   if (checkOverFiveAllDirection(i2 + 4, j2)) {
//     alert("Success : over five vertical test");
//     boardRef.value[getIndex(i2 + 4, j2)] = BoardState.Empty;
//   } else {
//     alert("Fail : over five vertical test");
//   }
//
//   const i3 = 2;
//   const j3 = 1;
//
//   for (let k = 0; k < 4; ++k) {
//     boardRef.value[getIndex(i3 + k, j3 + k)] = turnRef.value;
//   }
//   boardRef.value[getIndex(i3 + 5, j3 + 5)] = turnRef.value;
//   boardRef.value[getIndex(i3 + 4, j3 + 4)] = turnRef.value;
//
//   if (checkOverFiveAllDirection(i3 + 4, j3 + 4)) {
//     alert("Success : over five diagonal right direction test");
//     boardRef.value[getIndex(i3 + 4, j3 + 4)] = BoardState.Empty;
//   } else {
//     alert("Fail : over five diagonal right direction test");
//   }
//
//   const i4 = 3;
//   const j4 = 13;
//
//   for (let k = 0; k < 4; ++k) {
//     boardRef.value[getIndex(i4 + k, j4 - k)] = turnRef.value;
//   }
//   boardRef.value[getIndex(i4 + 5, j4 - 5)] = turnRef.value;
//   boardRef.value[getIndex(i4 + 4, j4 - 4)] = turnRef.value;
//   if (checkOverFiveAllDirection(i4 + 4, j4 - 4)) {
//     alert("Success : over five diagonal left direction test");
//     boardRef.value[getIndex(i4 + 4, j4 - 4)] = BoardState.Empty;
//   } else {
//     alert("Fail : over five diagonal left direction test");
//   }
// };
//
// // END TEST CODE

const ROWS = 15;
const COLUMNS = 15;
const boardRef = ref<BoardState[]>(
  Array.from({ length: ROWS * COLUMNS }, () => BoardState.Empty),
);
const turnRef = ref(BoardState.Black);
const alertDialogRef = ref(false);
const alertMessage = ref("");

const alertDialogToggle = () => (alertDialogRef.value = !alertDialogRef.value);

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
  if (!isInRange(i) || !isInRange(j)) return;
  if (boardRef.value[getIndex(i, j)] !== BoardState.Empty) return;

  boardRef.value[getIndex(i, j)] = turnRef.value;

  if (turnRef.value === BoardState.Black && checkForbidden(i, j)) {
    alertDialogToggle();
    boardRef.value[getIndex(i, j)] = BoardState.Empty;
    return;
  }

  if (checkWinner()) {
    console.log("winner : ", turnRef.value);
    return;
  }
  turnRef.value =
    turnRef.value === BoardState.Black ? BoardState.White : BoardState.Black;
};

const pointer = (i: number, j: number) => {
  if (boardRef.value[getIndex(i, j)] === BoardState.Empty) {
    return "pointer";
  }
  return "not-allowed";
};

const checkForbidden = (i: number, j: number) => {
  return checkDoubleThree(i, j) || checkDoubleFour(i, j) || checkOverFive(i, j);
};

const checkDoubleThreeAllDirection = (i: number, j: number) => {
  let count = 0;

  for (let d = 0; d < startMoved.length; ++d) {
    count += checkDoubleThreeOneDirection(i, j, d);
  }
  return count;
};
const checkDoubleThree = (i: number, j: number) => {
  const count = checkDoubleThreeAllDirection(i, j);
  if (count >= 2) {
    alertMessage.value = "그곳에 착수할 수 없습니다(삼삼)";
    return true;
  }
  return false;
};

const checkDoubleThreeOneDirection = (i: number, j: number, d: number) => {
  let count = 0;
  for (let k = 0; k < 4; ++k) {
    const startRow = i + k * startMoved[d][0];
    const startCol = j + k * startMoved[d][1];

    if (
      !isInRange(startRow) ||
      !isInRange(startCol) ||
      boardRef.value[getIndex(startRow, startCol)] !== BoardState.Black
    ) {
      continue;
    }

    let stoneCount = 0;
    let canEmpty = 1;
    for (let m = 0; m < 5; ++m) {
      const currRow = startRow + m * currMoved[d][0];
      const currCol = startCol + m * currMoved[d][1];

      if (!isValidCondition(currRow, currCol, canEmpty)) {
        break;
      }

      if (boardRef.value[getIndex(currRow, currCol)] === BoardState.Empty) {
        canEmpty--;
      } else {
        stoneCount++;
        if (
          stoneCount === 3 &&
          isValidThreeStones(startRow, startCol, canEmpty, d)
        ) {
          count++;
          break;
        }
      }
    }
  }
  return count;
};

const isValidThreeStones = (
  startRow: number,
  startCol: number,
  canEmpty: number,
  d: number,
) => {
  if (canEmpty) {
    if (
      !isInRange(startRow + startMoved[d][0]) ||
      !isInRange(startCol + startMoved[d][1]) ||
      boardRef.value[
        getIndex(startRow + startMoved[d][0], startCol + startMoved[d][1])
      ] === BoardState.White
    ) {
      return false;
    }
    if (
      (isInRange(startRow + startMoved[d][0]) &&
        isInRange(startCol + startMoved[d][1]) &&
        boardRef.value[
          getIndex(startRow + startMoved[d][0], startCol + startMoved[d][1])
        ] === BoardState.Black) ||
      (isInRange(startRow + currMoved[d][0] * 3) &&
        isInRange(startCol + currMoved[d][1] * 3) &&
        boardRef.value[
          getIndex(
            startRow + currMoved[d][0] * 3,
            startCol + currMoved[d][1] * 3,
          )
        ] === BoardState.Black)
    ) {
      return false;
    }

    if (
      !isInRange(startRow + currMoved[d][0] * 3) ||
      !isInRange(startCol + currMoved[d][1] * 3) ||
      boardRef.value[
        getIndex(startRow + currMoved[d][0] * 3, startCol + currMoved[d][1] * 3)
      ] === BoardState.White
    ) {
      return false;
    }

    if (
      isInRange(startRow + startMoved[d][0] * 2) &&
      isInRange(startCol + startMoved[d][1] * 2) &&
      boardRef.value[
        getIndex(
          startRow + startMoved[d][0] * 2,
          startCol + startMoved[d][1] * 2,
        )
      ] === BoardState.White &&
      isInRange(startRow + currMoved[d][0] * 4) &&
      isInRange(startCol + currMoved[d][1] * 4) &&
      boardRef.value[
        getIndex(startRow + currMoved[d][0] * 4, startCol + currMoved[d][1] * 4)
      ] === BoardState.White
    ) {
      return false;
    }
  } else {
    if (
      !isInRange(startRow + startMoved[d][0]) ||
      !isInRange(startCol + startMoved[d][1]) ||
      boardRef.value[
        getIndex(startRow + startMoved[d][0], startCol + startMoved[d][1])
      ] === BoardState.White
    ) {
      return false;
    }
    if (
      !isInRange(startRow + currMoved[d][0] * 4) ||
      !isInRange(startCol + currMoved[d][1] * 4) ||
      boardRef.value[
        getIndex(startRow + currMoved[d][0] * 4, startCol + currMoved[d][1] * 4)
      ] === BoardState.White
    ) {
      return false;
    }

    if (
      (isInRange(startRow + startMoved[d][0]) &&
        isInRange(startCol + startMoved[d][1]) &&
        boardRef.value[
          getIndex(startRow + startMoved[d][0], startCol + startMoved[d][1])
        ] === BoardState.Black) ||
      (isInRange(startRow + currMoved[d][0] * 4) &&
        isInRange(startCol + currMoved[d][1] * 4) &&
        boardRef.value[
          getIndex(
            startRow + currMoved[d][0] * 4,
            startCol + currMoved[d][1] * 4,
          )
        ] === BoardState.Black)
    ) {
      return false;
    }
  }
  return true;
};

const checkDoubleFour = (i: number, j: number) => {
  const count = checkDoubleFourAllDirection(i, j);
  if (count >= 2) {
    alertMessage.value = "그곳에 착수할 수 없습니다(사사)";
    return true;
  }
  return false;
};

// <- ->
// ↖ ↘
// ↑ ↓
// ↗ ↙
const startMoved = [
  [0, -1],
  [-1, -1],
  [-1, 0],
  [1, -1],
];
const currMoved = [
  [0, 1],
  [1, 1],
  [1, 0],
  [-1, 1],
];

const isValidFourStones = (
  startRow: number,
  startCol: number,
  canEmpty: number,
  d: number,
) => {
  if (canEmpty) {
    if (
      !isInRange(startRow + startMoved[d][0]) ||
      !isInRange(startCol + startMoved[d][1]) ||
      boardRef.value[
        getIndex(startRow + startMoved[d][0], startCol + startMoved[d][1])
      ] === BoardState.White
    ) {
      if (
        !isInRange(startRow + currMoved[d][0] * 4) ||
        !isInRange(startCol + currMoved[d][1] * 4) ||
        boardRef.value[
          getIndex(
            startRow + currMoved[d][0] * 4,
            startCol + currMoved[d][1] * 4,
          )
        ] === BoardState.White
      ) {
        return false;
      }
    }
    if (
      (isInRange(startRow + startMoved[d][0]) &&
        isInRange(startCol + startMoved[d][1]) &&
        boardRef.value[
          getIndex(startRow + startMoved[d][0], startCol + startMoved[d][1])
        ] === BoardState.Black) ||
      (isInRange(startRow + currMoved[d][0] * 4) &&
        isInRange(startCol + currMoved[d][1] * 4) &&
        boardRef.value[
          getIndex(
            startRow + currMoved[d][0] * 4,
            startCol + currMoved[d][1] * 4,
          )
        ] === BoardState.Black)
    ) {
      return false;
    }
  } else {
    if (
      (isInRange(startRow + startMoved[d][0]) &&
        isInRange(startCol + startMoved[d][1]) &&
        boardRef.value[
          getIndex(startRow + startMoved[d][0], startCol + startMoved[d][1])
        ] === BoardState.Black) ||
      (isInRange(startRow + currMoved[d][0] * 5) &&
        isInRange(startCol + currMoved[d][1] * 5) &&
        boardRef.value[
          getIndex(
            startRow + currMoved[d][0] * 5,
            startCol + currMoved[d][1] * 5,
          )
        ] === BoardState.Black)
    ) {
      return false;
    }
  }
  return true;
};
const isValidCondition = (
  currRow: number,
  currCol: number,
  canEmpty: number,
) => {
  return (
    (isInRange(currRow) &&
      isInRange(currCol) &&
      boardRef.value[getIndex(currRow, currCol)] === BoardState.Black) ||
    (boardRef.value[getIndex(currRow, currCol)] === BoardState.Empty &&
      canEmpty)
  );
};
const checkDoubleFourOneDirection = (i: number, j: number, d: number) => {
  let count = 0;
  for (let k = 0; k < 5; ++k) {
    const startRow = i + k * startMoved[d][0];
    const startCol = j + k * startMoved[d][1];

    if (
      !isInRange(startRow) ||
      !isInRange(startCol) ||
      boardRef.value[getIndex(startRow, startCol)] !== BoardState.Black
    ) {
      continue;
    }

    let stoneCount = 0;
    let canEmpty = 1;
    for (let m = 0; m < 5; ++m) {
      const currRow = startRow + m * currMoved[d][0];
      const currCol = startCol + m * currMoved[d][1];

      if (!isValidCondition(currRow, currCol, canEmpty)) {
        break;
      }

      if (boardRef.value[getIndex(currRow, currCol)] === BoardState.Empty) {
        canEmpty--;
      } else {
        stoneCount++;
        if (
          stoneCount === 4 &&
          isValidFourStones(startRow, startCol, canEmpty, d)
        ) {
          count++;
          break;
        }
      }
    }
  }
  return count;
};
const checkDoubleFourAllDirection = (i: number, j: number) => {
  let count = 0;

  for (let d = 0; d < startMoved.length; ++d) {
    count += checkDoubleFourOneDirection(i, j, d);
  }
  return count;
};

const checkOverFive = (i: number, j: number) => {
  if (checkOverFiveAllDirection(i, j)) {
    alertMessage.value = "그곳에 착수할 수 없습니다(장목)";
    return true;
  }
  return false;
};

const checkOverFiveAllDirection = (i: number, j: number) => {
  for (let d = 0; d < currMoved.length; ++d) {
    let count = 0;
    for (let k = 1; k < 5; ++k) {
      const currRow = i + k * currMoved[d][0];
      const currCol = j + k * currMoved[d][1];
      if (
        !isInRange(currRow) ||
        !isInRange(currCol) ||
        boardRef.value[getIndex(currRow, currCol)] !== turnRef.value
      ) {
        break;
      }
      count++;
    }
    for (let k = 1; k < 5; ++k) {
      const currRow = i - k * currMoved[d][0];
      const currCol = j - k * currMoved[d][1];
      if (
        !isInRange(currRow) ||
        !isInRange(currCol) ||
        boardRef.value[getIndex(currRow, currCol)] !== turnRef.value
      ) {
        break;
      }
      count++;
    }
    if (count >= 5) {
      return true;
    }
  }
  return false;
};

const isInRange = (num: number) => {
  return num > 0 && num < 16;
};

const checkWinner = () => {
  for (let row = 1; row < ROWS + 1; ++row) {
    for (let col = 1; col < COLUMNS + 1; ++col) {
      if (boardRef.value[getIndex(row, col)] === turnRef.value) {
        const winner =
          checkHorizontal(row, col) ||
          checkVertical(row, col) ||
          checkDiagonal(row, col);
        if (winner) {
          turnRef.value = BoardState.Empty;
          return winner;
        }
      }
    }
  }
  return null;
};

const checkHorizontal = (row: number, col: number) => {
  for (let i = 0; i < 5; ++i) {
    if (boardRef.value[getIndex(row, col + i)] !== turnRef.value) {
      return null;
    }
  }
  return turnRef.value;
};

const checkVertical = (row: number, col: number) => {
  for (let i = 0; i < 5; ++i) {
    if (boardRef.value[getIndex(row + i, col)] !== turnRef.value) {
      return null;
    }
  }
  return turnRef.value;
};

const checkDiagonal = (row: number, col: number) => {
  return (
    checkDiagonalLeftDirection(row, col) ||
    checkDiagonalRightDirection(row, col)
  );
};

const checkDiagonalLeftDirection = (row: number, col: number) => {
  for (let i = 0; i < 5; ++i) {
    if (boardRef.value[getIndex(row + i, col - i)] !== turnRef.value) {
      return null;
    }
  }
  return turnRef.value;
};

const checkDiagonalRightDirection = (row: number, col: number) => {
  for (let i = 0; i < 5; ++i) {
    if (boardRef.value[getIndex(row + i, col + i)] !== turnRef.value) {
      return null;
    }
  }
  return turnRef.value;
};
</script>

<style scoped lang="scss">
.game-board {
  height: 60rem;
  width: 60rem;
}

.row {
  height: calc(100 / 15 * 1%);
}
</style>
