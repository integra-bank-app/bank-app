import {Dialog} from "primereact/dialog";
import {useTranslation} from "react-i18next";
import {useState} from "react";
import {useNotificationContext} from "../lib/hooks";
import {UserControllerApi} from "../api";
import {useAuthentication} from "../contexts/AuthenticationProvider";
import {Button} from "primereact/button";
import {InputText} from "primereact/inputtext";
import {InputNumber} from "primereact/inputnumber";

type SendMoneyDialogProps = {
	visible: boolean;
	onHide: () => void;
};

export function SendMoneyDialog({visible, onHide}: SendMoneyDialogProps) {
	const {t} = useTranslation();
	const {user} = useAuthentication();
	const {toastRef} = useNotificationContext();
	const [recipientId, setRecipientId] = useState<string>("");
	const [amount, setAmount] = useState<number | null>(null);
	const [recipientIdInvalid, setRecipientIdInvalid] = useState(false);
	const [amountInvalid, setAmountInvalid] = useState(false);

	const onSendMoney = async () => {
		if (!recipientId) {
			setRecipientIdInvalid(true);
		}
		if (!amount) {
			setAmountInvalid(true);
		}

		if (!recipientId || !amount) {
			return;
		}

		if (amount <= 0) {
			toastRef.current?.show({
				severity: "warn",
				summary: t("sendMoneyDialog.notifications.negativeAmount.summary"),
				detail: t("sendMoneyDialog.notifications.negativeAmount.detail"),
				life: 3000,
			});
			return;
		}

		toastRef.current?.show({
			severity: "info",
			summary: t("sendMoneyDialog.notifications.sendingMoney.summary"),
			detail: t("sendMoneyDialog.notifications.sendingMoney.detail", {
				amount: amount.toFixed(2),
				recipient: recipientId,
			}),
			life: 1500,
		});

		try {
			if (!user) {
				throw new Error("User not authenticated. Please log in.");
			}

			const userControllerApi = new UserControllerApi();
			await userControllerApi.transferMoney(user.id, recipientId, amount);

			toastRef.current?.show({
				severity: "success",
				summary: t("sendMoneyDialog.notifications.moneySent.summary"),
				detail: t("sendMoneyDialog.notifications.moneySent.detail", {
					amount: amount.toFixed(2),
					recipient: recipientId,
				}),
				life: 3000,
			});

			setRecipientId("");
			setAmount(null);
			setRecipientIdInvalid(false);
			setAmountInvalid(false);

			window.dispatchEvent(new Event("refetchData"));
			onHide();
		} catch (error: any) {
			toastRef.current?.show({
				severity: "error",
				summary: t("sendMoneyDialog.notifications.errorSendingMoney.summary"),
				detail:
					error?.response?.data?.message ||
					error?.message ||
					t("sendMoneyDialog.notifications.errorSendingMoney.default"),
				life: 4000,
			});
		}
	};

	return (
		<Dialog
			header={t("sendMoneyDialog.title")}
			visible={visible}
			modal
			onHide={onHide}
			className="w-full max-w-md"
		>
			<div className="flex flex-col gap-4 p-fluid">
				<div className="flex flex-col">
					<label htmlFor="recipientId" className="font-bold mb-2">
						{t("sendMoneyDialog.recipientId")}
					</label>
					<InputText
						id="recipientId"
						value={recipientId}
						onChange={(e) => setRecipientId(e.target.value)}
						className={recipientIdInvalid ? "p-invalid" : ""}
					/>
				</div>
				<div className="flex flex-col">
					<label htmlFor="amount" className="font-bold mb-2">
						{t("sendMoneyDialog.amount")}
					</label>
					<InputNumber
						id="amount"
						value={amount}
						onValueChange={(e) => setAmount(e.value ?? null)}
						mode="currency"
						currency="RON"
						locale="ro-RO"
						minFractionDigits={2}
						maxFractionDigits={2}
						className={amountInvalid ? "p-invalid" : ""}
					/>
				</div>
				<div className="flex justify-end space-x-2">
					<Button
						label={t("sendMoneyDialog.cancel")}
						icon="pi pi-times"
						onClick={onHide}
						className="p-button-text"
					/>
					<Button
						label={t("sendMoneyDialog.send")}
						icon="pi pi-send"
						onClick={onSendMoney}
					/>
				</div>
			</div>
		</Dialog>
	);
}
