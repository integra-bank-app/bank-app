export type TNotification = {
	type: "SUCCESS" | "INFO" | "WARN" | "ERROR" | "SECONDARY" | "CONTRAST";
	message: string;
};
