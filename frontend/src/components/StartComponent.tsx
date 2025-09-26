import { Button } from "primereact/button";
import { InputText } from "primereact/inputtext";
import { JSX, use, useState } from "react";
import { useNotificationContext, useUserContext } from "../lib/hooks";

export function StartComponent(): JSX.Element {
	const { user, setUser } = useUserContext();
	const [text, setText] = useState("");

	return (
		<div>
			<div className="text-5xl font-italic">test</div>
			<InputText
				placeholder="Enter uuid"
				value={text}
				onChange={(e) => setText(e.target.value)}
			/>
			<Button
				icon="pi pi-search"
				onClick={() => setUser({ ...user, uuid: text })}
			>
				Confirm
			</Button>
		</div>
	);
}