<template>
  <v-card
    @click="$emit('joinRoom', gameRoom.id)"
    class="h-100 bg-tertiary text-h4 position-relative"
  >
    <v-img
      class="w-100 position-absolute card-image"
      :src="`/images/${gameTitle}/${gameTitle}.png`"
      cover
      style="opacity: 0.1"
    />
    <v-container class="card-image d-flex flex-column">
      <v-row>
        <v-col>
          <v-card-text class="text-center text-h4 font-weight-black"
            >{{ gameRoom.player1.nickname }}
          </v-card-text>
        </v-col>
      </v-row>
      <v-row>
        <v-col class="text-center font-weight-black">vs</v-col>
      </v-row>
      <v-row>
        <v-col>
          <v-card-text class="text-center text-h4 font-weight-black"
            >{{ gameRoom.player2?.nickname }}
          </v-card-text>
        </v-col>
      </v-row>
    </v-container>
    <v-container class="d-flex flex-column pt-0 card-content">
      <v-card-title class="text-center text-h5"
        >{{ gameRoom.title }}
      </v-card-title>
      <v-card-subtitle class="text-end"
        >{{ gameRoom.userNum }} / {{ gameRoom.limitUserNum }}
      </v-card-subtitle>
      <v-card-subtitle class="text-end pt-0">
        {{ gameRoom.state }}
      </v-card-subtitle>
    </v-container>
  </v-card>
</template>

<script setup lang="ts">
import { GameRoom } from "@/types/game";
import { useRoute } from "vue-router";

defineProps<{
  gameRoom: GameRoom;
}>();
defineEmits<{
  (event: "joinRoom", gameRoomId: string): void;
}>();

const { gameTitle } = useRoute().params;
</script>

<style scoped>
.card-image {
  height: 70%;
}

.card-content {
  height: 30%;
}
</style>
