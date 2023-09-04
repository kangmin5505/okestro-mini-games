export enum BoardState {
  Empty,
  Black,
  White,
}

export interface GomokuGamePayload {
  type: "INIT_BOARD" | "PUT_STONE" | "TURN_CHANGE" | "FINISH_GAME";
  content: any;
}

export interface CurrTurn {
  id: string;
}

export interface GomokuStoneColor {
  blackUserId: string;
  whiteUserId: string;
}

export interface Stone {
  row: number;
  col: number;
}
