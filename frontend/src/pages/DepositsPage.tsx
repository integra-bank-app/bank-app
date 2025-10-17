import React, { useEffect, useState } from "react";
import { Chart } from "primereact/chart";
import { Button } from "primereact/button";

type Deposit = {
    id: string;
    amount: number;
};

export type PageKey = "start" | "deposits";

interface DepositsPageProps {
    goToPage: (page: PageKey) => void;
}

const DepositsPage: React.FC<DepositsPageProps> = ({ goToPage }) => {
    const [deposits, setDeposits] = useState<Deposit[]>([]);
    const [loading, setLoading] = useState(true);

    const colors = ["#f542da", "#42f5f5", "#c5f542", "#AB47BC", "#FF7043"];

    useEffect(() => {
        // Random deposits for demo
        const fakeDeposits: Deposit[] = [
            { id: "1", amount: 1000 },
            { id: "2", amount: 2500 },
            { id: "3", amount: 500 },
        ];
        setDeposits(fakeDeposits);
        setLoading(false);
    }, []);

    const total = deposits.reduce((sum, d) => sum + d.amount, 0);

    const chartData = {
        labels: deposits.map((d) => `Deposit ${d.id}`),
        datasets: [
            {
                data: deposits.map((d) => d.amount),
                backgroundColor: deposits.map((_, i) => colors[i % colors.length]),
                hoverBackgroundColor: deposits.map((_, i) => colors[i % colors.length]),
            },
        ],
    };

    const chartOptions = {
        cutout: "70%",
        plugins: {
            legend: { display: false },
            tooltip: { enabled: true },
        },
    };

    return (
        <div className="flex flex-col items-center justify-center min-h-screen p-6 space-y-6">
            <h1 className="text-3xl font-bold mb-4">My Deposits</h1>

            {loading ? (
                <p>No deposits...</p>
            ) : deposits.length > 0 ? (
                <>
                    <div className="relative w-64 h-64">
                        <Chart type="doughnut" data={chartData} options={chartOptions} />
                        <div className="absolute inset-0 flex flex-col items-center justify-center">
                            <span className="text-lg font-semibold">Total</span>
                            <span className="text-2xl font-bold">{total} RON</span>
                        </div>
                    </div>

                    <div className="mt-6 w-full max-w-md">
                        <h2 className="text-xl font-semibold mb-2">Deposit List</h2>
                        <ul className="space-y-4">
                            {deposits.map((deposit, index) => (
                                <li
                                    key={deposit.id}
                                    className="flex justify-between items-center p-3 rounded-lg shadow-sm border-2"
                                    style={{ borderColor: colors[index % colors.length] }}
                                >
                                    <div className="flex flex-col">
                                        <div className="flex flex-col">
                                            <span className="font-semibold text-lg">
                                                Deposit {deposit.id}
                                            </span>
                                            <span className="text-base font-normal">
                                                {deposit.amount} RON
                                            </span>
                                        </div>
                                    </div>
                                </li>
                            ))}
                        </ul>
                    </div>
                </>
            ) : (
                <p>No deposits found.</p>
            )}

            <div className="mt-6 flex justify-center gap-4">
                <Button
                    className=" p-button-lg p-button-primary"
                    icon="pi pi-angle-left"
                    onClick={() => goToPage("start")}
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
