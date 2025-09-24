import React from "react";
import { Chart } from "primereact/chart";

type Deposit = {
    id: string;
    amount:number;
};

interface DepositChartProps {
    deposits: Deposit[];
    total: number
}

const colors = ["#42A5F5", "#66BB6A", "#FFA726", "#AB47BC", "#FF7043"];

const Depositchart: React.FC<DepositChartProps> = ({ deposits, total }) => {
    const data = {
        labels: deposits.map((d) => `Deposit ${d.id}`),
        datasets: [{
            data: deposits.map((d) => d.amount),
            backgroundColor: deposits.map((_, i) => colors[i % colors.length]),
        }],
    };

    const options = {
        cutout: "70%", // donut
        plugins: { legend: { display: false }, tooltip: { enabled: true } },
    };

    return (
        <div className="relative p-4 w-80">
            <Chart type="doughnut" data={data} options={options} />
            <div className="absolute top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2 text-center">
                <div className="text-lg font-semibold">Total</div>
                <div className="text-xl font-bold">{total} RON</div>
            </div>
        </div>
    );
};

export default Depositchart;
