<template>
  <v-pagination
    :length="pageLength"
    prev-icon="mdi-menu-left"
    next-icon="mdi-menu-right"
    v-model="pageRef"
    @click="changePage"
    class="text-center pagination-container"
  />
</template>

<script setup lang="ts">
import { computed, ref, toRefs } from "vue";

const props = defineProps<{
  totalElements: number;
  perPage: number;
}>();

const pageRef = ref(1);
const { perPage, totalElements } = toRefs(props);

const emit = defineEmits<{
  (event: "current-page", currentPage: number): void;
}>();

const changePage = () => {
  emit("current-page", pageRef.value);
};
changePage();

const pageLength = computed(() => {
  return Math.ceil(totalElements.value / perPage.value);
});
</script>

<style scoped>
.pagination-container {
  height: 5%;
}
</style>
