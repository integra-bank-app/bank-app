import { Dialog } from "primereact/dialog";
import { Stepper } from "primereact/stepper";
import { StepperPanel } from "primereact/stepperpanel";
import { useTranslation } from "react-i18next";
import { useRef, useState } from "react";
import { useNotificationContext } from "../lib/hooks";
import ReviewStep from "./CreateDepositSteps/ReviewStep";
import ConfigureStep from "./CreateDepositSteps/ConfigureStep";
import TermsStep from "./CreateDepositSteps/TermsStep";
import { INTREST_RATE_OPTIONS } from "../lib/constants";

import { useAuthentication } from "../contexts/AuthenticationProvider";
export type DepositsDTO = {
	id?: number;
	interest_rate: number;
	amount: number;
};

type CreateDepositDialogProps = {
	visible: boolean;
	onHide: () => void;
};

export function CreateDepositDialog({
	visible,
	onHide,
}: CreateDepositDialogProps) {
	const interestOptions = INTREST_RATE_OPTIONS.map((value) => ({
		name: `${value}%`,
		value,
	}));

	const { t } = useTranslation();
	const { user } = useAuthentication();
	const { toastRef } = useNotificationContext();
	const [depositValue, setDepositValue] = useState<number | null>(null);
	const [interestRate, setInterestRate] = useState<number | null>(null);
	const [agreesToTerms, setAgreesToTerms] = useState(false);
	const [checkboxEnabled, setCheckboxEnabled] = useState(false);
	const stepperRef = useRef<Stepper>(null);

	const [depositInvalid, setDepositInvalid] = useState(false);
	const [interestInvalid, setInterestInvalid] = useState(false);

	const onCreateDeposit = async () => {
		if (!depositValue || !interestRate) {
			setDepositInvalid(!depositValue);
			setInterestInvalid(!interestRate);
			return;
		}

		if (!agreesToTerms) {
			toastRef.current?.show({
				severity: "warn",
				summary: t("createDepositDialog.notifications.termsNotAgreed.summary"),
				detail: t("createDepositDialog.notifications.termsNotAgreed.detail"),
				life: 3000,
			});
			return;
		}

		toastRef.current?.show({
			severity: "info",
			summary: t("createDepositDialog.notifications.creatingDeposit.summary"),
			detail: t("createDepositDialog.notifications.creatingDeposit.detail", {
				amount: depositValue.toFixed(2),
			}),
			life: 1500,
		});

		try {
			const depositDTO: DepositsDTO = {
				id: undefined,
				interest_rate: interestRate,
				amount: depositValue,
			};

			if (!user) {
				throw new Error("User not authenticated. Please log in.");
			}

			const token = localStorage.getItem("authToken");
			const response = await fetch(
				`http://localhost:8080/api/deposits/${user.id}`,
				{
					method: "POST",
					headers: {
						"Content-Type": "application/json",
						Authorization: `Bearer ${token}`,
					},
					body: JSON.stringify(depositDTO),
				}
			);

			if (!response.ok) {
				const errorText = await response.text();
				let errorMessage = errorText;
				try {
					const errorJson = JSON.parse(errorText);
					if (errorJson && errorJson.message) {
						errorMessage = errorJson.message;
					}
				} catch (e) {
					// Not a JSON error, use the raw text
				}
				throw new Error(errorMessage || "Failed to create deposit");
			}

			const responseData = await response.text();

			toastRef.current?.show({
				severity: "success",
				summary: t("createDepositDialog.notifications.depositCreated.summary"),
				detail: t("createDepositDialog.notifications.depositCreated.detail", {
					amount: depositValue.toFixed(2),
					interestRate: interestRate,
				}),
				life: 3000,
			});

			setDepositValue(null);
			setInterestRate(null);
			setAgreesToTerms(false);
			setCheckboxEnabled(false);
			setDepositInvalid(false);
			setInterestInvalid(false);

			onHide();
		} catch (error: any) {
			toastRef.current?.show({
				severity: "error",
				summary: t(
					"createDepositDialog.notifications.errorCreatingDeposit.summary"
				),
				detail:
					error?.response?.data?.message ||
					error?.message ||
					t("createDepositDialog.notifications.errorCreatingDeposit.default"),
				life: 4000,
			});
		}
	};

	return (
		<Dialog
			header={t("createDepositDialog.title")}
			visible={visible}
			modal
			onHide={onHide}
			className="w-full max-w-3xl sm:max-w-lg md:max-w-2xl"
			contentClassName="p-0"
		>
			<div className="flex justify-center items-center w-full p-2 sm:p-4">
				<div className="block w-full">
					<Stepper
						linear
						ref={stepperRef}
						orientation="horizontal"
						className="w-full"
					>
						<StepperPanel header={t("createDepositDialog.steps.terms.header")}>
							<TermsStep
								agreesToTerms={agreesToTerms}
								setAgreesToTerms={setAgreesToTerms}
								checkboxEnabled={checkboxEnabled}
								setCheckboxEnabled={setCheckboxEnabled}
								onNext={() => stepperRef.current?.nextCallback()}
							/>
						</StepperPanel>

						<StepperPanel
							header={t("createDepositDialog.steps.configure.header")}
						>
							<ConfigureStep
								depositValue={depositValue}
								setDepositValue={setDepositValue}
								interestRate={interestRate}
								setInterestRate={setInterestRate}
								depositInvalid={depositInvalid}
								setDepositInvalid={setDepositInvalid}
								interestInvalid={interestInvalid}
								setInterestInvalid={setInterestInvalid}
								interestOptions={interestOptions}
								onNext={() => stepperRef.current?.nextCallback()}
								onPrev={() => stepperRef.current?.prevCallback()}
							/>
						</StepperPanel>

						<StepperPanel header={t("createDepositDialog.steps.review.header")}>
							<ReviewStep
								depositValue={depositValue}
								interestRate={interestRate}
								onPrev={() => stepperRef.current?.prevCallback()}
								onCreateDeposit={onCreateDeposit}
							/>
						</StepperPanel>
					</Stepper>
				</div>
			</div>
		</Dialog>
	);
}
