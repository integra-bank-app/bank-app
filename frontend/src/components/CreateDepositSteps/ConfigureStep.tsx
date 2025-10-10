import { InputNumber } from "primereact/inputnumber";
import { Dropdown } from "primereact/dropdown";
import { Button } from "primereact/button";

type ConfigureStepProps = {
	depositValue: number | null;
	setDepositValue: (v: number | null) => void;
	interestRate: number | null;
	setInterestRate: (v: number | null) => void;
	depositInvalid: boolean;
	setDepositInvalid: (v: boolean) => void;
	interestInvalid: boolean;
	setInterestInvalid: (v: boolean) => void;
	interestOptions: { name: string; value: number }[];
	onNext: () => void;
	onPrev: () => void;
};

export default function ConfigureStep({
	depositValue,
	setDepositValue,
	interestRate,
	setInterestRate,
	depositInvalid,
	setDepositInvalid,
	interestInvalid,
	setInterestInvalid,
	interestOptions,
	onNext,
	onPrev,
}: ConfigureStepProps) {
	const handleNext = () => {
		const isDepositValid = depositValue !== null && depositValue > 0;
		const isInterestValid = interestRate !== null;

		setDepositInvalid(!isDepositValid);
		setInterestInvalid(!isInterestValid);

		if (isDepositValid && isInterestValid) {
			onNext();
		}
	};

	return (
		<>
			<div className="flex flex-col p-2 space-y-6 min-h-[600px]">
				<div className="flex flex-col">
					<label htmlFor="depositAmount" className="mb-2 font-semibold">
						Deposit Amount
					</label>
					<InputNumber
						id="depositAmount"
						value={depositValue}
						onValueChange={(e) => setDepositValue(e.value ?? null)}
						mode="decimal"
						min={0}
						className={depositInvalid ? "p-invalid w-full" : "w-full"}
					/>
				</div>
				<div className="flex flex-col">
					<label htmlFor="interestRate" className="mb-2 font-semibold">
						Interest Rate
					</label>
					<Dropdown
						id="interestRate"
						value={interestRate}
						onChange={(e) => setInterestRate(e.value)}
						options={interestOptions}
						optionLabel="name"
						placeholder="Select interest rate"
						className={interestInvalid ? "p-invalid w-full" : "w-full"}
					/>
				</div>
			</div>
			<div className="flex pt-4 justify-content-between">
				<Button
					label="Return"
					severity="secondary"
					icon="pi pi-arrow-left"
					onClick={onPrev}
				/>
				<Button
					label="Review"
					icon="pi pi-arrow-right"
					iconPos="right"
					onClick={handleNext}
				/>
			</div>
		</>
	);
}
