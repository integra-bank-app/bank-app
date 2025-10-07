import { useEffect, useState } from "react";
import { useUserContext } from "../lib/hooks";
import { BranchControllerApi, UserDTO, PagedModelUserDTO } from "../api";
import { DataTable } from "primereact/datatable";
import { Column } from "primereact/column";
import { Card } from "primereact/card";
import { Paginator } from "primereact/paginator";
import { USER_LIST_ROWS_PER_PAGE_OPTIONS } from "../lib/constants";
import {Title} from "../components/TitleComponent";

export default function UserListPage() {
	const { user } = useUserContext();
	const [users, setUsers] = useState<UserDTO[]>([]);
	const [loading, setLoading] = useState(true);
	const [error, setError] = useState<string | null>(null);
	const [page, setPage] = useState(0);
	const [rows, setRows] = useState(10);
	const [totalRecords, setTotalRecords] = useState(0);

	useEffect(() => {
		if (!user?.branchId) return;

		let isMounted = true;

		const fetchUsers = async () => {
			setLoading(true);
			try {
				const api = new BranchControllerApi();
				const response = await api.getUsersByBranch(user.branchId, page, rows);
				const paged: PagedModelUserDTO = (response as any).data ?? response;

				if (isMounted) {
					setUsers(paged.content ?? []);
					setTotalRecords(paged.page?.totalElements ?? 0);
					setError(null);
				}
			} catch (err: any) {
				if (isMounted) {
					console.error(err);
					setError(err.message || "Failed to fetch users");
				}
			} finally {
				if (isMounted) {
					setLoading(false);
				}
			}
		};

		fetchUsers();

		return () => {
			isMounted = false;
		};
	}, [user?.branchId, page, rows]);

	if (loading) return <p>Loading users...</p>;
	if (error) return <p>Error: {error}</p>;

	const fullNameTemplate = (rowData: UserDTO) => {
		return `${rowData.firstName ?? ""} ${
			rowData.middleName ? `${rowData.middleName} ` : ""
		}${rowData.lastName ?? ""}`.trim();
	};

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
					totalRecords={totalRecords}
					loading={loading}
					tableStyle={{
						minWidth: "40rem",
						maxWidth: "800px",
					}}
				>
					<Column field="firstName" header="First Name" />
					<Column field="middleName" header="Middle Name" />
					<Column field="lastName" header="Last Name" />
					<Column header="Full Name" body={fullNameTemplate} />
				</DataTable>
				<Paginator
					first={page * rows}
					rows={rows}
					totalRecords={totalRecords}
					rowsPerPageOptions={USER_LIST_ROWS_PER_PAGE_OPTIONS}
					onPageChange={(e) => {
						setPage(e.page);
						setRows(e.rows);
					}}
					template="FirstPageLink PrevPageLink PageLinks NextPageLink LastPageLink RowsPerPageDropdown CurrentPageReport "
				/>
			</Card>
		</div>
	);
}
