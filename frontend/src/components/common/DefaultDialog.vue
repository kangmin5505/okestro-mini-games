<template>
  <v-dialog
    class="dialog-container"
    :model-value="dialog"
    @click:outside="$emit('toggle-dialog')"
    @keydown.enter="$emit('click-yes')"
    persistent
    no-click-animation
  >
    <v-card class="bg-primary">
      <v-card-title class="text-center text-h5 font-weight-black">
        <slot name="title"></slot>
      </v-card-title>
      <v-card-text class="text-center text-h5 font-weight-black">
        <slot name="text"></slot>
      </v-card-text>
      <v-container>
        <v-row>
          <v-col>
            <v-card-actions>
              <v-btn
                class="text-h6 font-weight-black"
                :class="negative ? 'bg-red' : 'bg-green'"
                block
                @click="$emit('click-yes')"
                >예</v-btn
              >
            </v-card-actions>
          </v-col>
          <v-col>
            <v-card-actions>
              <v-btn
                class="text-h6 font-weight-black"
                :class="negative ? 'bg-green' : 'bg-red'"
                block
                @click="$emit('toggle-dialog')"
                >아니오</v-btn
              >
            </v-card-actions>
          </v-col>
        </v-row>
      </v-container>
    </v-card>
  </v-dialog>
</template>

<script setup lang="ts">
defineProps<{
  negative?: boolean;
  dialog: boolean;
}>();

defineEmits<{
  (event: "toggle-dialog"): void;
  (event: "click-yes"): void;
  (event: "click-no"): void;
}>();
</script>

<style scoped>
.dialog-container {
  width: 30%;
}
</style>
