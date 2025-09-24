import React from "react";

type Deposit = {
    id: string;
    amount: number;
    interestRate: number;
}

interface DepositListProps {
    deposits: Deposit[];
}

const DepositList: React.FC<DepositListProps> = ({ deposits }) => {
    return (
        <div className="p-4">
            <h2 className="text-xl font font-semibold mb-2">Your Deposit</h2>
            <ul className="space-y-2">
                {deposits.map((deposit) => (
                    <li
                        key={deposit.id}
                        className="p-3 border rounded-lg shadow-sm flex justify-between"
                    >
                        <span>Amount: {deposit.amount} €</span>
                        <span>Rate: {deposit.interestRate}%</span>
                    </li>
                ))}
            </ul>
        </div>
    );
};

export default DepositList;