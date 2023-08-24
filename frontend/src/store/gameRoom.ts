import { defineStore } from "pinia";
import { GameRoom } from "@/types/game";
import { User } from "@/types/user";
import { ref } from "vue";
import { useUserStore } from "@/store/user";

export const useGameRoomStore = defineStore("game-room", () => {
  const gameRoom = ref<GameRoom | null>(null);

  const userStore = useUserStore();

  function setGameRoom(gameRoomInfo: GameRoom) {
    gameRoom.value = gameRoomInfo;
  }

  function joinPlayer(user: User) {
    if (gameRoom.value) {
      gameRoom.value.player2 = user;
      gameRoom.value.userNum++;
    }
  }

  function leavePlayer() {
    if (gameRoom.value) {
      gameRoom.value.player2 = null;
      gameRoom.value.userNum--;
      userStore.offReady();
    }
  }

  function destroyGameRoom() {
    gameRoom.value = null;
    userStore.leaveGameRoom();
  }

  function findNicknameById(id: string) {
    if (gameRoom.value) {
      if (gameRoom.value.player1.id === id) {
        return gameRoom.value.player1.nickname;
      } else if (gameRoom.value.player2 && gameRoom.value.player2.id === id) {
        return gameRoom.value.player2.nickname;
      }
    }
    return "";
  }

  return {
    gameRoom,
    setGameRoom,
    joinPlayer,
    leavePlayer,
    destroyGameRoom,
    findNicknameById,
  };
});
