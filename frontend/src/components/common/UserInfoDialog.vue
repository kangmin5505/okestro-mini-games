<template>
  <v-dialog
    class="w-50"
    :model-value="dialog"
    @click:outside="$emit('toggle-dialog')"
    @keydown.enter="$emit('toggle-dialog')"
    no-click-animation
    persistent
  >
    <v-card class="bg-primary">
      <v-card-title class="text-center text-h4 font-weight-black my-3">
        유저 정보
      </v-card-title>
      <v-container fluid>
        <v-row>
          <v-col>
            <user-basic-info :userId="userId" />
          </v-col>
        </v-row>
        <v-row>
          <v-col>
            <user-game-stat :userId="userId" />
          </v-col>
        </v-row>
      </v-container>
      <v-container>
        <v-row>
          <v-col>
            <v-card-actions>
              <v-btn
                class="text-h6 font-weight-black bg-red"
                block
                @click="$emit('toggle-dialog')"
                >닫기
              </v-btn>
            </v-card-actions>
          </v-col>
        </v-row>
      </v-container>
    </v-card>
  </v-dialog>
</template>

<script setup lang="ts">
import UserBasicInfo from "@/components/userProfile/UserBasicInfo.vue";
import UserGameStat from "@/components/userProfile/UserGameStat.vue";

defineProps<{
  dialog: boolean;
  userId: string;
}>();
defineEmits<{
  (event: "toggle-dialog"): void;
}>();
</script>
