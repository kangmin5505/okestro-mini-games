<template>
  <error-modal />

  <v-app>
    <router-view />
  </v-app>
</template>

<script lang="ts" setup>
import ErrorModal from "@/components/common/ErrorModal.vue";
</script>
