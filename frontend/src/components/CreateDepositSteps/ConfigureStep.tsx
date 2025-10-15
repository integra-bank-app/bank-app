import { InputNumber } from "primereact/inputnumber";
import { Dropdown } from "primereact/dropdown";
import { Button } from "primereact/button";
import { useTranslation } from "react-i18next";

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
	const { t } = useTranslation();
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
		<div className="flex flex-col w-full">
			<div className="flex flex-col gap-6 min-h-[400px] sm:min-h-[480px] p-2 sm:p-4">
				<div className="flex flex-col w-full">
					<label
						htmlFor="depositAmount"
						className="mb-2 font-semibold text-sm sm:text-base"
					>
						{t("deposits.configureStep.depositAmount")}
					</label>
					<InputNumber
						id="depositAmount"
						value={depositValue}
						onValueChange={(e) => setDepositValue(e.value ?? null)}
						mode="decimal"
						min={0}
						placeholder={t("deposits.configureStep.enterAmount")}
						className={`${depositInvalid ? "p-invalid" : ""} w-full`}
					/>
				</div>

				<div className="flex flex-col w-full">
					<label
						htmlFor="interestRate"
						className="mb-2 font-semibold text-sm sm:text-base"
					>
						{t("deposits.configureStep.interestRate")}
					</label>
					<Dropdown
						id="interestRate"
						value={interestRate}
						onChange={(e) => setInterestRate(e.value)}
						options={interestOptions}
						optionLabel="name"
						placeholder={t("deposits.configureStep.selectInterestRate")}
						className={`${interestInvalid ? "p-invalid" : ""} w-full`}
					/>
				</div>
			</div>

			<div className="flex flex-col-reverse sm:flex-row justify-between gap-2 sm:gap-4 pt-6 px-2 sm:px-4">
				<Button
					label={t("deposits.configureStep.return")}
					severity="secondary"
					icon="pi pi-arrow-left"
					onClick={onPrev}
					className="w-full sm:w-auto"
				/>
				<Button
					label={t("deposits.configureStep.review")}
					icon="pi pi-arrow-right"
					iconPos="right"
					onClick={handleNext}
					className="w-full sm:w-auto"
				/>
			</div>
		</div>
	);
}
