import axios from "axios";
import { Configuration } from "../api/configuration";

const getAccessToken = (): string => {
	const token = localStorage.getItem("authToken");
	return token ? `Bearer ${token}` : "";
};

export const configuration = new Configuration({
	accessToken: () => getAccessToken(),
});

export const axiosClient = axios.create({});

axiosClient.interceptors.request.use(
	(config) => {
		if (config.url?.includes("/login") || config.url?.includes("/signup")) {
			return config;
		}

		const token = getAccessToken();
		if (token) {
			config.headers["Authorization"] = token;
		}

		return config;
	},
	(error) => Promise.reject(error)
);
