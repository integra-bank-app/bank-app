import { Button } from "primereact/button";
import { useNavigate, useLocation } from "react-router-dom";
import { useAuthentication } from "../contexts/AuthenticationProvider";

export default function NotFoundPage() {
    const navigate = useNavigate();
    const location = useLocation();
    const { isAuthenticated, user } = useAuthentication();

    const isAccessDenied = isAuthenticated &&
        (location.pathname.includes("/admin") || location.pathname.includes("/users"));

    return (
        <div className="flex flex-col align-items-center justify-content-center min-h-screen bg-gray-900">
            <div className="text-center">
                <i className={`pi ${isAccessDenied ? 'pi-ban' : 'pi-exclamation-triangle'} text-6xl ${isAccessDenied ? 'text-red-500' : 'text-yellow-500'} mb-4`}></i>
                <h1 className="text-6xl font-bold mb-3">{isAccessDenied ? '403' : '404'}</h1>
                <h2 className="text-2xl mb-4">
                    {isAccessDenied ? 'Access Denied' : 'Page Not Found'}
                </h2>
                <p className="text-gray-400 mb-2">
                    {isAccessDenied
                        ? `You don't have permission to access this page.`
                        : `The page you are looking for doesn't exist.`
                    }
                </p>
                {isAccessDenied && (
                    <p className="text-gray-500 mb-5">
                        Your role: <span className="text-primary font-semibold">{user?.role}</span> | Required: <span className="text-red-500 font-semibold">ADMIN</span>
                    </p>
                )}
                <div className="flex gap-2 justify-content-center">
                    <Button
                        label="Go to Home"
                        icon="pi pi-home"
                        onClick={() => navigate("/home")}
                    />
                    {!isAuthenticated && (
                        <Button
                            label="Go to Login"
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