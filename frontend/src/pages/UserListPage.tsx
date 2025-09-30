import { useEffect, useState } from "react";
import { useUserContext } from "../lib/hooks";
import { BranchControllerApi, UserDTO, PagedModelUserDTO } from "../api";
import { DataTable } from "primereact/datatable";
import { Column } from "primereact/column";
import { Card } from "primereact/card";
import { Paginator } from "primereact/paginator";

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
				<h2 className="text-2xl md:text-3xl font-semibold text-gray-900">
					Users in Branch
				</h2>
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
					rowsPerPageOptions={[5, 10, 20, 50, 100, 1000, 10000, 100000]}
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
