<template>
  <v-dialog
    class="dialog-container"
    v-model="errorModal.errorModal"
    @click:outside="clickYes"
  >
    <v-card class="bg-primary">
      <v-card-title class="text-center text-h5 font-weight-black">
        {{ errorModal.title }}
      </v-card-title>
      <v-card-text class="text-center text-h6 font-weight-black">
        {{ errorModal.content }}
      </v-card-text>
      <v-container>
        <v-row>
          <v-col>
            <v-card-actions>
              <v-btn
                class="text-h6 font-weight-black bg-green"
                block
                @click="clickYes"
                >확인</v-btn
              >
            </v-card-actions>
          </v-col>
        </v-row>
      </v-container>
    </v-card>
  </v-dialog>
</template>

<script setup lang="ts">
import { useErrorModalStore } from "@/store/errorModal";
import { useRouter } from "vue-router";

const router = useRouter();

const { errorModal, closeModal } = useErrorModalStore();
const clickYes = () => {
  if (errorModal.to) {
    router.replace(errorModal.to);
  }
  closeModal();
};
</script>

<style scoped>
.dialog-container {
  width: 30%;
}
</style>
