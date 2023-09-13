import { useCustomAxios } from "@/api/index";

export const getGames = async () => {
  const response = await useCustomAxios("/games");
  return response?.data.value.games;
};

export const getGameRooms = async (gameTitle: string) => {
  const response = await useCustomAxios(
    `/games/${gameTitle}`,
    {},
    { errorTitle: "게임 찾기 실패", to: { name: "Games" } },
  );
  return response?.data.value;
};

export const createGameRoom = async (
  gameTitle: string,
  gameRoomTitle: string,
) => {
  const response = await useCustomAxios(
    `/games/${gameTitle}`,
    { data: { title: gameRoomTitle }, method: "POST" },
    {
      errorTitle: "방만들기 실패",
    },
  );
  return response?.data.value;
};

export const joinGameRoom = async (gameTitle: string, gameRoomId: string) => {
  const response = await useCustomAxios(`/games/${gameTitle}/${gameRoomId}`);
  return response?.data.value;
};

export const leaveGameRoom = async (gameTitle: string, gameRoomId: string) => {
  await useCustomAxios(`/games/${gameTitle}/${gameRoomId}`, {
    method: "delete",
  });
};

export const startGame = async (gameTitle: string, gameRoomId: string) => {
  await useCustomAxios(`/games/${gameTitle}/${gameRoomId}/start`);
};
