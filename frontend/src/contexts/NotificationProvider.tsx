import { Toast } from "primereact/toast";
import { createContext, useEffect, useRef, useState } from "react";
import { TNotification } from "../lib/types";
import { useAuthentication } from "./AuthenticationProvider";

type NotificationContextType = {
	isConnected: boolean;
	toastRef: React.RefObject<Toast | null>;
};

type NotificationProviderProps = {
	children: React.ReactNode;
};

export const NotificationContext =
	createContext<NotificationContextType | null>(null);
NotificationContext.displayName = "NotificationContext";

export function NotificationProvider({ children }: NotificationProviderProps) {
	const toast = useRef<Toast>(null);
	const { user, isAuthenticated } = useAuthentication();
	const [isConnected, setIsConnected] = useState(false);

	const severityMap: Record<
		TNotification["type"],
		"info" | "success" | "warn" | "error" | "secondary" | "contrast"
	> = {
		SUCCESS: "success",
		INFO: "info",
		WARN: "warn",
		ERROR: "error",
		SECONDARY: "secondary",
		CONTRAST: "contrast",
	};

	useEffect(() => {
		if (!isAuthenticated || !user?.id) return;

		const socket = new WebSocket(
			`${import.meta.env.VITE_BACKEND_SOCKET_URL}/api/ws/notifications?uuid=${user.id}`
		);

		socket.onopen = () => {
			setIsConnected(true);
			toast.current?.show({
				severity: "success",
				summary: "Connected",
				detail: "WebSocket connection established",
			});
		};

		socket.onmessage = (event) => {
			const data: TNotification = JSON.parse(event.data);
			if (data.message) {
				toast.current?.show({
					severity: severityMap[data.type],
					summary: "Notification",
					detail: data.message,
				});
				console.log("Received notification:", data);
			}
			if (data.type === "SUCCESS") {
				console.log("Refetching data due to SUCCESS notification");
				const refetchEvent = new Event("refetchData");
				window.dispatchEvent(refetchEvent);
			}
		};

		socket.onerror = () => setIsConnected(false);
		socket.onclose = () => setIsConnected(false);

		return () => socket.close();
	}, [isAuthenticated, user?.id]);

	return (
		<NotificationContext.Provider value={{ isConnected, toastRef: toast }}>
			<Toast ref={toast} />
			{children}
		</NotificationContext.Provider>
	);
}