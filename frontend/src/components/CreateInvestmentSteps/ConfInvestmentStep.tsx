import { InputNumber } from "primereact/inputnumber";
import { Dropdown } from "primereact/dropdown";
import { Button } from "primereact/button";
import { useTranslation } from "react-i18next";

type ConfigureInvestmentStepProps = {
    risk: number | null;
    setRisk: (v: number | null) => void;
    balance: number | null;
    setBalance: (v: number | null) => void;
    balanceInvalid: boolean;
    setBalanceInvalid: (v: boolean) => void;
    riskInvalid: boolean;
    setRiskInvalid: (v: boolean) => void;
    riskOptions: { name: string; value: number }[];
    onNext: () => void;
    onPrev: () => void;
};

export default function ConfigureInvestmentStep({
                                                    risk,
                                                    setRisk,
                                                    balance,
                                                    setBalance,
                                                    balanceInvalid,
                                                    setBalanceInvalid,
                                                    riskInvalid,
                                                    setRiskInvalid,
                                                    riskOptions,
                                                    onNext,
                                                    onPrev,
                                                }: ConfigureInvestmentStepProps) {
    const { t } = useTranslation();

    const RISK_OPTIONS = Array.from({ length: 10 }, (_, i) => ({
        name: `Risk ${i + 1}`,
        value: i + 1,
    }));

    const handleNext = () => {
        const isBalanceValid = balance !== null && balance > 0;
        const isRiskValid = risk !== null;

        setBalanceInvalid(!isBalanceValid);
        setRiskInvalid(!isRiskValid);

        if (isBalanceValid && isRiskValid) {
            onNext();
        }
    };

    return (
        <div className="flex flex-col w-full">
            <div className="flex flex-col gap-6 min-h-[400px] sm:min-h-[480px] p-2 sm:p-4">
                {/* Balance */}
                <div className="flex flex-col w-full">
                    <label htmlFor="balance" className="mb-2 font-semibold text-sm sm:text-base">
                        {t("investmentsPage.configureStep.invBalance")}
                    </label>
                    <InputNumber
                        id="balance"
                        value={balance}
                        onValueChange={(e) => setBalance(e.value ?? null)}
                        mode="decimal"
                        min={0}
                        placeholder={t("investmentsPage.configureStep.enterBalance")}
                        className={`${balanceInvalid ? "p-invalid" : ""} w-full`}
                    />
                </div>

                {/* Risk */}
                <div className="flex flex-col w-full">
                    <label htmlFor="risk" className="mb-2 font-semibold text-sm sm:text-base">
                        {t("investmentsPage.configureStep.risk")}
                    </label>
                    <Dropdown
                        id="risk"
                        value={risk}
                        onChange={(e) => setRisk(e.value)}
                        options={RISK_OPTIONS}
                        optionLabel="name"
                        placeholder={t("investmentsPage.configureStep.selectRisk")}
                        className={`${riskInvalid ? "p-invalid" : ""} w-full`}
                    />
                </div>
            </div>

            <div className="flex flex-col-reverse sm:flex-row justify-between gap-2 sm:gap-4 pt-6 px-2 sm:px-4">
                <Button
                    label={t("investmentsPage.configureStep.return")}
                    severity="secondary"
                    icon="pi pi-arrow-left"
                    onClick={onPrev}
                    className="w-full sm:w-auto"
                />
                <Button
                    label={t("investmentsPage.configureStep.review")}
                    icon="pi pi-arrow-right"
                    iconPos="right"
                    onClick={handleNext}
                    className="w-full sm:w-auto"
                />
            </div>
        </div>
    );
}
