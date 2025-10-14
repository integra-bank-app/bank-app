import React, { useState } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import { Button } from "primereact/button";
import { useAuthentication } from '../contexts/AuthenticationProvider';
import ConfirmationDialog from "./ConfirmationDialog";
import { useTranslation } from "react-i18next";
import { Dropdown } from "primereact/dropdown";

const Header: React.FC = () => {
	const navigate = useNavigate();
	const location = useLocation();
	const { isAuthenticated, user, logout } = useAuthentication();
	const [confirmVisible, setConfirmVisible] = useState(false);
	const { t, i18n } = useTranslation();

	const languages = [
		{ code: "en", name: "English", flag: "🇬🇧" },
		{ code: "ro", name: "Română", flag: "🇷🇴" },
	];
	const [selectedLang, setSelectedLang] = useState(
		languages.find((l) => l.code === i18n.language) || languages[0]
	);

	const handleLanguageChange = (lang: any) => {
		setSelectedLang(lang);
		i18n.changeLanguage(lang.code);
	};

	const handleLogout = () => {
		logout();
		navigate("/login");
		setConfirmVisible(false);
	};

	const showLogout = isAuthenticated && location.pathname !== "/login";
	const showUserInfo = isAuthenticated && location.pathname !== "/login";

	return (
		<header className="bg-primary text-white p-3 shadow-lg">
			<div className="flex justify-content-between align-items-center">

				<div className="flex align-items-center">
					<i className="pi pi-wallet text-2xl mr-2"></i>
					<h1 className="text-xl font-bold m-0">{t("header.title")}</h1>
				</div>

				{/* Language dropdown */}
				<div>
					<Dropdown
						value={selectedLang}
						options={languages}
						onChange={(e) => handleLanguageChange(e.value)}
						optionLabel="name"
						valueTemplate={(option) => (
							<span>{option.flag} {option.name}</span>
						)}
						itemTemplate={(option) => (
							<div className="flex items-center gap-2">
								<span>{option.flag}</span>
								<span>{option.name}</span>
							</div>
						)}
						className="w-36"
					/>
				</div>

				{showUserInfo && (
					<div className="flex align-items-center gap-4">
						<div className="flex gap-2">
							<Button
								label={t("header.home")}
								icon="pi pi-home"
								className="p-button-text p-button-sm text-white"
								onClick={() => navigate('/home')}
							/>
							<Button
								label={t("header.deposits")}
								icon="pi pi-chart-line"
								className="p-button-text p-button-sm text-white"
								onClick={() => navigate('/deposits')}
							/>
							{user?.role === 'ADMIN' && (
								<>
									<Button
										label={t("header.admin")}
										icon="pi pi-cog"
										className="p-button-text p-button-sm text-white"
										onClick={() => navigate('/admin')}
									/>
									<Button
										label={t("header.users")}
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
						label={t("header.logout")}
						icon="pi pi-sign-out"
						className="p-button-sm"
						onClick={() => setConfirmVisible(true)}
					/>
				)}
			</div>

			<ConfirmationDialog
				visible={confirmVisible}
				message={t("header.logoutConfirm")}
				onAccept={handleLogout}
				onReject={() => setConfirmVisible(false)}
				onHide={() => setConfirmVisible(false)}
			/>
		</header>
	);
};

export default Header;