import { Button } from "primereact/button";
import { useTranslation } from "react-i18next";

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
	const { t } = useTranslation();
	return (
		<div className="flex flex-col w-full">
			<div className="flex flex-col items-center justify-center w-full min-h-[400px] sm:min-h-[480px] p-3 sm:p-6">
				<div className="w-full max-w-md border border-surface-400/30 bg-surface-800/40 rounded-2xl p-4 sm:p-6 shadow-md">
					<h3 className="text-lg sm:text-xl font-semibold mb-4 text-center">
						{t("deposits.reviewStep.depositSummary")}
					</h3>

					<div className="flex justify-between py-2 text-sm sm:text-base">
						<span className="font-medium text-gray-300">{t("deposits.reviewStep.depositAmount")}</span>
						<span className="text-white">
							{depositValue ? `${depositValue.toFixed(2)}` : "-"}
						</span>
					</div>

					<div className="flex justify-between py-2 text-sm sm:text-base">
						<span className="font-medium text-gray-300">{t("deposits.reviewStep.interestRate")}</span>
						<span className="text-white">
							{interestRate ? `${interestRate}%` : "-"}
						</span>
					</div>
				</div>
			</div>

			<div className="flex flex-col-reverse sm:flex-row justify-between gap-2 sm:gap-4 pt-6 px-2 sm:px-4">
				<Button
					label={t("deposits.reviewStep.makeChanges")}
					severity="secondary"
					icon="pi pi-arrow-left"
					onClick={onPrev}
					className="w-full sm:w-auto"
				/>
				<Button
					label={t("deposits.reviewStep.createDeposit")}
					icon="pi pi-arrow-right"
					iconPos="right"
					onClick={onCreateDeposit}
					disabled={!depositValue || !interestRate}
					className="w-full sm:w-auto"
				/>
			</div>
		</div>
	);
}
