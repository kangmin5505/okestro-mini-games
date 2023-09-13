<template>
  <v-navigation-drawer color="tertiary" width="100" permanent>
    <drawer-button
      title="홈화면"
      prepend-icon="mdi-home"
      :to="{ name: 'Games' }"
    />

    <v-divider class="mx-3" />

    <drawer-button
      title="로그아웃"
      prepend-icon="mdi-logout"
      @click="logoutDialog"
    />
    <default-dialog
      negative
      :dialog="dialogRef"
      @toggleDialog="logoutDialog"
      @clickYes="logoutYes"
    >
      <template v-slot:title>로그아웃</template>
      <template v-slot:text>로그아웃 하시겠습니까?</template>
    </default-dialog>

    <drawer-button
      v-if="user"
      title="내정보"
      prepend-icon="mdi-account"
      :to="{ name: 'UserInfo', params: { userId: user?.id } }"
    />

    <drawer-button
      title="랭킹"
      prepend-icon="mdi-crown"
      :to="{ name: 'Ranking' }"
    />
  </v-navigation-drawer>
</template>

<script setup lang="ts">
import DefaultDialog from "@/components/common/DefaultDialog.vue";
import { ref } from "vue";
import { useUserStore } from "@/store/user";
import { storeToRefs } from "pinia";
import DrawerButton from "@/components/common/DrawerButton.vue";
import { logoutUser } from "@/api/auth";

const dialogRef = ref(false);
const { user } = storeToRefs(useUserStore());

const logoutDialog = () => (dialogRef.value = !dialogRef.value);
const logoutYes = async () => {
  try {
    await logoutUser();
    logoutDialog();
  } catch (e) {
    /* empty */
  } finally {
    window.location.replace("/");
  }
};
</script>
