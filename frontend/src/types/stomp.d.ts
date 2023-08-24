export interface Message {
  type: "JOIN" | "LEAVE" | "USER";
  from?: string;
  content: string;
}

export interface ErrorMessage {
  httpStatus: string;
  message: string;
}
