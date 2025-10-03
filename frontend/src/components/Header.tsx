import {useState} from "react";
import {useNavigate, useLocation} from "react-router-dom";
import {FaUser} from "react-icons/fa";
import {Button} from "primereact/button";
import {Dropdown} from "primereact/dropdown";
import {useTranslation} from "react-i18next";
import ConfirmationDialog from "./ConfirmationDialog";

const Header: React.FC = () => {
    const navigate = useNavigate();
    const location = useLocation();
    const [confirmVisible, setConfirmVisible] = useState(false);
    const {t, i18n} = useTranslation();

    const languages = [
        {code: "en", name: "English", flag: "🇬🇧"},
        {code: "ro", name: "Română", flag: "🇷🇴"},
    ];

    const [selectedLang, setSelectedLang] = useState(
        languages.find((l) => l.code === i18n.language) || languages[0]
    );

    const handleLanguageChange = (lang: any) => {
        setSelectedLang(lang);
        i18n.changeLanguage(lang.code);
    };

    const handleLogout = () => {
        navigate("/login");
    };

    const showLogout = location.pathname !== "/login";
    const showUserIcon = location.pathname !== "/login";

    return (
        <header className="p-2 relative flex items-center justify-between bg-gray-100 shadow-md">
            {/* Left: Language Dropdown */}
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

            {/* Center */}
            {showUserIcon && (
                <div className="absolute left-1/2 transform -translate-x-1/2">
                    <FaUser size={22}/>
                </div>
            )}

            {/* Right */}
            <div className="flex justify-end">
                {showLogout && (
                    <Button
                        className="font-semibold px-3 py-1 rounded text-sm"
                        onClick={() => setConfirmVisible(true)}
                    >
                        {t("header.logout")}
                    </Button>
                )}
            </div>

            {/* Confirmation Dialog */}
            <ConfirmationDialog
                visible={confirmVisible}
                message={t("header.confirmLogout")}
                onAccept={handleLogout}
                onReject={() => setConfirmVisible(false)}
                onHide={() => setConfirmVisible(false)}
            />
        </header>
    );
};

export default Header;
