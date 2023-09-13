export interface User {
  id: string;
  nickname: string;
  email: string;
}

export interface UserDetail {
  nickname: string;
  email: string;
  createdAt: string;
}

export interface GameStat {
  gameTitle: string;
  wins: number;
  loses: number;
}

export interface GameRecord {
  gameTitle: string;
  winUserNickname: string;
  loseUserNickname: string;
  winUserScore: number;
  loseUserScore: number;
  startTime: string;
  finishTime: string;
}

export interface FormattedGameRecord {
  게임: string;
  결과: string;
  "닉네임(점수)": string;
  시작시간: string;
  게임시간: string;
}

export type UserLocation = "LOBBY" | "GAME_ROOM" | "ON_GAME";
