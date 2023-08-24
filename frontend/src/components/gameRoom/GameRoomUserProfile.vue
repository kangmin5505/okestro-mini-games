<template>
  <v-container class="d-flex flex-column justify-center">
    <v-row class="h-50">
      <v-col>
        <v-card
          class="h-100 text-h5 d-flex justify-center align-center bg-indigo-darken-4 font-italic font-weight-black"
          @click="user1DialogToggle"
        >
          {{ gameRoom?.player1.nickname }}
        </v-card>
      </v-col>
    </v-row>
    <v-row class="h-50">
      <v-col>
        <v-card class="h-100">
          <v-card
            v-if="gameRoom?.player2"
            class="h-100 w-100 text-h5 d-flex justify-center align-center font-italic font-weight-black text-indigo-darken-4 position-relative"
            @click="user2DialogToggle"
            flat
          >
            {{ gameRoom?.player2!.nickname }}
            <v-card
              v-if="!userStore.isOnGame && userStore.isReady"
              class="position-absolute text-green w-100 pr-2 bg-transparent ready-sign text-end align-self-auto"
              flat
              >준비
            </v-card>
          </v-card>
        </v-card>
      </v-col>
    </v-row>
    <user-info-dialog
      v-if="gameRoom?.player1"
      :dialog="user1Dialog"
      :userId="gameRoom?.player1?.id"
      @toggle-dialog="user1DialogToggle"
    />
    <user-info-dialog
      v-if="gameRoom?.player2"
      :dialog="user2Dialog"
      :userId="gameRoom?.player2?.id"
      @toggle-dialog="user2DialogToggle"
    />
  </v-container>
</template>

<script setup lang="ts">
import { useGameRoomStore } from "@/store/gameRoom";
import { useUserStore } from "@/store/user";
import { storeToRefs } from "pinia";
import { ref } from "vue";
import UserInfoDialog from "@/components/common/UserInfoDialog.vue";

const userStore = useUserStore();
const { gameRoom } = storeToRefs(useGameRoomStore());
const user1Dialog = ref(false);
const user2Dialog = ref(false);

const user1DialogToggle = () => (user1Dialog.value = !user1Dialog.value);
const user2DialogToggle = () => (user2Dialog.value = !user2Dialog.value);
</script>

<style scoped>
.ready-sign {
  right: 5px;
  font-size: 1rem;
}
</style>
