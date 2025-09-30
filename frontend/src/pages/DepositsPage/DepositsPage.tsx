import React, {useEffect, useState} from "react";
import {Button} from "primereact/button";
import {depositColors} from "../../lib/utils";
import DepositChart from "./DepositChart";
import DepositsList from "./DepositsList";
import {useNotificationContext} from "../../lib/hooks";
import {DepositsDTO, UserControllerApi} from "../../api";
import {useNavigate} from "react-router-dom";

const DepositsPage: React.FC = () => {
    const [deposits, setDeposits] = useState<DepositsDTO[]>([]);
    const [loading, setLoading] = useState(true);
    const {uuid} = useNotificationContext()
    const navigate = useNavigate();

    useEffect(() => {
        console.log(uuid);

        if (!uuid) {
            setLoading(false);
            return;
        }

        const loadDeposits = async () => {
            try {
                const userApi = new UserControllerApi();
                const respons = await userApi.getUserDeposits(uuid);
                setDeposits(respons.data);
            } catch (err) {
                console.error("Error fetching deposits:", err);
            } finally {
                setLoading(false);
            }
        }

        loadDeposits()
    }, [uuid]);

    const total = deposits.reduce((sum, d) => sum + (d.amount ?? 0), 0);

    return (
        <div className="flex flex-col items-center justify-center min-h-screen p-6 space-y-6">
            <h1 className="text-3xl font-bold mb-4">My Deposits</h1>

            {loading ? (
                <p>No deposits...</p>
            ) : deposits.length > 0 ? (
                <>
                    <DepositChart deposits={deposits} total={total}/>
                    <DepositsList deposits={deposits} depositColors={depositColors}/>
                </>
            ) : (
                <p>No deposits found.</p>
            )}

            <div className="mt-6 flex justify-center gap-4">
                <Button
                    className=" p-button-lg p-button-primary"
                    icon="pi pi-angle-left"
                    onClick={() => navigate("/")}
                >
                    Back
                </Button>

                <Button
                    className="p-button-lg p-button-primary"
                    disabled
                >
                    + Create New Deposit
                </Button>
            </div>
        </div>
    );
};

export default DepositsPage;
