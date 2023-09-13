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
import { computed, onMounted, onUpdated, ref, toRefs } from "vue";

const props = defineProps<{
  items: Item[];
  perPage: number;
}>();
const emit = defineEmits<{
  (event: "change-page", currPageItems: Item[]): void;
}>();

const pageRef = ref(1);
const { items, perPage } = toRefs(props);

onMounted(() => {
  changePage();
});
onUpdated(() => {
  changePage();
});

const changePage = () => {
  emit(
    "change-page",
    items.value.slice(
      (pageRef.value - 1) * perPage.value,
      pageRef.value * perPage.value,
    ),
  );
};

const pageLength = computed(() => {
  if (items.value.length === 0) return 1;
  return Math.ceil(items.value.length / perPage.value);
});
</script>

<style scoped>
.pagination-container {
  height: 5%;
}
</style>
