import { useState, useEffect } from "react";
import { useNavigate, Link } from "react-router-dom";
import { Button } from "primereact/button";
import { Card } from "primereact/card";
import { useAuthentication } from "../../contexts/AuthenticationProvider";
import FormInput from "../../components/FormInput";
import FormDropdown from "../../components/FormDropdown";
import { Message } from "primereact/message";
import { useTranslation } from "react-i18next";

export default function RegisterPage() {
    const navigate = useNavigate();
    const { register, logout, isAuthenticated } = useAuthentication();
    const { t } = useTranslation();

    const [formData, setFormData] = useState({
        firstName: "",
        lastName: "",
        middleName: "",
        branchId: "",
        email: "",
        password: "",
        confirmPassword: "",
        role: "",
    });

    const [errors, setErrors] = useState<Record<string, string>>({});
    const [loading, setLoading] = useState(false);
    const [registerError, setRegisterError] = useState("");
    const [registerSuccess, setRegisterSuccess] = useState(false);

    useEffect(() => {
        logout();
    }, []);

    useEffect(() => {
        if (isAuthenticated) {
            navigate("/home");
        }
    }, [isAuthenticated, navigate]);

    const roleOptions = [
        { label: t("register.roleSelect"), value: "" },
        { label: t("register.roleUser"), value: "USER" },
        { label: t("register.roleAdmin"), value: "ADMIN" },
    ];

    const validateForm = (): boolean => {
        const newErrors: Record<string, string> = {};

        if (!formData.firstName.trim()) {
            newErrors.firstName = t("register.errors.firstNameRequired");
        }

        if (!formData.lastName.trim()) {
            newErrors.lastName = t("register.errors.lastNameRequired");
        }

        if (!formData.branchId) {
            newErrors.branchId = t("register.errors.branchIdRequired");
        }

        if (!formData.role) {
            newErrors.role = t("register.errors.roleRequired");
        }

        if (!formData.email.trim()) {
            newErrors.email = t("register.errors.emailRequired");
        } else if (!/\S+@\S+\.\S+/.test(formData.email)) {
            newErrors.email = t("register.errors.emailInvalid");
        }

        if (!formData.password) {
            newErrors.password = t("register.errors.passwordRequired");
        } else if (formData.password.length < 6) {
            newErrors.password = t("register.errors.passwordTooShort");
        }

        if (!formData.confirmPassword) {
            newErrors.confirmPassword = t("register.errors.confirmPasswordRequired");
        } else if (formData.password !== formData.confirmPassword) {
            newErrors.confirmPassword = t("register.errors.passwordMismatch");
        }

        setErrors(newErrors);
        return Object.keys(newErrors).length === 0;
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setRegisterError("");
        setRegisterSuccess(false);

        if (!validateForm()) {
            return;
        }

        setLoading(true);
        const success = await register({
            firstName: formData.firstName,
            lastName: formData.lastName,
            middleName: formData.middleName || undefined,
            branchId: formData.branchId,
            email: formData.email,
            password: formData.password,
            role: formData.role,
        });
        setLoading(false);

        if (success) {
            setRegisterSuccess(true);
            setTimeout(() => {
                navigate("/login");
            }, 2000);
        } else {
            setRegisterError(t("register.errors.registrationFailed"));
        }
    };

    return (
        <div className="flex align-items-center justify-content-center min-h-screen bg-gray-900 p-4">
            <Card className="w-full max-w-3xl shadow-lg">
                <div className="text-center mb-5">
                    <i className="pi pi-user-plus text-5xl text-primary mb-3"></i>
                    <h1 className="text-3xl font-bold mb-2">{t("register.title")}</h1>
                    <p className="text-gray-400">{t("register.subtitle")}</p>
                </div>

                {registerError && (
                    <Message severity="error" text={registerError} className="mb-4 w-full" />
                )}

                {registerSuccess && (
                    <Message
                        severity="success"
                        text={t("register.success")}
                        className="mb-4 w-full"
                    />
                )}

                <form onSubmit={handleSubmit}>
                    <div className="mb-5">
                        <h3 className="text-lg font-semibold mb-3 text-primary border-bottom-1 border-primary pb-2">
                            <i className="pi pi-user mr-2"></i>
                            {t("register.personalInfo")}
                        </h3>
                        <div className="grid">
                            <div className="col-12 md:col-6">
                                <FormInput
                                    id="firstName"
                                    label={t("register.firstName")}
                                    value={formData.firstName}
                                    onChange={(value) => setFormData({ ...formData, firstName: value })}
                                    error={errors.firstName}
                                    required
                                    placeholder={t("register.firstNamePlaceholder")}
                                />
                            </div>
                            <div className="col-12 md:col-6">
                                <FormInput
                                    id="lastName"
                                    label={t("register.lastName")}
                                    value={formData.lastName}
                                    onChange={(value) => setFormData({ ...formData, lastName: value })}
                                    error={errors.lastName}
                                    required
                                    placeholder={t("register.lastNamePlaceholder")}
                                />
                            </div>
                            <div className="col-12">
                                <FormInput
                                    id="middleName"
                                    label={t("register.middleName")}
                                    value={formData.middleName}
                                    onChange={(value) => setFormData({ ...formData, middleName: value })}
                                    placeholder={t("register.middleNamePlaceholder")}
                                />
                            </div>
                        </div>
                    </div>

                    <div className="mb-5">
                        <h3 className="text-lg font-semibold mb-3 text-primary border-bottom-1 border-primary pb-2">
                            <i className="pi pi-building mr-2"></i>
                            {t("register.organizationInfo")}
                        </h3>
                        <div className="grid">
                            <div className="col-12 md:col-6">
                                <FormInput
                                    id="branchId"
                                    label={t("register.branchId")}
                                    value={formData.branchId}
                                    onChange={(value) => setFormData({ ...formData, branchId: value })}
                                    error={errors.branchId}
                                    required
                                    placeholder={t("register.branchIdPlaceholder")}
                                />
                            </div>
                            <div className="col-12 md:col-6">
                                <FormDropdown
                                    id="role"
                                    label={t("register.role")}
                                    value={formData.role}
                                    onChange={(value) => setFormData({ ...formData, role: value })}
                                    options={roleOptions}
                                    error={errors.role}
                                    required
                                    placeholder={t("register.roleSelect")}
                                />
                            </div>
                        </div>
                    </div>

                    <div className="mb-4">
                        <h3 className="text-lg font-semibold mb-3 text-primary border-bottom-1 border-primary pb-2">
                            <i className="pi pi-lock mr-2"></i>
                            {t("register.accountSecurity")}
                        </h3>
                        <div className="grid">
                            <div className="col-12">
                                <FormInput
                                    id="email"
                                    label={t("register.email")}
                                    type="email"
                                    value={formData.email}
                                    onChange={(value) => setFormData({ ...formData, email: value })}
                                    error={errors.email}
                                    required
                                    placeholder={t("register.emailPlaceholder")}
                                />
                            </div>
                            <div className="col-12 md:col-6">
                                <FormInput
                                    id="password"
                                    label={t("register.password")}
                                    type="password"
                                    value={formData.password}
                                    onChange={(value) => setFormData({ ...formData, password: value })}
                                    error={errors.password}
                                    required
                                    placeholder={t("register.passwordPlaceholder")}
                                />
                            </div>
                            <div className="col-12 md:col-6">
                                <FormInput
                                    id="confirmPassword"
                                    label={t("register.confirmPassword")}
                                    type="password"
                                    value={formData.confirmPassword}
                                    onChange={(value) => setFormData({ ...formData, confirmPassword: value })}
                                    error={errors.confirmPassword}
                                    required
                                    placeholder={t("register.confirmPasswordPlaceholder")}
                                />
                            </div>
                        </div>
                    </div>

                    <Button
                        type="submit"
                        label={loading ? t("register.creatingAccount") : t("register.createAccount")}
                        icon={loading ? "pi pi-spin pi-spinner" : "pi pi-user-plus"}
                        className="w-full mt-3"
                        disabled={loading || registerSuccess}
                    />
                </form>

                <div className="text-center mt-4">
                    <p className="text-sm text-gray-400">
                        {t("register.hasAccount")}{" "}
                        <Link to="/login" className="text-primary hover:underline font-semibold">
                            {t("register.signInHere")}
                        </Link>
                    </p>
                </div>
            </Card>
        </div>
    );
}