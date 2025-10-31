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

