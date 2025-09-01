import { Button } from "primereact/button";

export function StartComponent(): React.ReactNode {
	return (
		<div>
			<div className="text-5xl font-italic">test</div>
			<Button icon="pi pi-search">Fancy Button</Button>
		</div>
	);
}
