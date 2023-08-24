<template>
  <v-container class="d-flex flex-column">
    <!--    <v-row>-->
    <!--      <v-col>-->
    <!--        <v-btn-->
    <!--          class="w-100 h-100 bg-deep-purple-accent-3 text-h6 font-weight-black"-->
    <!--          @click="inviteToggleDialog"-->
    <!--        >-->
    <!--          초대하기-->
    <!--        </v-btn>-->

    <!--        <v-dialog-->
    <!--          class="w-25 h-75 bg-secondary"-->
    <!--          :model-value="inviteToggle"-->
    <!--          @click:outside="inviteToggleDialog"-->
    <!--        >-->
    <!--          <v-card-title class="text-center text-h5 font-weight-black">-->
    <!--            초대하기-->
    <!--          </v-card-title>-->
    <!--          <v-card class="bg-primary h-100">-->
    <!--            <v-card-text class="text-center text-h5 font-weight-black">-->
    <!--              <v-container class="px-0 d-flex flex-column justify-space-around">-->
    <!--                <v-row v-for="index in 3" :key="index">-->
    <!--                  <v-col>-->
    <!--                    <v-btn class="w-100 bg-primary">1</v-btn>-->
    <!--                  </v-col>-->
    <!--                </v-row>-->
    <!--              </v-container>-->
    <!--            </v-card-text>-->
    <!--          </v-card>-->
    <!--          <v-container>-->
    <!--            <v-row>-->
    <!--              <v-col>-->
    <!--                <v-card-actions>-->
    <!--                  <v-btn-->
    <!--                    class="bg-green text-h6 font-weight-black"-->
    <!--                    block-->
    <!--                    @click="$emit('click-yes')"-->
    <!--                    >초대하기</v-btn-->
    <!--                  >-->
    <!--                </v-card-actions>-->
    <!--              </v-col>-->
    <!--            </v-row>-->
    <!--          </v-container>-->
    <!--        </v-dialog>-->
    <!--      </v-col>-->
    <!--    </v-row>-->

    <v-row class="h-50">
      <v-col>
        <game-room-ready-button />
      </v-col>
    </v-row>
    <v-row class="h-50">
      <v-col>
        <v-btn
          class="w-100 h-100 bg-red text-h6 font-weight-black"
          @click="exitDialog"
        >
          나가기
        </v-btn>

        <default-dialog
          negative
          :dialog="exitToggleRef"
          @toggleDialog="exitDialog"
          @clickYes="exitYes"
        >
          <template v-slot:title>나가기</template>
          <template v-slot:text>게임방에서 나가시겠습니까?</template>
        </default-dialog>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup lang="ts">
import { ref } from "vue";
import DefaultDialog from "@/components/common/DefaultDialog.vue";
import { useRoute, useRouter } from "vue-router";
import GameRoomReadyButton from "@/components/gameRoom/GameRoomGameButton.vue";
import { leaveGameRoom } from "@/api/game";
import { useGameRoomStore } from "@/store/gameRoom";

const exitToggleRef = ref(false);
const { gameTitle, gameRoomId } = useRoute().params as {
  gameTitle: string;
  gameRoomId: string;
};
const router = useRouter();
const gameRoomStore = useGameRoomStore();

const exitDialog = () => (exitToggleRef.value = !exitToggleRef.value);

// TODO: 초대 기능
// const inviteToggle = ref(false);
// const inviteToggleDialog = () => {
//   inviteToggle.value = !inviteToggle.value;
// };
const exitYes = async () => {
  gameRoomStore.destroyGameRoom();
  try {
    await leaveGameRoom(gameTitle, gameRoomId);
  } catch (e) {
    /* empty */
  } finally {
    await router.replace(`/games/${gameTitle}`);
  }
};
</script>
