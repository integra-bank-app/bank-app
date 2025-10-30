import React, { useCallback, useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { useTranslation } from "react-i18next";
import { useAuthentication } from "../../contexts/AuthenticationProvider";
import { Button } from "primereact/button";
import { InvestmentChart } from "./InvestmentChart";
import { InvestmentList } from "./InvestmentList";
import { InvestmentDTO } from "../../api";
import { depositColors } from "../../lib/utils";
import CreateInvestmentDialog from "../../components/CreateInvestmentDialog";
import {InvestmentHistoryDTO} from "../../api";

export const InvestmentsPage: React.FC = () => {
    const [showAddDialog, setShowAddDialog] = useState<boolean>(false);
    const [investments, setInvestments] = useState<InvestmentDTO[]>([]);
    const [history, setInvestmentsHistory] = useState<InvestmentHistoryDTO[]>([]);
    const [loading, setLoading] = useState(true);
    const { user, isAuthenticated } = useAuthentication();
    const navigate = useNavigate();
    const { t } = useTranslation();



    const loadInvestments = useCallback(async () => {
        if (!user?.id) {
            setLoading(false);
            return;
        }

        const token = localStorage.getItem("authToken");
        try {
            const [investmentsRes, historyRes] = await Promise.all([
                fetch(`http://localhost:8080/api/users/${user.id}/investment`, {
                    headers: {
                        "Content-Type": "application/json",
                        Authorization: `Bearer ${token}`,
                    },
                }),
                fetch(`http://localhost:8080/api/users/${user.id}/investments/history`, {
                    headers: {
                        "Content-Type": "application/json",
                        Authorization: `Bearer ${token}`,
                    },
                }),
            ]);

            if (!investmentsRes.ok || !historyRes.ok) throw new Error("Failed to fetch data");

            const [investmentsData, historyData] = await Promise.all([
                investmentsRes.json(),
                historyRes.json(),
            ]);

            setInvestments(investmentsData);
            setInvestmentsHistory(historyData);
        } catch (err) {
            console.error("Error fetching investments:", err);
        } finally {
            setLoading(false);
        }
    }, [user, showAddDialog]);

    useEffect(() => {
        if (!isAuthenticated) {
            setLoading(false);
            return;
        }
        loadInvestments();
    }, [isAuthenticated, loadInvestments]);

    const total = investments.reduce((sum, i) => sum + (i.balance ?? 0), 0);

    return (
        <div className="flex flex-col items-center justify-center min-h-5/6 p-6 space-y-6">
            <h1 className="text-3xl font-bold mb-4">{t("investmentsPage.title")}</h1>

            {loading ? (
                <p>{t("investmentsPage.loading")}</p>
            ) : investments.length > 0 ? (
                <>
                    <InvestmentChart total={total} />
                    <InvestmentList investments={investments} depositColors={depositColors} />
                </>
            ) : (
                <p>{t("investmentsPage.noInvestments")}</p>
            )}

            <CreateInvestmentDialog
                visible={showAddDialog}
                onHide={() => setShowAddDialog(false)}
                onCreated={loadInvestments}
            />

            <div className="mt-6 flex justify-center gap-4">

                <Button
                    className="p-button-lg p-button-primary"
                    onClick={() => setShowAddDialog(true)}
                >
                    {t("investmentsPage.createButton")}
                </Button>
            </div>
        </div>
    );
};

