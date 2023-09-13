<template>
  <v-btn
    :prepend-icon="prependIcon"
    rounded
    class="text-h4 ma-3"
    size="70"
    color="btn-primary"
    @click="$emit('click')"
  >
    <v-tooltip activator="parent" location="end">{{ title }} </v-tooltip>
  </v-btn>
</template>

<script setup lang="ts">
defineProps<{
  prependIcon: string;
  title: string;
}>();

defineEmits<{
  (event: "click"): void;
}>();
</script>
