import { Button } from "primereact/button";
import { InputText } from "primereact/inputtext";
import { JSX, use, useState } from "react";
import { useNotificationContext, useUserContext } from "../lib/hooks";

type PageKey = "start" | "deposits";

interface StartComponentProps {
	uuid: string;
	setUuid: (uuid: string) => void;
	goToPage: (page: PageKey) => void;
}

export function StartComponent({uuid, setUuid, goToPage}:StartComponentProps): JSX.Element {
	const [text, setText] = useState(uuid);

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
					goToPage("deposits");
				}}
				className="p-button-lg p-button-primary"
				style={{ gap: "0.5rem" }}
			>
				Deposits
			</Button>
		</div>
	);
}

export default StartComponent;
