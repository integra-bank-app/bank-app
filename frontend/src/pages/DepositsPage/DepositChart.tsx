import React from "react";
import {Chart} from "primereact/chart";
import {depositColors} from "../../lib/utils";
import {DepositsDTO} from "../../api";

interface DepositChartProps {
    deposits: DepositsDTO[];
    total: number
}

const DepositChart: React.FC<DepositChartProps> = ({deposits, total}) => {
    const data = {
        labels: deposits.map((_, index) => `Deposit ${index + 1}`),
        datasets: [{
            data: deposits.map((d) => d.amount ?? 0),
            backgroundColor: deposits.map((_, i) => depositColors[i % depositColors.length]),
            hoverBackgroundColor: deposits.map((_, i) => depositColors[i % depositColors.length]),
        }],
    };

    const options = {
        cutout: "70%",
        plugins: {
            legend: {display: false},
            tooltip: {enabled: true}
        },
    };

    return (
        <div className="relative w-64 h-64">
            <Chart type="doughnut" data={data} options={options}/>
            <div className="absolute inset-0 flex flex-col items-center justify-center">
                <span className="text-lg font-semibold">Total</span>
                <span className="text-2xl font-bold">{total} RON</span>
            </div>
        </div>
    );
};

export default DepositChart;
