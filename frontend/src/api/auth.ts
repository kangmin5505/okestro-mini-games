import { useCustomAxios } from "@/api/index";

export const logoutUser = async () => {
  await useCustomAxios("/auth/logout", { method: "post" });
};
