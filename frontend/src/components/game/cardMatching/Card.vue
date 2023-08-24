<template>
  <v-container
    class="h-100 w-100 position-relative d-flex justify-center align-center card"
    :class="flippedStyles"
    @click="selectCard"
  >
    <v-img
      class="w-100 h-100 d-flex justify-center align-center position-absolute rounded-xl is-front"
      :src="`/images/card-matching/${value}.png`"
      :alt="value"
    >
      <v-icon
        v-if="matched"
        icon="mdi-check-circle"
        class="position-absolute matched"
        :color="
          matchedBy === gameRoom?.player1.id ? 'indigo-darken-4' : 'white'
        "
      />
    </v-img>
    <v-img
      class="w-100 h-100 d-flex justify-center align-center position-absolute rounded-xl bg-tertiary card-face"
      src="@/assets/logo.png"
      cover
    />
  </v-container>
</template>

<script setup lang="ts">
import { CardPayload } from "@/types/cardMatching";
import { computed, toRefs } from "vue";
import { useGameRoomStore } from "@/store/gameRoom";

const props = defineProps<{
  value: string;
  visible: boolean;
  position: number;
  matched: boolean;
  isTurn: boolean;
  matchedBy: string | null;
}>();
const { value, visible, position, isTurn } = toRefs(props);
const { gameRoom } = useGameRoomStore();

const emit = defineEmits<{
  (event: "select-card", value: CardPayload): void;
}>();

const selectCard = () => {
  if (!isTurn.value) return;
  emit("select-card", { position: position.value, faceValue: value.value });
};

const flippedStyles = computed(() => {
  return visible.value ? "is-flipped" : "";
});
</script>

<style scoped>
.matched {
  right: 5px;
  bottom: 5px;
}

.card {
  transition: 0.5s transform ease-in-out;
  transform-style: preserve-3d;
}

.is-front {
  transform: rotateY(180deg);
}

.is-flipped {
  transform: rotateY(180deg);
}

.card-face {
  backface-visibility: hidden;
}
</style>
