import { useState } from "react";
import { Button } from "primereact/button";
import { SendMoneyDialog } from "./SendMoneyDialog";
import { useTranslation } from "react-i18next";

export function SendMoney() {
    const [sendMoneyDialogVisible, setSendMoneyDialogVisible] = useState(false);
    const { t } = useTranslation();

    return (
        <>
            <Button
                label={t("userMain.sendMoney") as string}
                icon="pi pi-send"
                onClick={() => setSendMoneyDialogVisible(true)}
            />
            <SendMoneyDialog
                visible={sendMoneyDialogVisible}
                onHide={() => setSendMoneyDialogVisible(false)}
            />
        </>
    );
}
