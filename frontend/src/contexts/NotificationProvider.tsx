import { Toast } from "primereact/toast";
import { createContext, useEffect, useRef, useState } from "react";
import { TNotification } from "../lib/types";

type NotificationContextType = {
	uuid: string | null;
	setUuid: (newUuid: string) => void;
	isConnected: boolean;
};

type NotificationProviderProps = {
	initialUuid?: string;
	children: React.ReactNode;
};

export const NotificationContext =
	createContext<NotificationContextType | null>(null);
NotificationContext.displayName = "NotificationContext";

export function NotificationProvider({
	initialUuid,
	children,
}: NotificationProviderProps) {
	const toast = useRef<Toast>(null);
	const [uuid, setUuid] = useState<string | null>(initialUuid ?? null);
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
		if (!uuid) return;
		const socket = new WebSocket(
			`${import.meta.env.VITE_BACKEND_SOCKET_URL}/ws/notifications?uuid=${uuid}`
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
			}
		};

		socket.onerror = () => setIsConnected(false);
		socket.onclose = () => setIsConnected(false);

		return () => socket.close();
	}, [uuid]);

	return (
		<NotificationContext.Provider value={{ uuid, setUuid, isConnected }}>
			<Toast ref={toast} />
			{children}
		</NotificationContext.Provider>
	);
}
