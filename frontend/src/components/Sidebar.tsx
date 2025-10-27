import React, { useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import { useAuthentication } from "../contexts/AuthenticationProvider";
import { useTranslation } from "react-i18next";
import { Button } from "primereact/button";
import ConfirmationDialog from "./ConfirmationDialog";
import { Dropdown } from "primereact/dropdown";

const Sidebar: React.FC = () => {
	const { user, logout } = useAuthentication();
	const { t, i18n } = useTranslation();
	const navigate = useNavigate();
	const location = useLocation();
	const [confirmVisible, setConfirmVisible] = useState(false);
	const [collapsed, setCollapsed] = useState(false);

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

	const getButtonClass = (path: string) => {
		return location.pathname === path
			? "p-button"
			: "p-button-outlined p-button-faint-gray";
	};

	return (
		<div
			className={`h-screen bg-gray-800 text-white p-4 flex flex-col shadow-2xl transition-width duration-300 ${
				collapsed ? "w-20" : "w-64"
			}`}
		>
			<div
				className={`flex ${
					collapsed ? "justify-content-center" : "justify-content-start"
				}`}
			>
				<Button
					icon={collapsed ? "pi pi-bars" : "pi pi-arrow-left"}
					onClick={() => setCollapsed(!collapsed)}
					className="p-button-text text-white mb-4"
				/>
			</div>
			<nav className="flex-grow">
				<ul className={`flex flex-col gap-2 ${collapsed && "items-center"}`}>
					<li>
						<Button
							icon="pi pi-home"
							label={collapsed ? "" : t("header.home")}
							className={`w-full ${
								collapsed
									? "h-3rem w-3rem justify-content-center"
									: "justify-content-start"
							} ${getButtonClass("/home")}`}
							onClick={() => navigate("/home")}
						/>
					</li>
					<li>
						<Button
							icon="pi pi-chart-line"
							label={collapsed ? "" : t("header.deposits")}
							className={`w-full ${
								collapsed
									? "h-3rem w-3rem justify-content-center"
									: "justify-content-start"
							} ${getButtonClass("/deposits")}`}
							onClick={() => navigate("/deposits")}
						/>
					</li>
					{user?.role === "ADMIN" && (
						<>
							<li>
								<Button
									icon="pi pi-cog"
									label={collapsed ? "" : t("header.admin")}
									className={`w-full ${
										collapsed
											? "h-3rem w-3rem justify-content-center"
											: "justify-content-start"
									} ${getButtonClass("/admin")}`}
									onClick={() => navigate("/admin")}
								/>
							</li>
							<li>
								<Button
									icon="pi pi-users"
									label={collapsed ? "" : t("header.users")}
									className={`w-full ${
										collapsed
											? "h-3rem w-3rem justify-content-center"
											: "justify-content-start"
									} ${getButtonClass("/users")}`}
									onClick={() => navigate("/users")}
								/>
							</li>
						</>
					)}
				</ul>
			</nav>
			<div className="mt-auto">
				<ul className={`flex flex-col gap-2 ${collapsed && "items-center"}`}>
					<li>
						<Dropdown
							value={selectedLang}
							options={languages}
							onChange={(e) => handleLanguageChange(e.value)}
							valueTemplate={(option) => (
								<span className="flex items-center justify-content-center">
									{collapsed ? option.flag : `${option.flag} ${option.name}`}
								</span>
							)}
							itemTemplate={(option) => (
								<div className="flex items-center gap-2">
									<span>{option.flag}</span>
									<span>{option.name}</span>
								</div>
							)}
							className={`w-full ${
								collapsed ? "h-3rem w-3rem justify-content-center" : ""
							}`}
						/>
					</li>
					<li>
						<Button
							icon="pi pi-sign-out"
							label={collapsed ? "" : t("header.logout")}
							className={`w-full ${
								collapsed
									? "h-3rem w-3rem justify-content-center"
									: "justify-content-start"
							}`}
							onClick={() => setConfirmVisible(true)}
						/>
					</li>
				</ul>
			</div>
			<ConfirmationDialog
				visible={confirmVisible}
				message={t("header.logoutConfirm")}
				onAccept={handleLogout}
				onReject={() => setConfirmVisible(false)}
				onHide={() => setConfirmVisible(false)}
			/>
		</div>
	);
};

export default Sidebar;
