import { useCustomAxios } from "@/api/index";

export const getSelfUserInfo = async () => {
  const response = await useCustomAxios("/users/self-info");
  return response?.data.value;
};

export const getUserRelativeGameStats = async (userId: string) => {
  const response = await useCustomAxios(`/users/${userId}/relative-game-stats`);

  return response?.data.value.userGameStats;
};

export const getUserGameStats = async (userId: string) => {
  const response = await useCustomAxios(`/users/${userId}/game-stats`);

  return response?.data.value.userGameStats;
};

export const getUserGameRecords = async (
  userId: string,
  userGameRecordParams: {
    page: number;
    limit: number;
  },
) => {
  const response = await useCustomAxios(`/users/${userId}/game-records`, {
    params: {
      page: userGameRecordParams.page,
      limit: userGameRecordParams.limit,
    },
  });

  return response?.data.value;
};

export const getUserDetail = async (userId: string) => {
  const response = await useCustomAxios(`/users/${userId}`);
  return response?.data.value;
};
