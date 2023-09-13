export const convertTitle = (title: string) => {
  switch (title) {
    case "card-matching":
      return "카드 맞추기";
    case "gomoku":
      return "오목";
    case "pong":
      return "Pong";
    default:
      return "";
  }
};

export const reconvertTitle = (title: string) => {
  switch (title) {
    case "카드 맞추기":
      return "card-matching";
    case "오목":
      return "gomoku";
    case "Pong":
      return "pong";
    default:
      return "";
  }
};

export const alertBeforeUnload = () => {
  window.addEventListener("beforeunload", alertEvent);
};

export const deleteAlertBeforeUnload = () => {
  window.removeEventListener("beforeunload", alertEvent);
};

const alertEvent = (e: BeforeUnloadEvent) => {
  e.preventDefault();
  e.returnValue = "";
};
