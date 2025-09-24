import { useState } from "react";
import { Button } from "primereact/button";
import { Dialog } from "primereact/dialog";
import { InputText } from "primereact/inputtext";
import UserListComponent from "./UserListComponent";

function AdminPage() {
	const [showAddUser, setShowAddUser] = useState(false);
	const [firstName, setFirstName] = useState("");
	const [lastName, setLastName] = useState("");
	const [middleName, setMiddleName] = useState("");
	const [errors, setErrors] = useState<{
		firstName?: string;
		lastName?: string;
		middleName?: string;
	}>({});

	const validate = () => {
		// Meaby in the future use zod
		const newErrors: typeof errors = {};
		if (!firstName.trim()) newErrors.firstName = "First name is required";
		if (!lastName.trim()) newErrors.lastName = "Last name is required";
		setErrors(newErrors);
		return Object.keys(newErrors).length === 0;
	};

	const handleAddUser = () => {
		if (!validate()) return;

		console.log("Saving:", { firstName, lastName, middleName });

		setFirstName("");
		setLastName("");
		setMiddleName("");
		setShowAddUser(false);
	};

	return (
		<section className="flex flex-column align-items-center p-4 gap-4">
			<h1 className="text-5xl font-bold">Hello, Admin</h1>

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

			<div className="w-full" style={{ maxWidth: "800px" }}>
				<UserListComponent />
			</div>

			<Dialog
				header="Add User"
				visible={showAddUser}
				style={{ width: "400px" }}
				modal
				onHide={() => setShowAddUser(false)}
			>
				<div className="flex flex-column gap-3">
					<div>
						<label htmlFor="firstName" className="block mb-1">
							First Name
						</label>
						<InputText
							id="firstName"
							value={firstName}
							onChange={(e) => setFirstName(e.target.value)}
							onKeyDown={(e) => e.key === "Enter" && handleAddUser()}
							className={errors.firstName ? "p-invalid w-full" : "w-full"}
						/>
						{errors.firstName && (
							<small className="p-error">{errors.firstName}</small>
						)}
					</div>
					<div>
						<label htmlFor="middleName" className="block mb-1">
							Middle Name
						</label>
						<InputText
							id="middleName"
							value={middleName}
							onChange={(e) => setMiddleName(e.target.value)}
							onKeyDown={(e) => e.key === "Enter" && handleAddUser()}
							className="w-full"
						/>
					</div>

					<div>
						<label htmlFor="lastName" className="block mb-1">
							Last Name
						</label>
						<InputText
							id="lastName"
							value={lastName}
							onChange={(e) => setLastName(e.target.value)}
							onKeyDown={(e) => e.key === "Enter" && handleAddUser()}
							className={errors.lastName ? "p-invalid w-full" : "w-full"}
						/>
						{errors.lastName && (
							<small className="p-error">{errors.lastName}</small>
						)}
					</div>

					<Button label="Add User" icon="pi pi-check" onClick={handleAddUser} />
				</div>
			</Dialog>
		</section>
	);
}

export default AdminPage;
