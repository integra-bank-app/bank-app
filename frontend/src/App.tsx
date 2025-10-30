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

import { Link } from "react-router-dom";
import {InvestmentsPage} from "./pages/InvestmentsPage/InvestmentPage";
function App() {
	return (
		<PrimeReactProvider>
			<AuthenticationProvider>
				<NotificationProvider>
					<BrowserRouter>
						<div className="flex flex-col min-h-screen">
							<Header />
							<main className="flex-grow p-4">
								<Routes>
									{/* Public Routes */}
									<Route
										path="/login"
										element={
											<PublicRoute>
												<LoginPage />
											</PublicRoute>
										}
									/>
									<Route
										path="/register"
										element={
											<PublicRoute>
												<RegisterPage />
											</PublicRoute>
										}
									/>

									{/* Protected Routes - Any authenticated user */}
									<Route
										path="/home"
										element={
											<ProtectedRoute>
												<UserMainPage />
											</ProtectedRoute>
										}
									/>
									<Route
										path="/deposits"
										element={
											<ProtectedRoute>
												<DepositsPage />
											</ProtectedRoute>
										}
									/>
									<Route
										path="/investments"
										element={
											<ProtectedRoute>
												<InvestmentsPage />
											</ProtectedRoute>
										}
									/>

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
