import { useContext } from "react";
import { NotificationContext } from "../contexts/NotificationProvider";
import { UserContext } from "../contexts/UserProvider";

export function useSafeContext<T>(context: React.Context<T | null>): T {
	const value = useContext(context);
	if (value === null) {
		throw new Error(
			(context.displayName ?? "Context") + " must be used within its Provider"
		);
	}
	return value;
}

export const useNotificationContext = () => useSafeContext(NotificationContext);
export const useUserContext = () => useSafeContext(UserContext);
