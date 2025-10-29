import React, { useCallback, useEffect, useState } from "react";
import { Button } from "primereact/button";
import { depositColors } from "../../lib/utils";
import DepositChart from "./DepositChart";
import DepositsList from "./DepositsList";
import { DepositsDTO, UserControllerApi } from "../../api";
import { useNavigate } from "react-router-dom";
import { useAuthentication } from "../../contexts/AuthenticationProvider";
import { useTranslation } from "react-i18next";
import { CreateDepositDialog } from "../../components/CreateDepositDialog";

const DepositsPage: React.FC = () => {
	const [showAddDialog, setShowAddDialog] = useState<boolean>(false);
	const [deposits, setDeposits] = useState<DepositsDTO[]>([]);
	const [loading, setLoading] = useState(true);
	const { user, isAuthenticated } = useAuthentication();
	const navigate = useNavigate();
	const { t } = useTranslation();

	const loadDeposits = useCallback(async () => {
		if (!user?.id) {
			setLoading(false);
			return;
		}

		setLoading(true);
		try {
			const userControllerApi = new UserControllerApi();
			const response = await userControllerApi.getUserDeposits(user.id);
			setDeposits(response.data);
		} catch (err) {
			console.error("Error fetching deposits:", err);
		} finally {
			setLoading(false);
		}
	}, [user, showAddDialog]);

	useEffect(() => {
		if (!isAuthenticated) {
			setLoading(false);
			return;
		}
		loadDeposits();
	}, [isAuthenticated, loadDeposits]);

	const total = deposits.reduce((sum, d) => sum + (d.amount ?? 0), 0);

	return (
		<div className="flex flex-col items-center justify-center min-h-5/6 p-6 space-y-6">
			<h1 className="text-3xl font-bold mb-4">{t("depositsPage.title")}</h1>

			{loading ? (
				<p>{t("depositsPage.loading")}</p>
			) : deposits.length > 0 ? (
				<>
					<DepositChart deposits={deposits} total={total} />
					<DepositsList deposits={deposits} depositColors={depositColors} />
				</>
			) : (
				<p>{t("depositsPage.noDeposits")}</p>
			)}
			<CreateDepositDialog
				visible={showAddDialog}
				onHide={() => {
					setShowAddDialog(false);
				}}
			/>
			<div className="mt-6 flex justify-center gap-4">
				<Button
					className="p-button-lg p-button-primary"
					icon="pi pi-angle-left"
					onClick={() => navigate("/")}
				>
					{t("depositsPage.backButton")}
				</Button>

				<Button
					className="p-button-lg p-button-primary"
					onClick={() => setShowAddDialog(true)}
				>
					{t("depositsPage.createButton")}
				</Button>
			</div>
		</div>
	);
};

export default DepositsPage;
