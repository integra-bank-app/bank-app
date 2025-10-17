import { PrimeReactProvider } from "primereact/api";
import { StartComponent } from "./components/StartComponent";
import { JSX, useState } from "react";

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
import Header from "./components/Header";
import Footer from "./components/Footer";
import LoginPage from "./pages/auth/LoginPage";
import RegisterPage from "./pages/auth/RegisterPage";
import NotFoundPage from "./pages/NotFoundPage";
import { AuthenticationProvider } from "./contexts/AuthenticationProvider";
import { ProtectedRoute, PublicRoute } from "./components/ProtectedRoute";

import { Link } from "react-router-dom";
function App() {
	const [currentPage, setCurrentPage] = useState<PageKey>("start");
	const [uuid, setUuid] = useState("");

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

									{/* Admin Only Routes */}
									<Route
										path="/admin"
										element={
											<ProtectedRoute requiredRole="ADMIN">
												<AdminPageComponent />
											</ProtectedRoute>
										}
									/>
									<Route
										path="/users"
										element={
											<ProtectedRoute requiredRole="ADMIN">
												<UserListPage />
											</ProtectedRoute>
										}
									/>

									{/* Default redirect */}
									<Route
										path="/"
										element={
											<ProtectedRoute>
												<StartComponent />
											</ProtectedRoute>
										}
									/>

									{/* 404 Page */}
									<Route path="/404" element={<NotFoundPage />} />
									<Route path="*" element={<NotFoundPage />} />
								</Routes>
							</main>

							<Footer />
						</div>
					</BrowserRouter>
				</NotificationProvider>
			</AuthenticationProvider>
		</PrimeReactProvider>
	);
}

export default App;