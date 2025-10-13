import { useState, useEffect } from "react";
import { useNavigate, Link } from "react-router-dom";
import { Button } from "primereact/button";
import { Card } from "primereact/card";
import { InputText } from "primereact/inputtext";
import { Password } from "primereact/password";
import { useAuthentication } from "../../contexts/AuthenticationProvider";
import { Message } from "primereact/message";

export default function LoginPage() {
    const navigate = useNavigate();
    const { login, logout, isAuthenticated } = useAuthentication();

    const [formData, setFormData] = useState({
        email: "",
        password: "",
    });

    const [errors, setErrors] = useState<Record<string, string>>({});
    const [loading, setLoading] = useState(false);
    const [loginError, setLoginError] = useState("");

    useEffect(() => {
        logout();
    }, []);

    useEffect(() => {
        if (isAuthenticated) {
            navigate("/home");
        }
    }, [isAuthenticated, navigate]);

    const validateForm = (): boolean => {
        const newErrors: Record<string, string> = {};

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

        setErrors(newErrors);
        return Object.keys(newErrors).length === 0;
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setLoginError("");

        if (!validateForm()) {
            return;
        }

        setLoading(true);
        const success = await login(formData.email, formData.password);
        setLoading(false);

        if (success) {
            navigate("/home");
        } else {
            setLoginError("Invalid email or password. Please try again.");
        }
    };

    return (
        <div className="flex align-items-center justify-content-center min-h-screen bg-gray-900 p-4">
            <Card className="w-full max-w-md shadow-lg">
                <div className="text-center mb-5">
                    <i className="pi pi-wallet text-5xl text-primary mb-3"></i>
                    <h1 className="text-3xl font-bold mb-2">Integra Pay</h1>
                    <p className="text-gray-400">Sign in to your account</p>
                </div>

                {loginError && (
                    <Message severity="error" text={loginError} className="mb-4 w-full" />
                )}

                <form onSubmit={handleSubmit}>
                    <div className="mb-4">
                        <label htmlFor="email" className="block font-semibold text-sm mb-2">
                            Email <span className="text-red-500">*</span>
                        </label>
                        <InputText
                            id="email"
                            type="email"
                            value={formData.email}
                            onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                            placeholder="Enter your email"
                            className={errors.email ? "p-invalid w-full" : "w-full"}
                            style={{ width: '100%', display: 'block' }}
                        />
                        {errors.email && (
                            <small className="block mt-1 text-red-500">{errors.email}</small>
                        )}
                    </div>

                    <div className="mb-4">
                        <label htmlFor="password" className="block font-semibold text-sm mb-2">
                            Password <span className="text-red-500">*</span>
                        </label>
                        <div style={{ width: '100%', display: 'block' }}>
                            <Password
                                id="password"
                                value={formData.password}
                                onChange={(e) => setFormData({ ...formData, password: e.target.value })}
                                placeholder="Enter your password"
                                toggleMask
                                feedback={false}
                                className={errors.password ? "p-invalid" : ""}
                                style={{ width: '100%', display: 'block' }}
                                inputStyle={{ width: '100%' }}
                                pt={{
                                    root: {
                                        style: { width: '100%', display: 'block' }
                                    },
                                    input: {
                                        style: { width: '100%' }
                                    }
                                }}
                            />
                        </div>
                        {errors.password && (
                            <small className="block mt-1 text-red-500">{errors.password}</small>
                        )}
                    </div>

                    <Button
                        type="submit"
                        label={loading ? "Signing in..." : "Sign In"}
                        icon={loading ? "pi pi-spin pi-spinner" : "pi pi-sign-in"}
                        className="w-full mt-3"
                        disabled={loading}
                    />
                </form>

                <div className="text-center mt-4">
                    <p className="text-sm text-gray-400">
                        Don't have an account?{" "}
                        <Link to="/register" className="text-primary hover:underline font-semibold">
                            Register here
                        </Link>
                    </p>
                </div>
            </Card>
        </div>
    );
}