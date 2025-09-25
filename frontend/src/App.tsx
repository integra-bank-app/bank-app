import { PrimeReactProvider } from "primereact/api";
import { StartComponent } from "./components/StartComponent";

import "primeflex/primeflex.css"; // flex
import "primeicons/primeicons.css"; //icons
import "primereact/resources/themes/bootstrap4-dark-purple/theme.css";
import "./App.css";
import {NotificationProvider} from "./contexts/NotificationProvider";
import AdminPageComponent from "./components/AdminPage";

function App() {
	return (
		<>
			<PrimeReactProvider>
				<UserProvider>
                    <Header/>
					<NotificationProvider>
						<BrowserRouter>
							<Routes>
								<Route path="/" element={<StartComponent />} />
								<Route path="/admin" element={<AdminPageComponent />} />
								<Route path="/users" element={<UserListPage />} />
							</Routes>
						</BrowserRouter>
					</NotificationProvider>
                    <Footer/>
				</UserProvider>
			</PrimeReactProvider>
		</>
	);
}

export default App;
