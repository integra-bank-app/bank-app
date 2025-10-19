import React, {
	createContext,
	useContext,
	useState,
	useEffect,
	ReactNode,
} from "react";

export interface User {
	id: string;
	firstName: string;
	lastName: string;
	email: string;
	role: "USER" | "ADMIN";
	branchId: string;
}

interface AuthContextType {
	user: User | null;
	isAuthenticated: boolean;
	login: (email: string, password: string) => Promise<boolean>;
	register: (userData: RegisterData) => Promise<boolean>;
	logout: () => void;
}

interface RegisterData {
	firstName: string;
	lastName: string;
	middleName?: string;
	branchId: string;
	email: string;
	password: string;
	role: string;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const useAuthentication = () => {
	const context = useContext(AuthContext);
	if (context === undefined) {
		throw new Error(
			"useAuthentication must be used within an AuthenticationProvider"
		);
	}
	return context;
};

interface AuthenticationProviderProps {
	children: ReactNode;
}

export const AuthenticationProvider: React.FC<AuthenticationProviderProps> = ({
	children,
}) => {
	const [user, setUser] = useState<User | null>(null);
	const [isAuthenticated, setIsAuthenticated] = useState<boolean>(false);

	useEffect(() => {
		const token = localStorage.getItem("authToken");
		const userData = localStorage.getItem("userData");

		if (token && userData) {
			try {
				const parsedUser = JSON.parse(userData);
				setUser(parsedUser);
				setIsAuthenticated(true);
			} catch (error) {
				localStorage.removeItem("authToken");
				localStorage.removeItem("userData");
			}
		}
	}, []);

	const login = async (email: string, password: string): Promise<boolean> => {
		try {
			const response = await fetch("http://localhost:8080/api/login", {
				method: "POST",
				headers: {
					"Content-Type": "application/json",
				},
				body: JSON.stringify({ email, password }),
			});

			if (response.ok) {
				const data = await response.json();

				const userData: User = {
					id: data.id,
					firstName: data.firstName,
					lastName: data.lastName,
					email: data.email,
					role: data.role,
					branchId: data.branchId,
				};

				localStorage.setItem("authToken", data.token);
				localStorage.setItem("userData", JSON.stringify(userData));

				setUser(userData);
				setIsAuthenticated(true);
				return true;
			}
			return false;
		} catch (error) {
			console.error("Login error:", error);
			return false;
		}
	};

	const register = async (userData: RegisterData): Promise<boolean> => {
		try {
			const registerPayload = {
				firstName: userData.firstName,
				lastName: userData.lastName,
				middleName: userData.middleName || null,
				branchId: userData.branchId,
				email: userData.email,
				password: userData.password,
				requestedRole: userData.role,
			};

			const response = await fetch("http://localhost:8080/api/register", {
				method: "POST",
				headers: {
					"Content-Type": "application/json",
				},
				body: JSON.stringify(registerPayload),
			});

			if (response.ok) {
				const data = await response.json();
				if (data?.token && data?.id && data?.branchId) {
					const newUser: User = {
						id: data.id,
						firstName: data.firstName,
						lastName: data.lastName,
						email: data.email,
						role: data.role,
						branchId: data.branchId,
					};
					localStorage.setItem("authToken", data.token);
					localStorage.setItem("userData", JSON.stringify(newUser));
					setUser(newUser);
					setIsAuthenticated(true);
				}
			}

			return response.ok;
		} catch (error) {
			console.error("Registration error:", error);
			return false;
		}
	};

	const logout = () => {
		localStorage.removeItem("authToken");
		localStorage.removeItem("userData");
		setUser(null);
		setIsAuthenticated(false);
	};

	return (
		<AuthContext.Provider
			value={{
				user,
				isAuthenticated,
				login,
				register,
				logout,
			}}
		>
			{children}
		</AuthContext.Provider>
	);
};
