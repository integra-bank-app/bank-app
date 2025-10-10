import { Checkbox } from "primereact/checkbox";
import { Button } from "primereact/button";
import ScrollablePdfViewer from "../ScrollablePdfViewer";

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
	return (
		<>
			<div className="flex flex-col items-center justify-center min-h-[556px]">
				<ScrollablePdfViewer
					fileUrl="/termsAndConditions.pdf"
					onScrolledToEnd={() => setCheckboxEnabled(true)}
				/>
			</div>
			<div className="flex justify-content-end align-items-center pt-4">
				<Checkbox
					inputId="agree"
					checked={agreesToTerms}
					disabled={!checkboxEnabled}
					onChange={() => setAgreesToTerms(!agreesToTerms)}
					className={`custom-checkbox ${!checkboxEnabled ? "disabled" : ""}`}
				/>
				<label
					htmlFor="agree"
					className="ml-2 text-sm"
					style={{
						opacity: checkboxEnabled ? 1 : 0.5, // Semi-transparent when disabled
						color: "white", // Keep the text white
					}}
				>
					I have read and agree to the terms and conditions
				</label>
			</div>
			<div className="flex pt-4 justify-content-end">
				<Button
					disabled={!agreesToTerms}
					icon="pi pi-check"
					label="Continue"
					iconPos="right"
					onClick={onNext}
				/>
			</div>
		</>
	);
}
