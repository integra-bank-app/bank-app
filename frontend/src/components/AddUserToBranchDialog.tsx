import React, {useState, useEffect} from "react";
import {Dialog} from "primereact/dialog";
import {InputText} from "primereact/inputtext";
import {Button} from "primereact/button";
import {UserWithBranchDTO, UserControllerApi} from "../api";
import {useTranslation} from "react-i18next";
import {Dropdown} from "primereact/dropdown";

type AddUserDialogProps = {
    visible: boolean;
    onHide: () => void;
    onUserAdded: () => void;
    branchId: string;
};

function generatePassword(length = 10) {
    const chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";
    let password = "";
    for (let i = 0; i < length; i++) {
        password += chars.charAt(
            Math.floor(
                window.crypto.getRandomValues(new Uint32Array(1))[0] /
                (0xffffffff + 1) *
                chars.length
            )
        );
    }
    return password;
}

const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

export default function AddUserToBranchDialog({
                                                  visible,
                                                  onHide,
                                                  onUserAdded,
                                                  branchId,
                                              }: AddUserDialogProps) {
    const {t} = useTranslation();
    const [firstName, setFirstName] = useState("");
    const [lastName, setLastName] = useState("");
    const [middleName, setMiddleName] = useState("");
    const [email, setEmail] = useState("");
    const [role, setRole] = useState<"USER" | "ADMIN">("USER");
    const [password, setPassword] = useState("");
    const [showPassword, setShowPassword] = useState(false);
    const [errors, setErrors] = useState<{
        firstName?: string;
        lastName?: string;
        email?: string;
        password?: string;
        role?: string;
    }>({});
    const [loading, setLoading] = useState(false);
    const [passwordCopied, setPasswordCopied] = useState(false);

    useEffect(() => {
        if (visible) {
            const genPass = generatePassword();
            setPassword(genPass);
            setShowPassword(false);
            setPasswordCopied(false);
            setFirstName("");
            setMiddleName("");
            setLastName("");
            setEmail("");
            setRole("USER");
            setErrors({});
        }
    }, [visible]);

    const isAdminAllowed = email.trim().toLowerCase().endsWith("@integrabank.com");

    const validate = () => {
        const newErrors: typeof errors = {};
        if (!firstName.trim())
            newErrors.firstName = t("addUserToBranchDialog.errorFirstName");
        else if(firstName.trim().length < 3)
            newErrors.firstName = t("addUserToBranchDialog.invalidFirstNameLength");

        if (!lastName.trim())
            newErrors.lastName = t("addUserToBranchDialog.errorLastName");
        else if (lastName.trim().length < 3)
            newErrors.lastName = t("addUserToBranchDialog.invalidLastNameLength");

        if (!email.trim())
            newErrors.email = t("addUserToBranchDialog.errorEmail");
        else if (email.trim().length <= 3)
            newErrors.email = t("addUserToBranchDialog.invalidEmailLength");
        else if (!emailRegex.test(email))
            newErrors.email = t("addUserToBranchDialog.invalidEmail");

        if (!password.trim())
            newErrors.password = t("addUserToBranchDialog.errorPassword");
        else if (password.trim().length < 6)
            newErrors.password = t("addUserToBranchDialog.invalidPasswordLength");

        if (role === "ADMIN" && !isAdminAllowed)
            newErrors.role = t("addUserToBranchDialog.adminEmailRestriction");

        setErrors(newErrors);
        return Object.keys(newErrors).length === 0;
    };

    useEffect(() => {
        if (role === "ADMIN" && !isAdminAllowed) {
            setRole("USER");
        }
    }, [email, role, isAdminAllowed]);

    const handleAddUser = async () => {
        if (!validate()) return;

        setLoading(true);

        try {
            const apiFp = new UserControllerApi();

            let finalRole: "USER" | "ADMIN" = role;
            if (email.trim().toLowerCase().endsWith("@integrabank.com")) {
                finalRole = "ADMIN";
            }

            const dto: UserWithBranchDTO = {
                firstName,
                middleName,
                lastName,
                branchId,
                email,
                password,
                role: finalRole,
            };
            const token = localStorage.getItem('authToken');
            const response = await apiFp.addUser(dto, {
                headers: {
                    Authorization: `Bearer ${token}`
                }
            });

            setFirstName("");
            setLastName("");
            setMiddleName("");
            setEmail("");
            setRole("USER");
            setPassword(generatePassword());
            setErrors({});
            onHide();
            onUserAdded();
        } catch (err: any) {
            console.error(t("adUserToBranchDialog.errorAddingUser"), err);
            alert(err.message || t("adUserToBranchDialog.failedToAddUser"));
        } finally {
            setLoading(false);
        }
    };

    const handleCopyPassword = () => {
        navigator.clipboard.writeText(password);
        setPasswordCopied(true);
        setTimeout(() => setPasswordCopied(false), 2000);
    };

    return (
        <Dialog
            header={t("addUserToBranchDialog.addUserHeader")}
            visible={visible}
            style={{width: "400px"}}
            modal
            onHide={onHide}
        >
            <div className="flex flex-column gap-3">
                <div>
                    <label htmlFor="firstName" className="block mb-1">
                        {t("addUserToBranchDialog.firstNameLabel")}
                    </label>
                    <InputText
                        id="firstName"
                        value={firstName}
                        onChange={(e) => setFirstName(e.target.value)}
                        onKeyDown={(e) => e.key === "Enter" && handleAddUser()}
                        className={errors.firstName ? "p-invalid w-full" : "w-full"}
                    />
                    {errors.firstName && (
                        <small className="p-error">{errors.firstName}</small>
                    )}
                </div>

                <div>
                    <label htmlFor="middleName" className="block mb-1">
                        {t("addUserToBranchDialog.middleNameLabel")}
                    </label>
                    <InputText
                        id="middleName"
                        value={middleName}
                        onChange={(e) => setMiddleName(e.target.value)}
                        onKeyDown={(e) => e.key === "Enter" && handleAddUser()}
                        className="w-full"
                    />
                </div>

                <div>
                    <label htmlFor="lastName" className="block mb-1">
                        {t("addUserToBranchDialog.lastNameLabel")}
                    </label>
                    <InputText
                        id="lastName"
                        value={lastName}
                        onChange={(e) => setLastName(e.target.value)}
                        onKeyDown={(e) => e.key === "Enter" && handleAddUser()}
                        className={errors.lastName ? "p-invalid w-full" : "w-full"}
                    />
                    {errors.lastName && (
                        <small className="p-error">{errors.lastName}</small>
                    )}
                </div>
                <div>
                    <label htmlFor="email" className="block mb-1">
                        {t("addUserToBranchDialog.emailLabel")}
                    </label>
                    <InputText
                        id="email"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                        onKeyDown={(e) => e.key === "Enter" && handleAddUser()}
                        className={errors.email ? "p-invalid w-full" : "w-full"}
                    />
                    {errors.email && (
                        <small className="p-error">{errors.email}</small>
                    )}
                </div>
                <div>
                    <label htmlFor="role" className="block mb-1">
                        {t("addUserToBranchDialog.roleLabel")}
                    </label>
                    <Dropdown
                        id="role"
                        value={role}
                        options={[
                            { label: "User", value: "USER" },
                            ...(isAdminAllowed
                                ? [{ label: "Admin", value: "ADMIN" }]
                                : [])
                        ]}
                        onChange={(e) => setRole(e.value)}
                        placeholder={t("addUserToBranchDialog.rolePlaceholder")}
                        className="w-full"
                        disabled={email === ""}
                    />
                    {!isAdminAllowed && role === "ADMIN" && (
                        <small className="p-error">
                            {t("addUserToBranchDialog.adminEmailRestriction") || "Admin role is allowed only for @integrabank.com emails."}
                        </small>
                    )}
                </div>
                <div>
                    <label className="block mb-1">
                        {t("addUserToBranchDialog.generatedPasswordLabel")}
                    </label>
                    <div style={{ display: "flex", alignItems: "center", gap: "8px" }}>
                        <InputText
                            type={showPassword ? "text" : "password"}
                            value={password}
                            onChange={e => setPassword(e.target.value)}
                            style={{ flex: 1 }}
                        />
                        <Button
                            icon={showPassword ? "pi pi-eye-slash" : "pi pi-eye"}
                            className="p-button-text p-button-sm"
                            onClick={() => setShowPassword((v) => !v)}
                            tooltip={
                                showPassword
                                    ? t("addUserToBranchDialog.hidePassword")
                                    : t("addUserToBranchDialog.showPassword")
                            }
                        />
                        <Button
                            icon="pi pi-copy"
                            className="p-button-text p-button-sm"
                            onClick={handleCopyPassword}
                            tooltip={t("addUserToBranchDialog.copyPassword")}
                        />
                        <Button
                            icon="pi pi-refresh"
                            className="p-button-text p-button-sm"
                            onClick={() => setPassword(generatePassword())}
                            tooltip={t("addUserToBranchDialog.regeneratePassword") || "Regenerate"}
                            type="button"
                        />
                    </div>
                    <small className="p-warning">
                        {t("addUserToBranchDialog.passwordVisibleWarning")}
                    </small>
                    {passwordCopied && (
                        <small className="p-success">
                            {t("addUserToBranchDialog.passwordCopied")}
                        </small>
                    )}
                    {errors.password && (
                        <small className="p-error">{errors.password}</small>
                    )}
                </div>
                <Button
                    label={
                        loading
                            ? t("addUserToBranchDialog.addingUser")
                            : t("addUserToBranchDialog.addUserButton")
                    }
                    onClick={handleAddUser}
                    disabled={loading}
                />
            </div>
        </Dialog>
    );
}
