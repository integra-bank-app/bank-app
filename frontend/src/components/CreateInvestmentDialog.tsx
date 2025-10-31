import { Dialog } from "primereact/dialog";
import { Stepper } from "primereact/stepper";
import { StepperPanel } from "primereact/stepperpanel";
import { useTranslation } from "react-i18next";
import { useRef, useState } from "react";
import { useNotificationContext } from "../lib/hooks";
import ConfigureInvestmentStep from "./CreateInvestmentSteps/ConfInvestmentStep";
import ReviewInvestmentStep from "./CreateInvestmentSteps/ReviewInvestmentStep";
import TermsStep from "./CreateDepositSteps/TermsStep";
import { useAuthentication } from "../contexts/AuthenticationProvider";
import { InvestmentsControllerApi } from "../api";


export type InvestmentDTO = {
    id?: string;
    risk: number;
    balance: number;
};

type CreateInvestmentDialogProps = {
    visible: boolean;
    onHide: () => void;
    onCreated?: () => void;
};

export default function CreateInvestmentDialog({
                                                   visible,
                                                   onHide,
                                                   onCreated,
                                               }: CreateInvestmentDialogProps) {
    const { t } = useTranslation();
    const { user } = useAuthentication();
    const { toastRef } = useNotificationContext();
    const stepperRef = useRef<Stepper>(null);

    const [balance, setBalance] = useState<number | null>(null);
    const [risk, setRisk] = useState<number | null>(null);
    const [agreesToTerms, setAgreesToTerms] = useState(false);
    const [checkboxEnabled, setCheckboxEnabled] = useState(false);

    const [balanceInvalid, setBalanceInvalid] = useState(false);
    const [riskInvalid, setRiskInvalid] = useState(false);

    const RISK_OPTIONS = Array.from({ length: 10 }, (_, i) => ({
        name: `Risk ${i + 1}`,
        value: i + 1,
    }));

    const onCreateInvestment = async () => {
        if (!balance || !risk) {
            setRiskInvalid(!risk);
            setBalanceInvalid(!balance);
            return;
        }

        if (!agreesToTerms) {
            toastRef.current?.show({
                severity: "warn",
                summary: t("createInvestmentDialog.notifications.termsNotAgreed.summary"),
                detail: t("createInvestmentDialog.notifications.termsNotAgreed.detail"),
                life: 3000,
            });
            return;
        }

        toastRef.current?.show({
            severity: "info",
            summary: t("createInvestmentDialog.notifications.creatingInvestment.summary"),
            detail: t("createInvestmentDialog.notifications.creatingInvestment.detail", {
                balance,
            }),
            life: 1500,
        });

        try {
            if (!user) throw new Error("User not authenticated. Please log in.");

            const investmentsApi = new InvestmentsControllerApi();
            const investmentDTO: InvestmentDTO = { risk, balance };

            await investmentsApi.createInvestment(user.id, investmentDTO);

            toastRef.current?.show({
                severity: "success",
                summary: t("createInvestmentDialog.notifications.investmentCreated.summary"),
                detail: t("createInvestmentDialog.notifications.investmentCreated.detail", {
                    risk,
                    balance,
                }),
                life: 3000,
            });

            setRisk(null);
            setBalance(null);
            setAgreesToTerms(false);
            setCheckboxEnabled(false);
            setBalanceInvalid(false);
            setRiskInvalid(false);

            onHide();
            onCreated?.();
        } catch (error: any) {
            toastRef.current?.show({
                severity: "error",
                summary: t("createInvestmentDialog.notifications.errorCreatingInvestment.summary"),
                detail: error?.message || t("createInvestmentDialog.notifications.errorCreatingInvestment.default"),
                life: 4000,
            });
        }
    };

    return (
        <Dialog
            header={t("createInvestmentDialog.title")}
            visible={visible}
            modal
            onHide={onHide}
            className="w-full max-w-3xl sm:max-w-lg md:max-w-2xl"
            contentClassName="p-0"
        >
            <div className="flex justify-center items-center w-full p-2 sm:p-4">
                <div className="block w-full">
                    <Stepper linear ref={stepperRef} orientation="horizontal" className="w-full">
                        {/* Terms Step */}
                        <StepperPanel header={t("createInvestmentDialog.steps.terms.header")}>
                            <TermsStep
                                agreesToTerms={agreesToTerms}
                                setAgreesToTerms={setAgreesToTerms}
                                checkboxEnabled={checkboxEnabled}
                                setCheckboxEnabled={setCheckboxEnabled}
                                onNext={() => stepperRef.current?.nextCallback()}
                            />
                        </StepperPanel>

                        {/* Configure Step */}
                        <StepperPanel header={t("createInvestmentDialog.steps.configure.header")}>
                            <ConfigureInvestmentStep
                                risk={risk}
                                setRisk={setRisk}
                                balance={balance}
                                setBalance={setBalance}
                                riskInvalid={riskInvalid}
                                setRiskInvalid={setRiskInvalid}
                                balanceInvalid={balanceInvalid}
                                setBalanceInvalid={setBalanceInvalid}
                                riskOptions={RISK_OPTIONS}
                                onNext={() => stepperRef.current?.nextCallback()}
                                onPrev={() => stepperRef.current?.prevCallback()}
                            />
                        </StepperPanel>

                        {/* Review Step */}
                        <StepperPanel header={t("createInvestmentDialog.steps.review.header")}>
                            <ReviewInvestmentStep
                                risk={risk}
                                balanceValue={balance}
                                onPrev={() => stepperRef.current?.prevCallback()}
                                onCreateInvestment={onCreateInvestment}
                            />
                        </StepperPanel>
                    </Stepper>
                </div>
            </div>
        </Dialog>
    );
}
