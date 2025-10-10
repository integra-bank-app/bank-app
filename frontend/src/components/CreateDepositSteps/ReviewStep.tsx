import { Button } from "primereact/button";

type ReviewStepProps = {
	depositValue: number | null;
	interestRate: number | null;
	onPrev: () => void;
	onCreateDeposit: () => void;
};

export default function ReviewStep({
	depositValue,
	interestRate,
	onPrev,
	onCreateDeposit,
}: ReviewStepProps) {
	return (
		<>
			<div className="flex flex-col items-center justify-center p-4 space-y-6 min-h-[600px] w-full">
				<div className="flex flex-col space-y-2 w-full max-w-md border p-4 rounded shadow">
					<h3 className="text-lg font-semibold">Deposit Summary</h3>
					<div className="flex justify-between">
						<span>Deposit Amount:</span>
						<span>{depositValue ? `$${depositValue.toFixed(2)}` : "-"}</span>
					</div>
					<div className="flex justify-between">
						<span>Interest Rate:</span>
						<span>{interestRate ? `${interestRate}%` : "-"}</span>
					</div>
				</div>
			</div>
			<div className="flex justify-content-between pt-4 w-full">
				<Button
					label="Make Changes"
					severity="secondary"
					icon="pi pi-arrow-left"
					onClick={onPrev}
				/>
				<Button
					label="Create Deposit"
					icon="pi pi-arrow-right"
					iconPos="right"
					onClick={onCreateDeposit}
					disabled={!depositValue || !interestRate}
				/>
			</div>
		</>
	);
}
