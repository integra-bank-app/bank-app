import { useState, useEffect } from "react";
import { useNavigate, Link } from "react-router-dom";
import { Button } from "primereact/button";
import { Card } from "primereact/card";
import { useAuthentication } from "../../contexts/AuthenticationProvider";
import FormInput from "../../components/FormInput";
import FormDropdown from "../../components/FormDropdown";
import { Message } from "primereact/message";

export default function RegisterPage() {
    const navigate = useNavigate();
    const { register, logout, isAuthenticated } = useAuthentication();

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
        { label: "Select a role", value: "" },
        { label: "User", value: "USER" },
        { label: "Admin", value: "ADMIN" },
    ];

    const validateForm = (): boolean => {
        const newErrors: Record<string, string> = {};

        if (!formData.firstName.trim()) {
            newErrors.firstName = "First name is required";
        }

        if (!formData.lastName.trim()) {
            newErrors.lastName = "Last name is required";
        }

        if (!formData.branchId) {
            newErrors.branchId = "Branch ID is required";
        }

        if (!formData.role) {
            newErrors.role = "Role is required";
        }

        if (!formData.email.trim()) {
            newErrors.email = "Email is required";
        } else if (!/\S+@\S+\.\S+/.test(formData.email)) {
            newErrors.email = "Email is invalid";
        }

        if (!formData.password) {
            newErrors.password = "Password is required";
        } else if (formData.password.length < 6) {
            newErrors.password = "Password must be at least 6 characters";
        }

        if (!formData.confirmPassword) {
            newErrors.confirmPassword = "Please confirm your password";
        } else if (formData.password !== formData.confirmPassword) {
            newErrors.confirmPassword = "Passwords do not match";
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
            setRegisterError("Registration failed. Email may already be in use.");
        }
    };

    return (
        <div className="flex align-items-center justify-content-center min-h-screen bg-gray-900 p-4">
            <Card className="w-full max-w-3xl shadow-lg">
                <div className="text-center mb-5">
                    <i className="pi pi-user-plus text-5xl text-primary mb-3"></i>
                    <h1 className="text-3xl font-bold mb-2">Create Account</h1>
                    <p className="text-gray-400">Join Integra Pay today</p>
                </div>

                {registerError && (
                    <Message severity="error" text={registerError} className="mb-4 w-full" />
                )}

                {registerSuccess && (
                    <Message
                        severity="success"
                        text="Registration successful! Redirecting to login..."
                        className="mb-4 w-full"
                    />
                )}

                <form onSubmit={handleSubmit}>
                    <div className="mb-5">
                        <h3 className="text-lg font-semibold mb-3 text-primary border-bottom-1 border-primary pb-2">
                            <i className="pi pi-user mr-2"></i>
                            Personal Information
                        </h3>
                        <div className="grid">
                            <div className="col-12 md:col-6">
                                <FormInput
                                    id="firstName"
                                    label="First Name"
                                    value={formData.firstName}
                                    onChange={(value) => setFormData({ ...formData, firstName: value })}
                                    error={errors.firstName}
                                    required
                                    placeholder="John"
                                />
                            </div>
                            <div className="col-12 md:col-6">
                                <FormInput
                                    id="lastName"
                                    label="Last Name"
                                    value={formData.lastName}
                                    onChange={(value) => setFormData({ ...formData, lastName: value })}
                                    error={errors.lastName}
                                    required
                                    placeholder="Doe"
                                />
                            </div>
                            <div className="col-12">
                                <FormInput
                                    id="middleName"
                                    label="Middle Name"
                                    value={formData.middleName}
                                    onChange={(value) => setFormData({ ...formData, middleName: value })}
                                    placeholder="Optional"
                                />
                            </div>
                        </div>
                    </div>

                    <div className="mb-5">
                        <h3 className="text-lg font-semibold mb-3 text-primary border-bottom-1 border-primary pb-2">
                            <i className="pi pi-building mr-2"></i>
                            Organization Information
                        </h3>
                        <div className="grid">
                            <div className="col-12 md:col-6">
                                <FormInput
                                    id="branchId"
                                    label="Branch ID"
                                    value={formData.branchId}
                                    onChange={(value) => setFormData({ ...formData, branchId: value })}
                                    error={errors.branchId}
                                    required
                                    placeholder="e246e2e6-d734-46a0-83e2-e6ee9b725977"
                                />
                            </div>
                            <div className="col-12 md:col-6">
                                <FormDropdown
                                    id="role"
                                    label="Role"
                                    value={formData.role}
                                    onChange={(value) => setFormData({ ...formData, role: value })}
                                    options={roleOptions}
                                    error={errors.role}
                                    required
                                    placeholder="Select a role"
                                />
                            </div>
                        </div>
                    </div>

                    <div className="mb-4">
                        <h3 className="text-lg font-semibold mb-3 text-primary border-bottom-1 border-primary pb-2">
                            <i className="pi pi-lock mr-2"></i>
                            Account Security
                        </h3>
                        <div className="grid">
                            <div className="col-12">
                                <FormInput
                                    id="email"
                                    label="Email"
                                    type="email"
                                    value={formData.email}
                                    onChange={(value) => setFormData({ ...formData, email: value })}
                                    error={errors.email}
                                    required
                                    placeholder="john.doe@example.com"
                                />
                            </div>
                            <div className="col-12 md:col-6">
                                <FormInput
                                    id="password"
                                    label="Password"
                                    type="password"
                                    value={formData.password}
                                    onChange={(value) => setFormData({ ...formData, password: value })}
                                    error={errors.password}
                                    required
                                    placeholder="Minimum 6 characters"
                                />
                            </div>
                            <div className="col-12 md:col-6">
                                <FormInput
                                    id="confirmPassword"
                                    label="Confirm Password"
                                    type="password"
                                    value={formData.confirmPassword}
                                    onChange={(value) => setFormData({ ...formData, confirmPassword: value })}
                                    error={errors.confirmPassword}
                                    required
                                    placeholder="Re-enter password"
                                />
                            </div>
                        </div>
                    </div>

                    <Button
                        type="submit"
                        label={loading ? "Creating Account..." : "Create Account"}
                        icon={loading ? "pi pi-spin pi-spinner" : "pi pi-user-plus"}
                        className="w-full mt-3"
                        disabled={loading || registerSuccess}
                    />
                </form>

                <div className="text-center mt-4">
                    <p className="text-sm text-gray-400">
                        Already have an account?{" "}
                        <Link to="/login" className="text-primary hover:underline font-semibold">
                            Sign in here
                        </Link>
                    </p>
                </div>
            </Card>
        </div>
    );
}