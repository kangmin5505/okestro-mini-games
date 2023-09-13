import { defineStore } from "pinia";
import { Client } from "@stomp/stompjs";

export const useStompStore = defineStore("stomp", () => {
  const client = new Client({
    brokerURL: `${import.meta.env.VITE_BACK_WS_URL}/ws`,
  });

  function connect() {
    client.activate();
  }

  return {
    client,
    connect,
  };
});
