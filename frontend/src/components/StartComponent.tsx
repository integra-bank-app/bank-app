import { Button } from "primereact/button";
import { useNavigate } from "react-router-dom";

export function StartComponent() {
    const navigate = useNavigate();

    return (
        <div className="flex flex-col items-center justify-center min-h-screen p-6 space-y-6">
            <div className="text-5xl font-italic text-center">IntegraPay</div>
            <Button
                icon="pi pi-home"
                label="Go to Home"
                className="p-button-lg p-button-primary"
                onClick={() => navigate("/home")}
            />
        </div>
    );
}

export default StartComponent;