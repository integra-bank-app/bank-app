import { createContext, useContext, useState, ReactNode } from "react";
import { TUser, TUserRole } from "../lib/types";

type UserContextType = {
	user: TUser;
	setUser: (user: TUser) => void;
};

export const UserContext = createContext<UserContextType | null>(null);

export function UserProvider({ children }: { children: ReactNode }) {
	const [user, setUserState] = useState<TUser>({
		uuid: "11111111-1111-1111-1111-111111111111", // dummy UUID
		branchId: "branch-001",
		role: "ADMIN" as TUserRole,
		firstName: "Alice",
		lastName: "Smith",
	});

	const setUser = (newUser: TUser) => {
		setUserState(newUser);
	};

	return (
		<UserContext.Provider value={{ user, setUser }}>
			{children}
		</UserContext.Provider>
	);
}
