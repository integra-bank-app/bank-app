export type TNotification = {
	type: "SUCCESS" | "INFO" | "WARN" | "ERROR" | "SECONDARY" | "CONTRAST";
	message: string;
};

export type TUserRole = "ADMIN" | "USER";

export type TUser = {
	uuid: string;
	branchId: string;
	role: TUserRole;
	firstName: string;
	middleName?: string;
	lastName: string;
};

// export const RISK_OPTIONS = [
// 	{ nameKey: "createInvestmentDialog.lowrisk", value: 1, color: "text-green-400" },
// 	{ nameKey: "createInvestmentDialog.mediumrisk", value: 2, color: "text-yellow-400" },
// 	{ nameKey: "createInvestmentDialog.highrisk", value: 3, color: "text-red-400" },
// ];

