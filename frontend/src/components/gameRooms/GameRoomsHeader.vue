<template>
  <v-container class="d-flex justify-center" fluid>
    <v-row>
      <v-col>
        <v-card class="d-flex justify-end h-100 bg-primary" flat>
          <create-game-room-button />
          <v-btn
            color="btn-primary"
            class="ml-3 h-100"
            @click="$emit('refresh')"
          >
            <v-icon size="32" icon="mdi-refresh" />
          </v-btn>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup lang="ts">
import CreateGameRoomButton from "@/components/gameRooms/CreateGameRoomButton.vue";

defineEmits<{
  (event: "refresh"): void;
}>();
</script>
