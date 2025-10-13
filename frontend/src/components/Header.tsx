import React, { useState } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import { Button } from "primereact/button";
import { MenuItem } from "primereact/menuitem";
import { useAuthentication } from '../contexts/AuthenticationProvider';
import ConfirmationDialog from "./ConfirmationDialog";

const Header: React.FC = () => {
	const navigate = useNavigate();
	const location = useLocation();
	const { isAuthenticated, user, logout } = useAuthentication();
	const [confirmVisible, setConfirmVisible] = useState(false);

	const handleLogout = () => {
		logout();
		navigate("/login");
		setConfirmVisible(false);
	};

	const showLogout = isAuthenticated && location.pathname !== "/login";
	const showUserInfo = isAuthenticated && location.pathname !== "/login";

	const menuItems: MenuItem[] = [
		{
			label: 'Home',
			icon: 'pi pi-home',
			command: () => navigate('/home')
		},
		{
			label: 'Deposits',
			icon: 'pi pi-chart-line',
			command: () => navigate('/deposits')
		}
	];

	if (user?.role === 'ADMIN') {
		menuItems.push(
			{
				separator: true
			},
			{
				label: 'Admin Panel',
				icon: 'pi pi-cog',
				command: () => navigate('/admin')
			},
			{
				label: 'Users',
				icon: 'pi pi-users',
				command: () => navigate('/users')
			}
		);
	}

	return (
		<header className="bg-primary text-white p-3 shadow-lg">
			<div className="flex justify-content-between align-items-center">

				<div className="flex align-items-center">
					<i className="pi pi-wallet text-2xl mr-2"></i>
					<h1 className="text-xl font-bold m-0">Integra Pay</h1>
				</div>

				{showUserInfo && (
					<div className="flex align-items-center gap-4">

						<div className="flex gap-2">
							<Button
								label="Home"
								icon="pi pi-home"
								className="p-button-text p-button-sm text-white"
								onClick={() => navigate('/home')}
							/>
							<Button
								label="Deposits"
								icon="pi pi-chart-line"
								className="p-button-text p-button-sm text-white"
								onClick={() => navigate('/deposits')}
							/>
							{user?.role === 'ADMIN' && (
								<>
									<Button
										label="Admin"
										icon="pi pi-cog"
										className="p-button-text p-button-sm text-white"
										onClick={() => navigate('/admin')}
									/>
									<Button
										label="Users"
										icon="pi pi-users"
										className="p-button-text p-button-sm text-white"
										onClick={() => navigate('/users')}
									/>
								</>
							)}
						</div>
					</div>
				)}

				{showLogout && (
					<Button
						label="Logout"
						icon="pi pi-sign-out"
						className="p-button-sm"
						onClick={() => setConfirmVisible(true)}
					/>
				)}
			</div>

			<ConfirmationDialog
				visible={confirmVisible}
				message="Are you sure you want to log out?"
				onAccept={handleLogout}
				onReject={() => setConfirmVisible(false)}
				onHide={() => setConfirmVisible(false)}
			/>
		</header>
	);
};

export default Header;