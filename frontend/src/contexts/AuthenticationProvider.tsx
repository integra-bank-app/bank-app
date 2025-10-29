import React, {
	createContext,
	useContext,
	useState,
	useEffect,
	ReactNode,
} from "react";
import { AuthControllerApi, LoginRequestDTO, RegisterRequestDTO } from "../api";

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

interface RegisterData extends RegisterRequestDTO {
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
			const authApi = new AuthControllerApi();
			const loginRequest: LoginRequestDTO = { email, password };
			const response = await authApi.authenticateUser(loginRequest);

			if (response.status === 200 && response.data) {
				const data = response.data as any;
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
			const authApi = new AuthControllerApi();
			const registerRequest: RegisterRequestDTO = {
				firstName: userData.firstName,
				lastName: userData.lastName,
				middleName: userData.middleName || undefined,
				branchId: userData.branchId,
				email: userData.email,
				password: userData.password,
				requestedRole: userData.role as any,
			};

			const response = await authApi.registerUser(registerRequest);

			if (response.status === 200 && response.data) {
				const data = response.data as any;
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

			return response.status === 200;
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
