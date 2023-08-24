import { useCustomAxios } from "@/api/index";
import { reconvertTitle } from "@/utils/utils";

export const getRanking = async (rankingParams: {
  gameTitle: string;
  filter: string;
  desc: string;
  page: number;
  limit: number;
}) => {
  const response = await useCustomAxios("/rankings", {
    params: {
      gameTitle: reconvertTitle(rankingParams.gameTitle),
      filter: rankingParams.filter,
      desc: rankingParams.desc,
      page: rankingParams.page,
      limit: rankingParams.limit,
    },
  });
  return response?.data.value;
};
