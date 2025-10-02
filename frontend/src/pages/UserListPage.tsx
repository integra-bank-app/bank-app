import { useEffect, useState } from "react";
import { useUserContext } from "../lib/hooks";
import { BranchControllerApi, UserDTO } from "../api";
import { DataTable } from "primereact/datatable";
import { Column } from "primereact/column";
import { Card } from "primereact/card";
import {Title} from "../components/TitleComponent";

export default function UserListPage() {
	const { user } = useUserContext();
	const [users, setUsers] = useState<UserDTO[]>([]);
	const [loading, setLoading] = useState(true);
	const [error, setError] = useState<string | null>(null);

	useEffect(() => {
		if (!user?.branchId) return;

		const fetchUsers = async () => {
			try {
				const apiFp = new BranchControllerApi();
				const response = await apiFp.getUsersByBranch(user.branchId);
				setUsers(response.data);
			} catch (err: any) {
				console.error(err);
				setError(err.message || "Failed to fetch users");
			} finally {
				setLoading(false);
			}
		};
		fetchUsers();
	}, [user?.branchId]);

	if (loading) return <p>Loading users...</p>;
	if (error) return <p>Error: {error}</p>;

	const fullNameTemplate = (rowData: UserDTO) => {
		return `${rowData.firstName ?? ""} ${
			rowData.middleName ? `${rowData.middleName} ` : ""
		}${rowData.lastName ?? ""}`.trim();
	};

	const footer = `In total there are ${users ? users.length : 0} users.`;

	return (
		<div className="flex flex-column align-items-center p-4 gap-4">
			<div
				className="w-full flex flex-col items-center justify-center text-center mx-auto"
				style={{ maxWidth: "800px" }}
			>
				<Title>Users in Branch</Title>
				<p className="text-sm text-gray-500 mt-2">
					Branch ID:
					<span className="inline-block ml-2 px-2 py-0.5 rounded-full bg-blue-50 text-blue-700 font-medium">
						{user?.branchId ?? "—"}
					</span>
				</p>
			</div>

			<Card>
				<DataTable
					value={users}
					tableStyle={{
						minWidth: "40rem",
						maxWidth: "800px",
					}}
					footer={footer}
				>
					<Column field="firstName" header="First Name"></Column>
					<Column field="middleName" header="Middle Name"></Column>
					<Column field="lastName" header="Last Name"></Column>
					<Column header="Full Name" body={fullNameTemplate}></Column>
				</DataTable>
			</Card>
		</div>
	);
}
