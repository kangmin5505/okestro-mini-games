<template>
  <v-main class="d-flex align-center justify-center">
    <v-container
      class="w-100 h-25 d-flex flex-column justify-center align-center"
    >
      <v-row class="w-100">
        <v-col class="align-center d-flex flex-column">
          <v-img src="@/assets/logo.png" class="rounded-circle w-25" />
        </v-col>
      </v-row>
      <v-row>
        <v-col>
          <v-card
            class="w-100 bg-tertiary text-h4 font-weight-bold font-italic pa-5"
            flat
          >
            요청하신 페이지를 찾을 수 없습니다.
          </v-card>
        </v-col>
      </v-row>
      <v-row class="w-25">
        <v-col>
          <v-btn
            class="pa-3 w-100 h-100 text-h4 font-weight-black"
            color="btn-primary"
            @click="router.replace('/')"
          >
            로그인 화면으로
          </v-btn>
        </v-col>
      </v-row>
    </v-container>
  </v-main>
</template>

<script setup lang="ts">
import { useRouter } from "vue-router";

const router = useRouter();
</script>
