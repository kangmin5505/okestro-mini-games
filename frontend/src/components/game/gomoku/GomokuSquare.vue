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
    />
    <div
      v-else-if="state === BoardState.Empty && hoverRef"
      class="stone"
      :style="isHover"
    />
  </v-container>
</template>

<script setup lang="ts">
import { computed, ref, toRefs } from "vue";
import { BoardState } from "@/types/gomoku";

const props = defineProps<{
  row: number;
  col: number;
  COLUMNS: number;
  state: BoardState;
  turn: BoardState;
}>();
const { row, col, COLUMNS, state, turn } = toRefs(props);
const hoverRef = ref(false);

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

const isHover = computed(() => {
  if (hoverRef.value) {
    if (turn.value === BoardState.Black) {
      return "background-color:rgba(0,0,0,0.5); width: 70%; height: 70%;";
    }
    return "background-color:rgba(255,255,255,0.5); width: 70%; height: 70%;";
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
