import {PrimeReactProvider} from "primereact/api";
import {StartComponent} from "./components/StartComponent";
import {BrowserRouter as Router, Routes, Route} from "react-router-dom";

import "primeflex/primeflex.css"; // flex
import "primeicons/primeicons.css"; //icons
import "primereact/resources/themes/bootstrap4-dark-purple/theme.css";
import "./App.css";
import {NotificationProvider} from "./contexts/NotificationProvider";
import Header from "./components/Header";
import Footer from "./components/Footer";
import Login from "./pages/Login";

function App() {
    return (
        <Router>
            <div className="flex flex-col min-h-screen">
                <Header/>

                <main className="flex-grow p-4">
                    <Routes>
                        {/* Login page route */}
                        <Route path="/login" element={<Login/>}/>

                        <Route
                            path="/"
                            element={
                                <PrimeReactProvider>
                                    <NotificationProvider>
                                        <StartComponent/>
                                    </NotificationProvider>
                                </PrimeReactProvider>
                            }
                        />
                    </Routes>
                </main>

                <Footer/>
            </div>
        </Router>
    );
}

export default App;
