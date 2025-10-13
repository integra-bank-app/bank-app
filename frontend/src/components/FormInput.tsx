import { InputText } from "primereact/inputtext";
import { Password } from "primereact/password";

type FormInputProps = {
    id: string;
    label: string;
    type?: "text" | "email" | "password";
    value: string;
    onChange: (value: string) => void;
    error?: string;
    required?: boolean;
    placeholder?: string;
};

export default function FormInput({
                                      id,
                                      label,
                                      type = "text",
                                      value,
                                      onChange,
                                      error,
                                      required = false,
                                      placeholder,
                                  }: FormInputProps) {
    return (
        <div className="mb-4">
            <label htmlFor={id} className="block font-semibold text-sm mb-2">
                {label} {required && <span className="text-red-500">*</span>}
            </label>
            {type === "password" ? (
                <div style={{ width: '100%', display: 'block' }}>
                    <Password
                        id={id}
                        value={value}
                        onChange={(e) => onChange(e.target.value)}
                        placeholder={placeholder}
                        toggleMask
                        feedback={false}
                        className={error ? "p-invalid" : ""}
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
            ) : (
                <InputText
                    id={id}
                    type={type}
                    value={value}
                    onChange={(e) => onChange(e.target.value)}
                    placeholder={placeholder}
                    className={error ? "p-invalid w-full" : "w-full"}
                    style={{ width: '100%', display: 'block' }}
                />
            )}
            {error && (
                <small className="block mt-1 text-red-500">{error}</small>
            )}
        </div>
    );
}