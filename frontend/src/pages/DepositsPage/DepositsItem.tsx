import React from "react";

interface DepositsItemProp {
    id?: string;
    amount?: number;
    color: string;
}

const DepositsItem: React.FC<DepositsItemProp> = ({id, amount, color}) => {
    return (
        <li
            className="flex justify-between items-center p-3 rounded-lg shadow-sm border-2"
            style={{borderColor: color}}
        >
            <div className="flex flex-col">
                <span className="font-semibold text-lg">
                    Deposit {id ?? "?"}
                </span>
                <span className="text-base font-normal">
                    {amount ?? 0} RON
                </span>
            </div>
        </li>
    );
};

export default DepositsItem;
