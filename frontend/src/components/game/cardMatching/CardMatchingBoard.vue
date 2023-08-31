<template>
  <v-container class="w-100 h-100 d-flex flex-column justify-center">
    <Transition>
      <score-board
        v-if="cardsRef.length !== 0"
        :turn-ref="turnRef"
        :score-ref="scoreRef"
        :timeout-ref="timeoutRef"
      />
    </Transition>
    <v-container class="game-board">
      <TransitionGroup name="list">
        <card
          v-for="card in cardsRef"
          :key="`${card.value}-${card.position}`"
          :value="card.value"
          :visible="card.visible"
          :position="card.position"
          :matched="card.matched"
          :matchedBy="card.matchedBy"
          :isTurn="isTurn"
          @select-card="flipCard"
          :class="isTurn ? 'cursor' : ''"
        />
      </TransitionGroup>
    </v-container>
    <v-dialog v-model="dialogRef" class="w-50" persistent no-click-animation>
      <v-card-text
        v-if="winnerNicknameRef.length > 0"
        class="text-h4 font-italic text-white font-weight-black text-center"
      >
        {{ winnerNicknameRef }} 님이 승리하셨습니다!
        <v-icon color="green" icon="mdi-emoticon-excited" />
      </v-card-text>
    </v-dialog>
  </v-container>
</template>

<script setup lang="ts">
import Card from "@/components/game/cardMatching/Card.vue";
import { computed, onBeforeUnmount, onMounted, ref, watch } from "vue";
import { launchConfetti } from "@/plugins/confetti.js";
import { useStompStore } from "@/store/stomp";
import { useRoute } from "vue-router";
import { StompSubscription } from "@stomp/stompjs";
import { useUserStore } from "@/store/user";
import { useGameRoomStore } from "@/store/gameRoom";
import ScoreBoard from "@/components/game/cardMatching/ScoreBoard.vue";
import {
  CardMatchingCard,
  CardMatchingGamePayload,
  CardPayload,
  CurrTurn,
} from "@/types/cardMatching";

const TIMEOUT = 10;
const cardsRef = ref<CardMatchingCard[]>([]);
const userSelectionRef = ref<CardPayload[]>([]);

const { gameRoomId } = useRoute().params;
const { client } = useStompStore();
const userStore = useUserStore();
const { findNicknameById } = useGameRoomStore();
const dialogRef = ref(false);
const winnerNicknameRef = ref("");

const turnRef = ref<CurrTurn | null>(null);
const scoreRef = ref({
  player1: 0,
  player2: 0,
});
const timeoutRef = ref(TIMEOUT);

let gameSubscription: StompSubscription;
onMounted(() => {
  gameSubscription = client.subscribe(
    `/topic/card-matching/${gameRoomId}`,
    (payload) => {
      const message = JSON.parse(payload.body) as CardMatchingGamePayload;

      switch (message.type) {
        case "INIT_BOARD": {
          const { cards } = message.content;
          cardsRef.value = cards;
          userStore.startGame();
          break;
        }
        case "FLIP_CARD": {
          const content = message.content as CardPayload;
          cardsRef.value[content.position].visible = true;
          if (userSelectionRef.value[0]) {
            if (userSelectionRef.value[0].position === content.position) return;
            userSelectionRef.value[1] = content;

            if (turnRef?.value?.id === userStore.user?.id) {
              client.publish({
                destination: `/app/card-matching/${gameRoomId}`,
                body: JSON.stringify({
                  type: "CHECK_MATCH",
                  content: {
                    card1: userSelectionRef.value[0],
                    card2: userSelectionRef.value[1],
                  },
                }),
              });
            }
          } else {
            userSelectionRef.value[0] = content;
          }
          break;
        }
        case "MATCHED_CARD": {
          const { id, position1, position2 } = message.content;
          cardsRef.value[position1].matched = true;
          cardsRef.value[position2].matched = true;
          cardsRef.value[position1].matchedBy = id;
          cardsRef.value[position2].matchedBy = id;
          break;
        }
        case "TURN_CHANGE": {
          turnRef.value = message.content;
          clearInterval(timeoutIntervalId);
          break;
        }

        case "SCORE_CHANGE":
          scoreRef.value.player1 = message.content.player1Score;
          scoreRef.value.player2 = message.content.player2Score;
          break;
        case "FINISH_GAME": {
          clearInterval(timeoutIntervalId);

          dialogRef.value = true;
          const { winnerId } = message.content;
          winnerNicknameRef.value = findNicknameById(winnerId);

          if (winnerId === userStore.user?.id) {
            launchConfetti();
          }

          setTimeout(() => {
            scoreRef.value.player1 = 0;
            scoreRef.value.player2 = 0;
            turnRef.value = null;
            dialogRef.value = false;
            userStore.finishGame();
            cardsRef.value.forEach((card) => {
              card.visible = false;
              card.matched = false;
            });
            cardsRef.value = [];
          }, 5000);

          break;
        }
      }
    },
  );
});

onBeforeUnmount(() => {
  gameSubscription.unsubscribe();
  clearInterval(timeoutIntervalId);
});

const isTurn = computed(() => {
  if (!turnRef.value) return false;
  return turnRef.value.id === userStore.user?.id;
});

const flipCard = (payload: CardPayload) => {
  if (cardsRef.value[payload.position].visible) return;

  const json = JSON.stringify({
    type: "FLIP_CARD",
    content: payload,
  });

  client.publish({
    destination: `/app/card-matching/${gameRoomId}`,
    body: json,
  });
};

let timeoutIntervalId: NodeJS.Timer;
watch(turnRef, () => {
  if (!turnRef.value) return;

  timeoutRef.value = TIMEOUT;
  timeoutIntervalId = setInterval(timeoutInterval, 1000);
});

const timeoutInterval = () => {
  if (timeoutRef.value > 0) {
    timeoutRef.value--;
  } else {
    if (turnRef?.value?.id === userStore.user?.id) {
      client.publish({
        destination: `/app/card-matching/${gameRoomId}`,
        body: JSON.stringify({
          type: "TIMEOUT",
        }),
      });
    }
    if (userSelectionRef.value[0]) {
      cardsRef.value[userSelectionRef.value[0].position].visible = false;
      userSelectionRef.value.length = 0;
    }
  }
};

watch(
  userSelectionRef,
  (currentValue: CardPayload[]) => {
    if (currentValue.length === 2) {
      const cardOne = currentValue[0];
      const cardTwo = currentValue[1];

      if (cardOne.faceValue === cardTwo.faceValue) {
        cardsRef.value[cardOne.position].matched = true;
        cardsRef.value[cardTwo.position].matched = true;
      } else {
        setTimeout(() => {
          cardsRef.value[cardOne.position].visible = false;
          cardsRef.value[cardTwo.position].visible = false;
        }, 1000);
      }

      userSelectionRef.value.length = 0;
    }
  },
  { deep: true },
);
</script>

<style scoped>
.game-board {
  width: 80%;
  height: 90%;
  display: grid;
  grid-template-columns: repeat(6, 1fr);
  grid-template-rows: repeat(6, 1fr);
  grid-gap: 2%;
  align-self: center;
}

.cursor {
  cursor: pointer;
}

.v-enter-from {
  opacity: 0;
  transform: translateY(-50px);
}

.v-enter-active {
  transition: all 1.5s ease-in-out;
  transform: translateY(0);
}

.list-enter-from,
.list-leave-to {
  opacity: 0;
  transform: translateY(-50px);
}

.list-enter-active,
.list-leave-active {
  transition: all 1.5s ease-in-out;
  transform: translateY(0);
}
</style>
