import { useTranslation } from "react-i18next";

type TotalBalanceSlideProps = {
    totalBalance: number | null;
};

export function TotalBalanceSlide({ totalBalance }: TotalBalanceSlideProps) {
    const { t } = useTranslation();

    return (
        <div className="flex flex-col items-center p-4 bg-surface-100 rounded shadow">
            <h2>{t("userMain.totalBalance")}</h2>
            <p>
                {t("userMain.balance")}: <strong>{totalBalance !== null ? totalBalance : t("userMain.loadingBalance")}</strong>
            </p>
        </div>
    );
}