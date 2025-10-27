import React from "react";
import { depositColors } from "../../lib/utils";
import { DepositsDTO } from "../../api";
import DepositsItem from "./DepositsItem";
import { useTranslation } from "react-i18next";

interface DepositListProps {
	deposits: DepositsDTO[];
	depositColors: string[];
}

const DepositsList: React.FC<DepositListProps> = ({ deposits }) => {
	const { t } = useTranslation();
	return (
		<div className="mt-6 w-full max-w-md">
			<h2 className="text-xl font-semibold mb-2">
				{t("depositsPage.list.title")}
			</h2>
			<ul className="space-y-4">
				{deposits.map((deposit, index) => (
					<DepositsItem
						key={deposit.id ?? index}
						number={index + 1}
						amount={deposit.amount}
						color={depositColors[index % depositColors.length]}
					/>
				))}
			</ul>
		</div>
	);
};

export default DepositsList;
