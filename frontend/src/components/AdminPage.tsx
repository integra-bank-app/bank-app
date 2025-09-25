import { useState } from "react";
import { Button } from "primereact/button";
import { useNavigate } from "react-router-dom";
import { useUserContext } from "../lib/hooks";
import AddUserToBranchDialog from "./AddUserToBranchDialog";

function AdminPage() {
	const { user } = useUserContext();
	const [showAddUser, setShowAddUser] = useState(false);
	const navigate = useNavigate();

	return (
		<section className="flex flex-column align-items-center p-4 gap-4">
			<h1 className="text-5xl font-bold">Hello, {user.firstName}</h1>

			<div className="grid w-full" style={{ maxWidth: "600px" }}>
				<div className="col-12 md:col-4">
					<Button
						label="Add User"
						onClick={() => setShowAddUser(true)}
						className="w-full"
					/>
				</div>
				<div className="col-12 md:col-4">
					<Button label="Export Users" className="w-full" />
				</div>
				<div className="col-12 md:col-4">
					<Button label="Settings" className="w-full" />
				</div>
			</div>

			<div className="w-full" style={{ maxWidth: "800px" }}></div>

			<AddUserToBranchDialog
				branchId={user.branchId}
				visible={showAddUser}
				onHide={() => setShowAddUser(false)}
				onUserAdded={() => navigate("/users")}
			/>
		</section>
	);
}

export default AdminPage;
