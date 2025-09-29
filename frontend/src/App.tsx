import { PrimeReactProvider } from "primereact/api";
import { StartComponent } from "./components/StartComponent";

import "primeflex/primeflex.css"; // flex
import "primeicons/primeicons.css"; // icons
import "primereact/resources/themes/bootstrap4-dark-purple/theme.css";
import "./App.css";
import { NotificationProvider } from "./contexts/NotificationProvider";
import UserMainPage from "./pages/UserMainPage";
import  DepositsPage  from "./pages/DepositsPage/DepositsPage";

import AdminPageComponent from "./pages/AdminPage";
import { Routes, Route, BrowserRouter } from "react-router-dom";
import UserListPage from "./pages/UserListPage";
import { UserProvider } from "./contexts/UserProvider";
import Header from "./components/Header";
import Footer from "./components/Footer";
import Login from "./pages/Login";

import { Link } from "react-router-dom";
function App() {

	return (
		<PrimeReactProvider>
			<UserProvider>
				<NotificationProvider>
					<BrowserRouter>
						{/* Use flexbox layout to make footer stick to the bottom */}
						<div className="flex flex-col min-h-screen">
							{/* Header */}
							<Header />

							{/* Main Content */}
							<main className="flex-grow p-4">
								<Routes>
									<Route path="/login" element={<Login />} />
									<Route path="/" element={<StartComponent />} />
									<Route path="/admin" element={<AdminPageComponent />} />
									<Route path="/users" element={<UserListPage />} />
									<Route
										path="*"
										element={
											<div className="flex align-items-center justify-content-center">
												404 Not Found
											</div>
										}
									/>
                                    <Route path="/deposits" element={<DepositsPage />} />
									<Route path="/home" element={<UserMainPage />} />
								</Routes>
							</main>

							{/* Footer */}
							<Footer />
						</div>
					</BrowserRouter>
				</NotificationProvider>
			</UserProvider>
		</PrimeReactProvider>
	);
}

export default App;
