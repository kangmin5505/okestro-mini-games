import axios, { AxiosError, AxiosRequestConfig } from "axios";
import { useAxios, UseAxiosOptions } from "@vueuse/integrations/useAxios";
import { useErrorModalStore } from "@/store/errorModal";
import { ErrorMessage } from "@/types/stomp";

const API_URL = `${import.meta.env.VITE_BACK_URL}/api/v1`;
const axiosInstance = axios.create({
  baseURL: API_URL,
  withCredentials: true,
});

axiosInstance.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response.status === 401) {
      return axiosInstance.request(error.config);
    }
    return Promise.reject(error);
  },
);

interface AxiosErrorModal {
  errorTitle?: string;
  to?: string | { name: string } | null;
}

export const useCustomAxios = async (
  url: string,
  config: AxiosRequestConfig = {},
  errorModal: AxiosErrorModal = {},
  options: UseAxiosOptions = {},
) => {
  const defaultConfig: AxiosRequestConfig = {
    method: "GET",
    withCredentials: true,
  };
  const defaultErrorModal = {
    errorTitle: "오류",
    to: null,
    ...errorModal,
  };

  let isAlertDisplayed = false;
  const defaultOptions: UseAxiosOptions = {
    immediate: true,
    onError: (error: unknown) => {
      if (error instanceof AxiosError) {
        if (error.message.includes("Network Error") && !isAlertDisplayed) {
          isAlertDisplayed = true;
          alert("서버와 연결이 끊어졌습니다. 다시 로그인하세요.");
          window.location.replace(
            `${import.meta.env.VITE_BACK_URL}/api/v1/auth/oauth/google`,
          );
        } else {
          const data = error.response?.data as ErrorMessage;
          const { openModal } = useErrorModalStore();
          openModal({
            title: defaultErrorModal.errorTitle,
            content: data?.message,
            to: defaultErrorModal.to,
          });
        }
      }
    },
  };

  return useAxios(url, { ...defaultConfig, ...config }, axiosInstance, {
    ...defaultOptions,
    ...options,
  });
};
