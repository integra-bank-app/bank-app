import React, { useEffect, useState } from "react";
import { Button } from "primereact/button";
import { depositColors } from "../../lib/utils";
import DepositChart from "./DepositChart";
import DepositsList from "./DepositsList";
import { DepositsDTO } from "../../api";
import { useNavigate } from "react-router-dom";
import { useAuthentication } from "../../contexts/AuthenticationProvider";
import { useTranslation } from "react-i18next";

const DepositsPage: React.FC = () => {
	const [showAddDialog, setShowAddDialog] = useState<boolean>(false);
	const [deposits, setDeposits] = useState<DepositsDTO[]>([]);
	const [loading, setLoading] = useState(true);
    const { user } = useAuthentication();
    const navigate = useNavigate();
    const { t } = useTranslation();

    useEffect(() => {
        if (!user?.id) {
            setLoading(false);
            return;
        }

        const token = localStorage.getItem("authToken");
        const loadDeposits = async () => {
            try {
                const response = await fetch(
                    `http://localhost:8080/api/users/${user.id}/deposits`,
                    {
                        headers: {
                            "Content-Type": "application/json",
                            Authorization: `Bearer ${token}`,
                        },
                    }
                );
                if (!response.ok) {
                    throw new Error(`HTTP error! status: ${response.status}`);
                }
                const data = await response.json();
                setDeposits(data);
            } catch (err) {
                console.error("Error fetching deposits:", err);
            } finally {
                setLoading(false);
            }
        };

        loadDeposits();
    }, [user?.id]);

	const total = deposits.reduce((sum, d) => sum + (d.amount ?? 0), 0);

    return (
        <div className="flex flex-col items-center justify-center min-h-screen p-6 space-y-6">
            <h1 className="text-3xl font-bold mb-4">{t("deposits.title")}</h1>

            {loading ? (
                <p>{t("deposits.loading")}</p>
            ) : deposits.length > 0 ? (
                <>
                    <DepositChart deposits={deposits} total={total} />
                    <DepositsList deposits={deposits} depositColors={depositColors} />
                </>
            ) : (
                <p>{t("deposits.notFound")}</p>
            )}

            <div className="mt-6 flex justify-center gap-4">
                <Button
                    className="p-button-lg p-button-primary"
                    icon="pi pi-angle-left"
                    onClick={() => navigate("/")}
                >
                    {t("deposits.back")}
                </Button>

                <Button className="p-button-lg p-button-primary"
                        onClick={() => setShowAddDialog(true)}
                >
                    {t("deposits.createNew")}
                </Button>
            </div>
        </div>
    );
};

export default DepositsPage;