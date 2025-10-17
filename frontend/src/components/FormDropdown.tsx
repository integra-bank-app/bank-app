import { Dropdown } from "primereact/dropdown";

type FormDropdownProps = {
    id: string;
    label: string;
    value: string;
    onChange: (value: string) => void;
    options: { label: string; value: string }[];
    error?: string;
    required?: boolean;
    placeholder?: string;
};

export default function FormDropdown({
                                         id,
                                         label,
                                         value,
                                         onChange,
                                         options,
                                         error,
                                         required = false,
                                         placeholder,
                                     }: FormDropdownProps) {
    return (
        <div className="flex flex-col gap-2 mb-4">
            <label htmlFor={id} className="font-semibold text-sm">
                {label} {required && <span className="text-red-500">*</span>}
            </label>
            <Dropdown
                id={id}
                value={value}
                onChange={(e) => onChange(e.value)}
                options={options}
                placeholder={placeholder}
                className={error ? "p-invalid w-full" : "w-full"}
            />
            {error && <small className="text-red-500">{error}</small>}
        </div>
    );
}