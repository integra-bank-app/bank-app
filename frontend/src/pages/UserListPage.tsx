import { useEffect, useState } from "react";
import { useAuthentication } from "../contexts/AuthenticationProvider";
import { UserDTO, PagedModelUserDTO } from "../api";
import { DataTable } from "primereact/datatable";
import { Column } from "primereact/column";
import { Paginator } from "primereact/paginator";
import { USER_LIST_ROWS_PER_PAGE_OPTIONS } from "../lib/constants";
import { Title } from "../components/TitleComponent";
import { useTranslation } from "react-i18next";

export default function UserListPage() {
	const { user, isAuthenticated } = useAuthentication();
	const [users, setUsers] = useState<UserDTO[]>([]);
	const [loading, setLoading] = useState(true);
	const [error, setError] = useState<string | null>(null);
	const [page, setPage] = useState(0);
	const [rows, setRows] = useState(10);
	const [totalRecords, setTotalRecords] = useState(0);
	const { t } = useTranslation();

	useEffect(() => {
		if (!isAuthenticated || !user?.branchId) {
			setLoading(false);
			return;
		}

		let isMounted = true;

		const fetchUsers = async () => {
			if (!user?.id) {
				setLoading(false);
				return;
			}

			setLoading(true);

			try {
				const token = localStorage.getItem('authToken');
				if (!token) {
					throw new Error('No auth token found');
				}

				const response = await fetch(
					`http://localhost:8080/api/branches/${user.branchId}/users?page=${page}&size=${rows}`,
					{
						headers: {
							'Authorization': `Bearer ${token}`,
							'Content-Type': 'application/json'
						}
					}
				);

				if (!response.ok) {
					throw new Error('Failed to fetch users');
				}

				const paged: PagedModelUserDTO = await response.json();

				if (isMounted) {
					setUsers(paged.content ?? []);
					setTotalRecords(paged.page?.totalElements ?? 0);
					setError(null);
				}
			} catch (err: any) {
				if (isMounted) {
					console.error(err);
					setError(err.message || t("users.errorFetch"));
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
	}, [isAuthenticated, user?.branchId, page, rows, t]);

	if (loading) return (
		<div className="flex justify-content-center align-items-center" style={{ minHeight: '30vh' }}>
			<div className="text-center">
				<i className="pi pi-spinner pi-spin text-4xl text-primary mb-3"></i>
				<h3 className="text-lg font-semibold mb-2">{t("users.loading")}</h3>
			</div>
		</div>
	);

	if (error) return (
		<div className="flex justify-content-center align-items-center" style={{ minHeight: '30vh' }}>
			<div className="text-center">
				<i className="pi pi-times-circle text-4xl text-danger mb-3"></i>
				<h3 className="text-lg font-semibold mb-2">{t("users.error")}: {error}</h3>
			</div>
		</div>
	);

	const fullNameTemplate = (rowData: UserDTO) => {
		return `${rowData.firstName ?? ""} ${
			rowData.middleName ? `${rowData.middleName} ` : ""
		}${rowData.lastName ?? ""}`.trim();
	};

	const indexBodyTemplate = (_: any, { rowIndex }: { rowIndex: number }) => {
		return <span style={{ fontWeight: "bold" }}>{page * rows + rowIndex + 1}</span>;
	};

	return (
		<div className="flex flex-col items-center justify-center min-h-screen p-4" style={{ background: "#232a32" }}>
			<div className="w-full flex flex-col items-center justify-center text-center mx-auto mb-8" style={{ maxWidth: "800px" }}>
				<Title>
					<span style={{ fontSize: "2.5rem", fontWeight: 700 }}>{t("users.title")}</span>
				</Title>
				<p className="text-sm text-gray-400 mt-2">
					{t("users.branchId")}:
					<span className="inline-block ml-2 px-2 py-0.5 rounded-full bg-blue-50 text-blue-700 font-medium">
                        {user?.branchId ?? "—"}
                    </span>
				</p>
			</div>
			<div className="w-full flex flex-col items-center justify-center" style={{ maxWidth: 900 }}>
				<DataTable
					value={users}
					totalRecords={totalRecords}
					loading={loading}
					tableStyle={{
						minWidth: "100%",
						background: "transparent"
					}}
					className="w-full"
					showGridlines
					stripedRows
					responsiveLayout="scroll"
				>
					<Column
						header="#"
						body={indexBodyTemplate}
						style={{ width: "3.5rem", textAlign: "center" }}
						bodyClassName="text-center font-bold"
						headerStyle={{ textAlign: "center", justifyContent: "center", display: "table-cell" }}
					/>
					<Column field="firstName" header={t("users.firstName")}
							style={{ minWidth: 120 }}
							bodyClassName="text-center"
							headerStyle={{ textAlign: "center", justifyContent: "center", display: "table-cell" }}
					/>
					<Column field="middleName" header={t("users.middleName")}
							style={{ minWidth: 120 }}
							bodyClassName="text-center"
							headerStyle={{ textAlign: "center", justifyContent: "center", display: "table-cell" }}
					/>
					<Column field="lastName" header={t("users.lastName")}
							style={{ minWidth: 120 }}
							bodyClassName="text-center"
							headerStyle={{ textAlign: "center", justifyContent: "center", display: "table-cell" }}
					/>
					<Column header={t("users.fullName")} body={fullNameTemplate}
							style={{ minWidth: 160 }}
							bodyClassName="text-center"
							headerStyle={{ textAlign: "center", justifyContent: "center", display: "table-cell" }}
					/>
				</DataTable>
				<div className="flex justify-center mt-6">
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
				</div>
			</div>
		</div>
	);
}