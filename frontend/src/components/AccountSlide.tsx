import { useTranslation } from "react-i18next";

type AccountSlideProps = {
    accountId: string;
    balance: number | null;
};

export function AccountSlide({ accountId, balance }: AccountSlideProps) {
    const { t } = useTranslation();

    return (
        <div className="flex flex-col items-center p-4 bg-surface-100 rounded shadow">
            <h2>{t("userMain.accountWithId", { id: accountId })}</h2>
            <p>
                {t("userMain.balance")}: <strong>{balance !== null ? balance : t("userMain.loadingBalance")}</strong>
            </p>
        </div>
    );
}