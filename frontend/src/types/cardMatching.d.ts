export interface CardMatchingGamePayload {
  type:
    | "INIT_BOARD"
    | "FLIP_CARD"
    | "CHECK_MATCH"
    | "TURN_CHANGE"
    | "SCORE_CHANGE"
    | "MATCHED_CARD"
    | "FINISH_GAME";
  content: any;
}

export interface CurrTurn {
  id: string;
}

export interface CardPayload {
  position: number;
  faceValue: String;
}

export interface CardMatchingCard {
  value: string;
  visible: boolean;
  position: number;
  matched: boolean;
  matchedBy: string;
}
