import { Checkbox } from "primereact/checkbox";
import { Button } from "primereact/button";
import ScrollablePdfViewer from "../ScrollablePdfViewer";
import { useTranslation } from "react-i18next";

type TermsStepProps = {
	agreesToTerms: boolean;
	setAgreesToTerms: (v: boolean) => void;
	checkboxEnabled: boolean;
	setCheckboxEnabled: (v: boolean) => void;
	onNext: () => void;
};

export default function TermsStep({
	agreesToTerms,
	setAgreesToTerms,
	checkboxEnabled,
	setCheckboxEnabled,
	onNext,
}: TermsStepProps) {
	const { t } = useTranslation();
	return (
		<div className="flex flex-col w-full">
			<div className="flex flex-col items-center justify-center w-full min-h-[400px] sm:min-h-[500px] md:min-h-[560px]">
				<div className="w-full max-w-3xl p-2 sm:p-4">
					<ScrollablePdfViewer
						fileUrl="/termsAndConditions.pdf"
						onScrolledToEnd={() => setCheckboxEnabled(true)}
					/>
				</div>
			</div>

			<div className="flex flex-wrap items-center justify-start gap-2 pt-4 px-2 sm:px-4">
				<Checkbox
					inputId="agree"
					checked={agreesToTerms}
					disabled={!checkboxEnabled}
					onChange={() => setAgreesToTerms(!agreesToTerms)}
					className={`custom-checkbox ${!checkboxEnabled ? "opacity-50" : ""}`}
				/>
				<label
					htmlFor="agree"
					className={`text-sm sm:text-base ${
						checkboxEnabled ? "text-white" : "text-white/70"
					}`}
				>
					{t("deposits.termsStep.agreeToTerms")}
				</label>
			</div>

			<div className="flex justify-end w-full pt-6 px-2 sm:px-4">
				<Button
					disabled={!agreesToTerms}
					icon="pi pi-check"
					label={t("deposits.termsStep.continue")}
					iconPos="right"
					onClick={onNext}
					className="w-full sm:w-auto"
				/>
			</div>
		</div>
	);
}
