import React, { useState } from "react";
import { useAuthentication } from "../contexts/AuthenticationProvider";
import { useTranslation } from "react-i18next";
import { Dropdown } from "primereact/dropdown";

const Header: React.FC = () => {
	const { isAuthenticated } = useAuthentication();
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

	return (
		<header className="bg-primary text-white p-3 shadow-lg">
			<div className="grid">
				<div className="col-4"></div>
				<div className="flex align-items-center justify-content-center col-4">
					<i className="pi pi-wallet text-2xl mr-2"></i>
					<h1 className="text-xl font-bold m-0">{t("header.title")}</h1>
				</div>

				{/* Language dropdown */}
				<div className="col-4 flex justify-content-end">
					{!isAuthenticated && (
						<div>
							<Dropdown
								value={selectedLang}
								options={languages}
								onChange={(e) => handleLanguageChange(e.value)}
								optionLabel="name"
								valueTemplate={(option) => (
									<span>
										{option.flag} {option.name}
									</span>
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
					)}
				</div>
			</div>
		</header>
	);
};

export default Header;
