import { User } from "@/types/user";

export interface Game {
  title: string;
}

export interface GameRoom {
  id: string;
  title: string;
  userNum: number;
  limitUserNum: number;
  state: string;
  hostId: string;
  player1: User;
  player2: User | null;
}

export interface RoomMaintainPayload {
  type: "JOIN" | "LEAVE" | "DESTROY" | "READY_TOGGLE";
  content: any;
}
