import { Dialog } from "primereact/dialog";
import { Stepper } from "primereact/stepper";
import { StepperPanel } from "primereact/stepperpanel";
import { useRef, useState } from "react";
import { useNotificationContext, useUserContext } from "../lib/hooks";
import ReviewStep from "./CreateDepositSteps/ReviewStep";
import ConfigureStep from "./CreateDepositSteps/ConfigureStep";
import TermsStep from "./CreateDepositSteps/TermsStep";
import { INTREST_RATE_OPTIONS } from "../lib/constants";
import { DepositControllerApi, DepositsDTO } from "../api";
import { TUser } from "../lib/types";

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

	const { user } = useUserContext();
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
				summary: "Terms Not Agreed",
				detail: "You must agree to the terms and conditions to proceed.",
				life: 3000,
			});
			return;
		}

		toastRef.current?.show({
			severity: "info",
			summary: "Creating Deposit",
			detail: `Creating your deposit of $${depositValue.toFixed(2)}...`,
			life: 1500,
		});

		try {
			const depositDTO: DepositsDTO = {
				id: undefined,
				interest_rate: interestRate,
				amount: depositValue,
			};

			const api = new DepositControllerApi();
			const response = await api.createDeposit(user.uuid, depositDTO);

			toastRef.current?.show({
				severity: "success",
				summary: "Deposit Created",
				detail: `Deposit of $${depositValue.toFixed(
					2
				)} at ${interestRate}% created successfully. ID: ${response.data}`,
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
				summary: "Error Creating Deposit",
				detail:
					error?.response?.data?.message ||
					error?.message ||
					"An unexpected error occurred.",
				life: 4000,
			});
		}
	};

	return (
		<Dialog
			header="Create New Deposit"
			visible={visible}
			modal
			onHide={onHide}
			className="w-full max-w-3xl sm:max-w-lg md:max-w-2xl"
			contentClassName="p-0"
		>
			<div className="flex justify-center items-center w-full p-2 sm:p-4">
				{/* Horizontal on medium+ screens */}
				<div className="hidden sm:block w-full">
					<Stepper
						linear
						ref={stepperRef}
						orientation="horizontal"
						className="w-full"
					>
						<StepperPanel header="Terms and Conditions">
							<TermsStep
								agreesToTerms={agreesToTerms}
								setAgreesToTerms={setAgreesToTerms}
								checkboxEnabled={checkboxEnabled}
								setCheckboxEnabled={setCheckboxEnabled}
								onNext={() => stepperRef.current?.nextCallback()}
							/>
						</StepperPanel>

						<StepperPanel header="Configure">
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

						<StepperPanel header="Review and Confirm">
							<ReviewStep
								depositValue={depositValue}
								interestRate={interestRate}
								onPrev={() => stepperRef.current?.prevCallback()}
								onCreateDeposit={onCreateDeposit}
							/>
						</StepperPanel>
					</Stepper>
				</div>

				{/* Vertical on mobile screens */}
				<div className="block sm:hidden w-full">
					<Stepper
						linear
						ref={stepperRef}
						orientation="vertical"
						className="w-full"
					>
						<StepperPanel header="Terms and Conditions">
							<TermsStep
								agreesToTerms={agreesToTerms}
								setAgreesToTerms={setAgreesToTerms}
								checkboxEnabled={checkboxEnabled}
								setCheckboxEnabled={setCheckboxEnabled}
								onNext={() => stepperRef.current?.nextCallback()}
							/>
						</StepperPanel>

						<StepperPanel header="Configure">
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

						<StepperPanel header="Review and Confirm">
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
