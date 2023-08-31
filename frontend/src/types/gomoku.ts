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

// export interface CardPayload {
//   position: number;
//   faceValue: String;
// }
//
// export interface CardMatchingCard {
//   value: string;
//   visible: boolean;
//   position: number;
//   matched: boolean;
//   matchedBy: string;
// }
