import { useState } from "react";
import { Dialog } from "primereact/dialog";
import { InputText } from "primereact/inputtext";
import { Button } from "primereact/button";
import { UserWithBranchDTO, UserControllerApi } from "../api";
import { useTranslation } from "react-i18next";

type AddUserDialogProps = {
	visible: boolean;
	onHide: () => void;
	onUserAdded: () => void;
	branchId: string;
};

export default function AddUserToBranchDialog({
	visible,
	onHide,
	onUserAdded,
	branchId,
}: AddUserDialogProps) {
	const { t } = useTranslation();
	const [firstName, setFirstName] = useState("");
	const [lastName, setLastName] = useState("");
	const [middleName, setMiddleName] = useState("");
	const [errors, setErrors] = useState<{
		firstName?: string;
		lastName?: string;
	}>({});
	const [loading, setLoading] = useState(false);

	const validate = () => {
		const newErrors: typeof errors = {};
		if (!firstName.trim())
			newErrors.firstName = t("addUserToBranchDialog.errorFirstName");
		if (!lastName.trim())
			newErrors.lastName = t("adUserToBranchDialog.errorLastName");
		setErrors(newErrors);
		return Object.keys(newErrors).length === 0;
	};

	const handleAddUser = async () => {
		if (!validate()) return;

		setLoading(true);

		try {
			const api = new UserControllerApi();
			const user: UserWithBranchDTO = {
				firstName,
				middleName,
				lastName,
				branchId,
			};
			await api.addUser(user);

			setFirstName("");
			setLastName("");
			setMiddleName("");
			setErrors({});
			onHide();
			onUserAdded();
		} catch (err: any) {
			console.error(t("adUserToBranchDialog.errorAddingUser"), err);
			alert(err.message || t("adUserToBranchDialog.failedToAddUser"));
		} finally {
			setLoading(false);
		}
	};

	return (
		<Dialog
			header={t("addUserToBranchDialog.addUserHeader")}
			visible={visible}
			style={{ width: "400px" }}
			modal
			onHide={onHide}
		>
			<div className="flex flex-column gap-3">
				<div>
					<label htmlFor="firstName" className="block mb-1">
						{t("addUserToBranchDialog.firstNameLabel")}
					</label>
					<InputText
						id="firstName"
						value={firstName}
						onChange={(e) => setFirstName(e.target.value)}
						onKeyDown={(e) => e.key === "Enter" && handleAddUser()}
						className={errors.firstName ? "p-invalid w-full" : "w-full"}
					/>
					{errors.firstName && (
						<small className="p-error">{errors.firstName}</small>
					)}
				</div>

				<div>
					<label htmlFor="middleName" className="block mb-1">
						{t("addUserToBranchDialog.middleNameLabel")}
					</label>
					<InputText
						id="middleName"
						value={middleName}
						onChange={(e) => setMiddleName(e.target.value)}
						onKeyDown={(e) => e.key === "Enter" && handleAddUser()}
						className="w-full"
					/>
				</div>

				<div>
					<label htmlFor="lastName" className="block mb-1">
						{t("addUserToBranchDialog.lastNameLabel")}
					</label>
					<InputText
						id="lastName"
						value={lastName}
						onChange={(e) => setLastName(e.target.value)}
						onKeyDown={(e) => e.key === "Enter" && handleAddUser()}
						className={errors.lastName ? "p-invalid w-full" : "w-full"}
					/>
					{errors.lastName && (
						<small className="p-error">{errors.lastName}</small>
					)}
				</div>

				<Button
					label={
						loading
							? t("addUserToBranchDialog.addingButton")
							: t("addUserToBranchDialog.addUserButton")
					}
					icon="pi pi-check"
					onClick={handleAddUser}
					disabled={loading}
				/>
			</div>
		</Dialog>
	);
}
