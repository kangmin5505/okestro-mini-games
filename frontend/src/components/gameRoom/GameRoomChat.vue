<template>
  <v-container class="d-flex flex-column align-center">
    <v-row
      class="bg-secondary rounded-xl w-100 d-flex flex-column align-center chat-content h-100"
    >
      <v-col style="overflow-y: auto" id="chat">
        <v-container
          class="pa-0"
          v-for="(message, index) in chat"
          :key="index"
          :class="['d-flex flex-row align-center']"
        >
          <v-container
            v-if="message.type === 'USER'"
            class="w-100 h-100 d-flex flex-column align-start pa-0"
          >
            <v-card-text
              class="pa-0 w-75 rounded-xl text-h6 text-center font-weight-black font-italic"
              :class="[
                message.from == player1?.nickname
                  ? 'text-white'
                  : 'text-indigo-darken-4',
                message.from == player1?.nickname
                  ? 'bg-indigo-darken-4'
                  : 'bg-white',
              ]"
            >
              {{ message.from }}
            </v-card-text>
            <v-card-text class="pt-1 text-h6" style="word-break: break-all">
              {{ message.content }}
            </v-card-text>
          </v-container>
          <v-card-text
            v-else
            class="w-100 text-center font-weight-black"
            :class="message.type === 'JOIN' ? 'text-green' : 'text-red'"
          >
            {{ message.content }}
          </v-card-text>
        </v-container>
      </v-col>
    </v-row>
    <!--  채팅 입력창    -->
    <v-row class="chat-input w-100">
      <v-col class="px-0" cols="10">
        <v-text-field
          v-model="message"
          bg-color="primary"
          variant="solo"
          density="compact"
          placeholder="메시지를 입력하세요"
          autofocus
          @keyup.enter="sendMessage"
          class="w-100"
        />
      </v-col>
      <v-col cols="2" class="pr-0">
        <v-btn icon @click="sendMessage" size="40">
          <v-icon class="text-indigo-darken-4">mdi-send</v-icon>
        </v-btn>
      </v-col>
    </v-row>
  </v-container>
</template>
<script setup lang="ts">
import { onBeforeUnmount, onMounted, ref } from "vue";
import { Message } from "@/types/stomp";
import { useUserStore } from "@/store/user";
import { useStompStore } from "@/store/stomp";
import { useRoute } from "vue-router";
import { useGameRoomStore } from "@/store/gameRoom";
import { StompSubscription } from "@stomp/stompjs";
import { User } from "@/types/user";

const chat = ref<Message[]>([]);
const message = ref<Message | null>(null);
const { user } = useUserStore() as { user: User };
const { client } = useStompStore();
const {
  params: { gameTitle, gameRoomId },
} = useRoute();
const player1 = useGameRoomStore().gameRoom?.player1;

let chatSubscribe: StompSubscription;
onMounted(() => {
  chatSubscribe = client.subscribe(
    `/topic/chat/${gameTitle}/${gameRoomId}`,
    (jsonMessage) => {
      const message = JSON.parse(jsonMessage.body);

      chat.value.push({
        type: message.type,
        from: message.from,
        content: message.content,
      });
      scrollDown();
    },
  );
});
onBeforeUnmount(() => {
  chatSubscribe.unsubscribe();
});

const sendMessage = () => {
  if (!message.value) return;

  client.publish({
    destination: `/app/chat/${gameTitle}/${gameRoomId}`,
    body: JSON.stringify({
      type: "USER",
      from: user.nickname,
      content: message.value,
    }),
  });
  message.value = null;
};

const scrollDown = () => {
  const chatElem = document.querySelector("#chat");
  setTimeout(() => {
    chatElem?.scrollTo({ top: chatElem?.scrollHeight });
  }, 0);
};
</script>

<style scoped>
.chat-content {
  height: 90%;
}

.chat-input {
  height: 10%;
}

.chat-username {
  height: 10%;
}
</style>
