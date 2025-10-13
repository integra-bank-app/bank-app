import './index.css'
import {StrictMode} from "react";
import {createRoot} from "react-dom/client";
import App from "./App.tsx";
import "./i18n/i18n.js";

createRoot(document.getElementById("root")).render(
    <StrictMode>
        <App/>
    </StrictMode>
);
