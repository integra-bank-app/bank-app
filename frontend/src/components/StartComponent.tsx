import {Button} from "primereact/button";
import {InputText} from "primereact/inputtext";
import {JSX, useState} from "react";
import {useNotificationContext} from "../lib/hooks";
import {useNavigate} from "react-router-dom";

export function StartComponent(): JSX.Element {
    const {setUuid, uuid} = useNotificationContext();
    const [text, setText] = useState(uuid ?? "");
    const navigate = useNavigate();

    return (
        <div className="flex flex-col items-center justify-center min-h-screen p-6 space-y-6">
            <div className="text-5xl font-italic text-center">IntegraPay</div>

            <div className="flex space-x-2">
                <InputText
                    placeholder="Enter UUID"
                    value={text}
                    onChange={(e) => setText(e.target.value)}
                    className="p-inputtext-lg"
                />
                <Button
                    icon="pi pi-search"
                    label="Confirm"
                    className="p-button-lg p-button-primary"
                    onClick={() => setUuid(text)}
                />
            </div>

            <Button
                icon="pi pi-wallet"
                onClick={() => {
                    setUuid(text);
                    navigate("/deposits");
                }}
                className="p-button-lg p-button-primary"
                style={{gap: "0.5rem"}}
            >
                Deposits
            </Button>
        </div>
    );
}

export default StartComponent;
