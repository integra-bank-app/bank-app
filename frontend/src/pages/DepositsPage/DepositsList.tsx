import React from "react";
import {depositColors} from "../../lib/utils";
 import {DepositsDTO} from "../../api";
import DepositsItem from "./DepositsItem";


interface DepositListProps {
    deposits: DepositsDTO[];
    depositColors: string[];
}

const DepositsList: React.FC<DepositListProps> = ({ deposits }) => {
    return (
        <div className="mt-6 w-full max-w-md">
            <h2 className="text-xl font-semibold mb-2">Deposit List</h2>
            <ul className="space-y-4">
                {deposits.map((deposit, index) => (
                    <DepositsItem
                        key={deposit.id ?? index}
                        id={deposit.id}
                        amount={deposit.amount}
                        color={depositColors[index % depositColors.length]}
                    />
                ))}
            </ul>
        </div>
    );
};

export default DepositsList;