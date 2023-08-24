<template>
  <v-main class="d-flex align-center justify-center">
    <v-container
      class="w-25 h-25 d-flex flex-column justify-center align-center"
    >
      <login-title />
      <login-button />
    </v-container>
  </v-main>
</template>

<script setup lang="ts">
import LoginTitle from "@/components/login/LoginTitle.vue";
import LoginButton from "@/components/login/LoginButton.vue";
</script>
