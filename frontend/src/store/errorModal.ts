import { defineStore } from "pinia";
import { ref } from "vue";

export interface ErrorModal {
  errorModal?: boolean;
  title: string;
  content: string;
  to: null | { name: string } | string;
}

export const useErrorModalStore = defineStore("errorModal", () => {
  const errorModal = ref<ErrorModal>({
    errorModal: false,
    title: "",
    content: "",
    to: null,
  });

  function openModal(errorModalParam: ErrorModal) {
    errorModal.value.errorModal = !errorModal.value.errorModal;
    errorModal.value.title = errorModalParam.title;
    errorModal.value.content = errorModalParam.content;
    errorModal.value.to = errorModalParam.to;
  }

  function closeModal() {
    errorModal.value.errorModal = !errorModal.value.errorModal;
  }

  return {
    errorModal,
    openModal,
    closeModal,
  };
});
