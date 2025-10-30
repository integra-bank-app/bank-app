import React from "react";
import {depositColors} from "../../lib/utils";
import {InvestmentDTO} from "../../api";
import {InvestmentItem} from "./InvestmentsItem";
import {useTranslation} from "react-i18next";

interface InvestmentListProps {
    investments: InvestmentDTO[];
    depositColors: string[];
}

export const InvestmentList: React.FC<InvestmentListProps> = ({investments}) => {
    const {t} = useTranslation();
    return (
        <div className="mt-8 w-full max-w-md">
            <h2 className="text-xl font-semibold mb-4 text-[#D9D9D9]">
                {t("investmentsPage.investmentsList")}
            </h2>
            <ul className="space-y-4">
                {investments.map((investment, index) => (
                    <InvestmentItem
                        key={investment.id ?? index}
                        number={index + 1}
                        balance={investment.balance ?? 0}
                        risk={investment.risk}
                        color={depositColors[index % depositColors.length]}
                    />
                ))}
            </ul>
        </div>
    );
};

