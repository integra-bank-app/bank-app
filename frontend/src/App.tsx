import { PrimeReactProvider } from "primereact/api";
import { StartComponent } from "./components/StartComponent";

import "primeflex/primeflex.css"; // flex
import "primeicons/primeicons.css"; //icons
import "primereact/resources/themes/bootstrap4-dark-purple/theme.css";
import "./App.css";
import { NotificationProvider } from "./contexts/NotificationProvider";

function App() {
	return (
		<>
			<PrimeReactProvider>
				<NotificationProvider>
					<StartComponent />
				</NotificationProvider>
			</PrimeReactProvider>
		</>
	);
}

export default App;
