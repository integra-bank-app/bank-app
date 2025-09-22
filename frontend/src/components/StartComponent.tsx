import { Button } from "primereact/button";
import { InputText } from "primereact/inputtext";
import { JSX, useState } from "react";
import { useNotificationContext } from "../lib/hooks";

export function StartComponent(): JSX.Element {
	const { setUuid } = useNotificationContext();
	const [text, setText] = useState("");

	return (
		<div>
			<div className="text-5xl font-italic">test</div>
			<InputText
				placeholder="Enter uuid"
				value={text}
				onChange={(e) => setText(e.target.value)}
			/>
			<Button icon="pi pi-search" onClick={() => setUuid(text)}>
				Confirm
			</Button>
		</div>
	);
}