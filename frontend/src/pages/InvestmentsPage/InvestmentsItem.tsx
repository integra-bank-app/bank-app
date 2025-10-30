import React from "react";

interface InvestmentItemProp {
    number: number;
    risk?: number;
    balance?: number;
    color: string;
}

export const InvestmentItem: React.FC<InvestmentItemProp> = ({number, risk=0, balance=0, color }) => {
    const riskColor =
        risk <= 3 ? "text-green-400" :
            risk <= 6 ? "text-yellow-400" :
                "text-red-400";

    const riskIcon = risk >= 5 ? "pi pi-sort-up-fill" : "pi pi-sort-down-fill";
    const riskValue = `${risk >= 5 ? "+" : ""}${risk.toFixed(1)}%`;

    return (
        <li className="flex justify-between items-center bg-[#1E1E2F] px-6 py-4 rounded-lg shadow-md border border-[#2A2A3C] hover:bg-[#25253A] transition-all duration-200">
            <div className="flex flex-col">
        <span className="text-lg font-medium text-[#D9D9D9]">
          Investment {number}
        </span>
            </div>

            <div className="flex flex-col items-end">
                <span className="text-lg font-semibold text-[#D9D9D9]">
                    {balance.toFixed(2)} RON
                </span>
                <span
                    className={`flex items-center gap-1 text-sm font-medium ${riskColor}`}>
                    <i className={riskIcon}/>
                    {riskValue} (Risk {risk})
                </span>
            </div>
        </li>
);
};


