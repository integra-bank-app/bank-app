import React from "react";
import {FaGithub} from "react-icons/fa";
import {useTranslation} from "react-i18next";


const Footer: React.FC = () => {
    const {t} = useTranslation();
    return (
        <footer className="p-4 pb-2 flex flex-col items-center justify-center">
            <p className="text-xs">{t("footer.copyright")}</p>
            <a
                href="https://github.com/integra-bank-app/bank-app"
                target="_blank"
                rel="noopener noreferrer"
                className="mt-2"
            >
                <FaGithub size={20}/>
            </a>
        </footer>
    );
};

export default Footer;
