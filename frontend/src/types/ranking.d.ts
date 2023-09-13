export interface Ranking {
  totalElements: number;
  rankings: RankingContent[];
}
export interface RankingContent {
  rank: number;
  nickname: string;
  userId: string;
  wins: number;
  loses: number;
  totalGames: number;
  winPercentage: number;
}
