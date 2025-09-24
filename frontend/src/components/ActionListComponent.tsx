import { Button } from "primereact/button";
import { Card } from "primereact/card";

function ActionListComponent({ className }: { className?: string }) {
	const actions = ["View Items", "Add Item", "Delete Item", "Update Item"];

	return (
		<div className="grid grid-cols-3 gap-4">
			{actions.map((action, idx) => (
				<Button key={idx} label={action} className="w-full" />
			))}
		</div>
	);
}
export default ActionListComponent;
