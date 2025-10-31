import {useState} from "react";
import {Button} from "primereact/button";
import {useNavigate} from "react-router-dom";
import {useAuthentication} from "../contexts/AuthenticationProvider";
import AddUserToBranchDialog from "../components/AddUserToBranchDialog";
import BulkImportDeposits from "../components/BulkImportDeposits";
import {useTranslation} from "react-i18next";
import {DepositControllerApi} from "../api";

function AdminPage() {
	const {user} = useAuthentication();
	const [showAddUser, setShowAddUser] = useState(false);
	const navigate = useNavigate();
	const {t} = useTranslation();
	const [showImportPanel, setShowImportPanel] = useState(false);

	const handleExportDeposits = async () => {
		const depositControllerApi = new DepositControllerApi();
		try {
			const response = await depositControllerApi.exportDeposits();
			const blob = new Blob([JSON.stringify(response.data)], {
				type: "application/json",
			});
			const url = window.URL.createObjectURL(blob);
			const a = document.createElement("a");
			a.href = url;
			a.download = "deposits_export.json";
			document.body.appendChild(a);
			a.click();
			document.body.removeChild(a);
			window.URL.revokeObjectURL(url);
		} catch (err) {
			console.error("Error exporting deposits:", err);
		}
	};

	return (
		<section className="flex flex-column align-items-center p-4 gap-4">
			<h1 className="text-5xl font-bold">
				{t("adminPage.hello")}, {user?.firstName}
			</h1>

			<div className="grid w-full" style={{maxWidth: "600px"}}>
				<div className="col-12 md:col-4">
					<Button
						label={t("adminPage.addUserLabel")}
						onClick={() => setShowAddUser(true)}
						className="w-full"
					/>
				</div>
				<div className="col-12 md:col-4">
					<Button
						label={t("adminPage.exportUserLabel")}
						icon="pi pi-download"
						className="w-full"
					/>
				</div>
				<div className="col-12 md:col-4">
					<Button label={t("adminPage.settingsLabel")} className="w-full" />
				</div>
				<div className="col-12 md:col-4">
					<Button
						label={t("adminPage.importLabel")}
						icon="pi pi-upload"
						onClick={() => setShowImportPanel(true)}
						className="w-full p-button-primary"
					/>
				</div>
				<div className={"col-12 md:col-4"}>
					<Button
						label={t("adminPage.exportLabel")}
						icon="pi pi-download"
						onClick={handleExportDeposits}
						className="w-full"
					/>
				</div>
			</div>

			{showImportPanel && (
				<div className="w-full" style={{maxWidth: "800px"}}>
					<BulkImportDeposits onClose={() => setShowImportPanel(false)} />
				</div>
			)}

			<div className="w-full" style={{maxWidth: "800px"}}></div>

			<AddUserToBranchDialog
				branchId={user?.branchId ?? ""}
				visible={showAddUser}
				onHide={() => setShowAddUser(false)}
				onUserAdded={() => navigate("/users")}
			/>
		</section>
	);
}

export default AdminPage;
