<template>
  <v-card
    class="d-flex h-100 w-100 text-h4 font-italic flex-column"
    color="primary"
    flat
  >
    <v-row>
      <v-col cols="5">
        <user-avatar />
      </v-col>
      <v-col cols="7" class="d-flex flex-column w-100">
        <v-row v-for="(data, index) in userDetailRef" :key="index">
          <v-col class="px-0 d-flex align-center justify-center">
            <v-card
              class="px-10 w-100 h-100 d-flex align-center"
              color="tertiary"
              flat
            >
              <v-card-text class="text-h4 font-italic">{{ data }}</v-card-text>
            </v-card>
          </v-col>
        </v-row>
      </v-col>
    </v-row>
  </v-card>
</template>

<script setup lang="ts">
import { UserDetail } from "@/types/user";
import { onMounted, onUpdated, ref, toRef } from "vue";
import UserAvatar from "@/components/userProfile/UserAvatar.vue";
import dayjs from "dayjs";
import { getUserDetail } from "@/api/user";

const props = defineProps<{
  userId: string;
}>();
const emit = defineEmits<{
  (event: "sendUserNickname", nickname: string): void;
}>();

const userId = toRef(props, "userId");
const userDetailRef = ref<UserDetail | null>(null);

onMounted(() => {
  setUserDetail();
});

onUpdated(() => {
  setUserDetail();
});

const setUserDetail = async () => {
  try {
    userDetailRef.value = await getUserDetail(userId.value);
    if (userDetailRef.value) {
      userDetailRef.value.createdAt = dayjs(
        userDetailRef.value.createdAt,
      ).format("YYYY/MM/DD 가입");
    }
    emit("sendUserNickname", userDetailRef.value?.nickname as string);
  } catch (e) {
    /* empty */
  }
};
</script>
