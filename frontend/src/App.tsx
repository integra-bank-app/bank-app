import { PrimeReactProvider } from "primereact/api";
import { StartComponent } from "./components/StartComponent";

import "primeflex/primeflex.css";
import "primeicons/primeicons.css";
import "primereact/resources/themes/bootstrap4-dark-purple/theme.css";
import "./App.css";
import { NotificationProvider } from "./contexts/NotificationProvider";
import UserMainPage from "./pages/UserMainPage";
import DepositsPage from "./pages/DepositsPage/DepositsPage";

import AdminPageComponent from "./pages/AdminPage";
import { Routes, Route, BrowserRouter } from "react-router-dom";
import UserListPage from "./pages/UserListPage";
import LoginPage from "./pages/auth/LoginPage";
import RegisterPage from "./pages/auth/RegisterPage";
import NotFoundPage from "./pages/NotFoundPage";
import { AuthenticationProvider } from "./contexts/AuthenticationProvider";
import { ProtectedRoute, PublicRoute } from "./components/ProtectedRoute";
import ProtectedLayout from "./components/ProtectedLayout";
import PublicLayout from "./components/PublicLayout";

function App() {
	return (
		<PrimeReactProvider>
			<AuthenticationProvider>
				<NotificationProvider>
					<BrowserRouter>
						<Routes>
							{/* Public Routes */}
							<Route element={<PublicRoute />}>
								<Route element={<PublicLayout />}>
									<Route path="/login" element={<LoginPage />} />
									<Route path="/register" element={<RegisterPage />} />
								</Route>
							</Route>

							{/* Protected Routes */}
							<Route element={<ProtectedRoute />}>
								<Route element={<ProtectedLayout />}>
									<Route path="/home" element={<UserMainPage />} />
									<Route path="/deposits" element={<DepositsPage />} />
									<Route path="/" element={<StartComponent />} />
									<Route element={<ProtectedRoute requiredRole="ADMIN" />}>
										<Route path="/admin" element={<AdminPageComponent />} />
										<Route path="/users" element={<UserListPage />} />
									</Route>
								</Route>
							</Route>

							{/* 404 Page */}
							<Route path="/404" element={<NotFoundPage />} />
							<Route path="*" element={<NotFoundPage />} />
						</Routes>
					</BrowserRouter>
				</NotificationProvider>
			</AuthenticationProvider>
		</PrimeReactProvider>
	);
}

export default App;
