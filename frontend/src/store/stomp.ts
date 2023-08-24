import { defineStore } from "pinia";
import { Client } from "@stomp/stompjs";

export const useStompStore = defineStore("stomp", () => {
  const client = new Client({
    brokerURL: `${import.meta.env.VITE_BACK_WS_URL}/ws`,
  });

  client.onDisconnect = () => {
    console.log("disconnected");
  };
  client.onConnect = () => {
    console.log("connected");
  };
  // client.connectionTimeout = 1000 * 60 * 60;
  client.onWebSocketClose(() => {
    console.log("websocket closed");
  });

  function connect() {
    client.activate();
  }

  return {
    client,
    connect,
  };
});
