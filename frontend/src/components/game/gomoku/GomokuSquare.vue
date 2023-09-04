<template>
  <v-container
    class="pa-0 w-100 h-100 square position-relative d-flex justify-center align-center"
    @mouseenter="hoverRef = true"
    @mouseleave="hoverRef = false"
  >
    <div class="position-absolute bg-black row" :style="rowEnd" />
    <div class="position-absolute bg-black col" :style="colEnd" />
    <div v-if="isDot" class="position-absolute bg-black dot rounded-circle" />
    <div
      v-if="state === BoardState.Black || state === BoardState.White"
      class="stone"
      :class="[stoneColor]"
      :style="prevStoneUI"
    />
    <div v-else-if="checkHover" class="stone" :style="hoverStyle" />
  </v-container>
</template>

<script setup lang="ts">
import { computed, ref, toRefs } from "vue";
import { BoardState, CurrTurn, GomokuStoneColor, Stone } from "@/types/gomoku";
import { useUserStore } from "@/store/user";

const props = defineProps<{
  row: number;
  col: number;
  COLUMNS: number;
  state: BoardState;
  turn: CurrTurn | null;
  userStoneColor: GomokuStoneColor | null;
  prevStone: Stone | null;
}>();
const { row, col, COLUMNS, state, turn, userStoneColor, prevStone } =
  toRefs(props);
const hoverRef = ref(false);
const { user } = useUserStore();

const isDot = computed(() => {
  return (
    (row.value + 1) % 4 === 0 &&
    (col.value + 1) % 4 === 0 &&
    ((row.value + col.value + 2) / 4) % 2 === 0
  );
});

const rowEnd = computed(() => {
  if (col.value === 0) {
    return "width:50%;left:50%";
  }
  if (col.value === COLUMNS.value - 1) {
    return "width:50%;left:0%;";
  }
  return "width:100%";
});

const colEnd = computed(() => {
  if (row.value === 0) {
    return "height:50%;top:50%";
  }
  if (row.value === COLUMNS.value - 1) {
    return "height:50%;top:0%";
  }
  return "height:100%";
});

const stoneColor = computed(() => {
  return state.value === BoardState.Black ? "bg-black" : "bg-white";
});

const hoverStyle = computed(() => {
  if (!turn.value || !userStoneColor.value) return;

  if (turn.value.id === user?.id && hoverRef.value) {
    if (turn.value.id === userStoneColor.value.blackUserId) {
      return "background-color:rgba(0,0,0,0.5); width: 70%; height: 70%;";
    }
    return "background-color:rgba(255,255,255,0.5); width: 70%; height: 70%;";
  }
  return "";
});

const checkHover = computed(() => {
  return (
    state.value === BoardState.Empty &&
    hoverRef.value &&
    turn.value?.id === user?.id
  );
});

const prevStoneUI = computed(() => {
  if (!prevStone.value) return;

  if (
    prevStone.value.row === row.value + 1 &&
    prevStone.value.col === col.value + 1
  ) {
    return "border: 2px solid red;position: absolute;";
  }
  return "";
});
</script>

<style scoped>
.square {
  background-color: #f79e1a;
}

.row {
  height: 2px;
}

.col {
  width: 2px;
}

.dot {
  width: 15%;
  height: 15%;
}

.stone {
  width: 70%;
  height: 70%;
  border-radius: 50%;
  z-index: 1;
  box-shadow: 0 0 5px rgba(0, 0, 0, 0.5);
}
</style>
