<template>
  <v-dialog
    class="w-25"
    :model-value="dialog"
    @click:outside="$emit('toggle-dialog')"
    @keydown.enter="$emit('toggle-dialog')"
    no-click-animation
    persistent
  >
    <v-card class="bg-primary">
      <v-card-title class="text-center text-h4 font-weight-black my-3">
        알림
      </v-card-title>
      <v-card-text class="text-center text-h5">
        {{ message }}
      </v-card-text>
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
defineProps<{
  dialog: boolean;
  message: string;
}>();
defineEmits<{
  (event: "toggle-dialog"): void;
}>();
</script>
