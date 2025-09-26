import { PrimeReactProvider } from "primereact/api";
import { StartComponent } from "./components/StartComponent";

import "primeflex/primeflex.css"; // flex
import "primeicons/primeicons.css"; //icons
import "primereact/resources/themes/bootstrap4-dark-purple/theme.css";
import "./App.css";
import { NotificationProvider } from "./contexts/NotificationProvider";
import { UserMainPage } from "./pages/UserMainPage";
import  DepositsPage  from "./pages/DepositsPage/DepositsPage";

import AdminPageComponent from "./pages/AdminPage";
import { Routes, Route, Router, BrowserRouter } from "react-router-dom";
import UserListPage from "./pages/UserListPage";
import { UserProvider } from "./contexts/UserProvider";

function App() {

	return (
		<>
			<PrimeReactProvider>
				<div className="p-m-4 p-p-4 surface-card border-round">
					<UserProvider>
						<NotificationProvider>
							<BrowserRouter>
								<Routes>
									<Route path="/" element={<StartComponent />} />
									<Route path="/admin" element={<AdminPageComponent />} />
									<Route path="/users" element={<UserListPage />} />
                                    <Route path="/deposits" element={<DepositsPage />} />
								</Routes>
							</BrowserRouter>
						</NotificationProvider>
					</UserProvider>
				</div>
			</PrimeReactProvider>
		</>
);
}

export default App;
