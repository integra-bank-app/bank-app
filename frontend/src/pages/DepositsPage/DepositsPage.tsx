import React, { useEffect, useState } from "react";
import { Button } from "primereact/button";
import { depositColors } from "../../lib/utils";
import DepositChart from "./DepositChart";
import DepositsList from "./DepositsList";
import { useNavigate } from "react-router-dom";
import { useAuthentication } from "../../contexts/AuthenticationProvider";

const DepositsPage: React.FC = () => {
    const [deposits, setDeposits] = useState<any[]>([]);
    const [loading, setLoading] = useState(true);
    const { user } = useAuthentication();
    const navigate = useNavigate();

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
            <h1 className="text-3xl font-bold mb-4">My Deposits</h1>

            {loading ? (
                <p>No deposits...</p>
            ) : deposits.length > 0 ? (
                <>
                    <DepositChart deposits={deposits} total={total} />
                    <DepositsList deposits={deposits} depositColors={depositColors} />
                </>
            ) : (
                <p>No deposits found.</p>
            )}

            <div className="mt-6 flex justify-center gap-4">
                <Button
                    className="p-button-lg p-button-primary"
                    icon="pi pi-angle-left"
                    onClick={() => navigate("/")}
                >
                    Back
                </Button>

                <Button className="p-button-lg p-button-primary" disabled>
                    + Create New Deposit
                </Button>
            </div>
        </div>
    );
};

export default DepositsPage;