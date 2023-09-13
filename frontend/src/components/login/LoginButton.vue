<template>
  <v-row class="w-100">
    <v-col>
      <v-btn
        class="pa-3 w-100 h-100 text-h4"
        color="btn-primary"
        @click="login"
      >
        <v-icon>mdi-account</v-icon>
        로그인
      </v-btn>
    </v-col>
  </v-row>
</template>

<script setup lang="ts">
const login = () => {
  window.location.replace(
    `${import.meta.env.VITE_BACK_URL}/oauth2/authorization/google`,
  );
};
</script>
