<template>
  <v-dialog
    :model-value="dialog"
    @click="closeDialog"
    class="w-50"
    persistent
    no-click-animation
  >
    <v-card-text
      v-if="winnerNickname.length > 0"
      class="text-h4 font-italic text-white font-weight-black text-center"
    >
      {{ winnerNickname }} 님이 승리하셨습니다!
      <v-icon color="green" icon="mdi-emoticon-excited" />
    </v-card-text>
  </v-dialog>
</template>

<script setup lang="ts">
defineProps<{
  winnerNickname: string;
  dialog: boolean;
}>();
const emit = defineEmits<{
  (event: "update:dialog", value: boolean): void;
}>();

const closeDialog = () => {
  emit("update:dialog", false);
};
</script>
