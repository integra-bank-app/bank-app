import { Button } from "primereact/button";
import { useNavigate, useLocation } from "react-router-dom";
import { useAuthentication } from "../contexts/AuthenticationProvider";
import { useTranslation } from "react-i18next";

export default function NotFoundPage() {
    const navigate = useNavigate();
    const location = useLocation();
    const { isAuthenticated, user } = useAuthentication();
    const { t } = useTranslation();

    const isAccessDenied = isAuthenticated &&
        (location.pathname.includes("/admin") || location.pathname.includes("/users"));

    return (
        <div className="flex flex-col align-items-center justify-content-center min-h-screen bg-gray-900">
            <div className="text-center">
                <i className={`pi ${isAccessDenied ? 'pi-ban' : 'pi-exclamation-triangle'} text-6xl ${isAccessDenied ? 'text-red-500' : 'text-yellow-500'} mb-4`}></i>
                <h1 className="text-6xl font-bold mb-3">{isAccessDenied ? '403' : '404'}</h1>
                <h2 className="text-2xl mb-4">
                    {isAccessDenied ? t("notFound.accessDenied") : t("notFound.pageNotFound")}
                </h2>
                <p className="text-gray-400 mb-2">
                    {isAccessDenied
                        ? t("notFound.deniedDescription")
                        : t("notFound.notFoundDescription")
                    }
                </p>
                {isAccessDenied && (
                    <p className="text-gray-500 mb-5">
                        {t("notFound.yourRole")}: <span className="text-primary font-semibold">{user?.role}</span> | {t("notFound.required")}: <span className="text-red-500 font-semibold">ADMIN</span>
                    </p>
                )}
                <div className="flex gap-2 justify-content-center">
                    <Button
                        label={t("notFound.goHome")}
                        icon="pi pi-home"
                        onClick={() => navigate("/home")}
                    />
                    {!isAuthenticated && (
                        <Button
                            label={t("notFound.goLogin")}
                            icon="pi pi-sign-in"
                            onClick={() => navigate("/login")}
                            severity="secondary"
                        />
                    )}
                </div>
            </div>
        </div>
    );
}