import React, {useState, ChangeEvent, useRef} from "react";
import {Button} from "primereact/button";
import {InputTextarea} from "primereact/inputtextarea";
import {Toast} from "primereact/toast";
import ConfirmationDialog from "./ConfirmationDialog";
import { DepositsDTO, DepositControllerApi} from "../api";
import {useTranslation} from "react-i18next";

type BulkImportProps = {
    onClose: () => void;
};

function safeParseJson(text: string): { ok: true; value: any } | { ok: false; error: string } {
    try {
        const parsed = JSON.parse(text);
        return {ok: true, value: parsed};
    } catch (err: any) {
        return {ok: false, error: err?.message ?? "Invalid JSON"};
    }
}

const BulkImportDeposits: React.FC<BulkImportProps> = ({onClose}) => {
    const [textAreaValue, setTextAreaValue] = useState("");
    const [fileName, setFileName] = useState<string | null>(null);
    const [fileContent, setFileContent] = useState<any[] | null>(null);
    const [fileError, setFileError] = useState<string | null>(null);
    const [textError, setTextError] = useState<string | null>(null);
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [showConfirm, setShowConfirm] = useState(false);
    const [pendingPayload, setPendingPayload] = useState<any[] | null>(null);
    const {t} = useTranslation();

    const toast = useRef<Toast>(null);

    const onTextChange = (e: ChangeEvent<HTMLTextAreaElement>) => {
        const v = e.target.value;
        setTextAreaValue(v);
        if (!v.trim()) {
            setTextError(null);
            return;
        }
        const parsed = safeParseJson(v);
        if (!parsed.ok) {
            setTextError(parsed.error);
        } else if (!Array.isArray(parsed.value)) {
            setTextError(t("bulkImportDeposits.errorNotArray"));
        } else {
            setTextError(null);
        }
    };

    const onFileChange = (e: ChangeEvent<HTMLInputElement>) => {
        setFileError(null);
        setFileContent(null);
        setFileName(null);

        const files = e.target.files;
        if (!files || files.length === 0) return;

        const file = files[0];
        setFileName(file.name);

        const reader = new FileReader();
        reader.onload = () => {
            const text = String(reader.result ?? "");
            const parsed = safeParseJson(text);
            if (!parsed.ok) {
                setFileError(parsed.error);
                return;
            }
            setFileContent(Array.isArray(parsed.value) ? parsed.value : [parsed.value]);
        };
        reader.onerror = () => setFileError(t("bulkImportDeposits.errorReadingFile"));
        reader.readAsText(file);
    };

    const buildMergedPayload = (): any[] | null => {
        const arrs: any[] = [];
        if (fileContent) arrs.push(...fileContent);
        if (textAreaValue.trim()) {
            const parsed = safeParseJson(textAreaValue);
            if (parsed.ok) {
                arrs.push(...(Array.isArray(parsed.value) ? parsed.value : [parsed.value]));
            }
        }
        return arrs.length > 0 ? arrs : null;
    };

    const onImportClick = async () => {
        if (textAreaValue && textError) {
            toast.current?.show({
                severity: "error",
                summary: t("bulkImportDeposits.invalidJson"),
                detail: textError
            });
            return;
        }
        if (fileError) {
            toast.current?.show({
                severity: "error",
                summary: t("bulkImportDeposits.invalidFile"),
                detail: textError
            });
            return;
        }

        const payload = buildMergedPayload();
        if (!payload) {
            toast.current?.show({
                severity: "warn",
                summary: t("bulkImportDeposits.nothingToImport"),
                detail: t("bulkImportDeposits.nothingToImport")
            });
            return;
        }

        setPendingPayload(payload);
        setShowConfirm(true);
    };

        const handleAccept = async () => {
            if (!pendingPayload) return;
            setShowConfirm(false);
            setIsSubmitting(true);
            try {
                const api= new DepositControllerApi();
                const resp= await api.importDeposits({ depositImports: pendingPayload });

                toast.current?.show({
                    severity: "success",
                    summary: t("bulkImportDeposits.successTitle"),
                    detail: t("bulkImportDeposits.successMessage")
                });

                setTextAreaValue("");
                setFileContent(null);
                setFileName(null);
                setFileError(null);
                setTextError(null);
                onClose();
            } catch (err: any) {
                toast.current?.show({
                    severity: "error",
                    summary: t("bulkImportDeposits.errorTitle"),
                    detail: err.message ?? t("bulkImportDeposits.unknownError")
                });
            } finally {
                setIsSubmitting(false);
            }
        }

    const handleReject = () => {
        setShowConfirm(false);
    };

    return (
        <div className="p-4 border-round border-1 surface-border">
            <Toast ref={toast}/>
            <ConfirmationDialog
                visible={showConfirm}
                message={t("bulkImportDeposits.confirmMessage", {
                    count: pendingPayload?.length ?? 0,
                })}
                onAccept={handleAccept}
                onReject={handleReject}
                onHide={() => setShowConfirm(false)}
            />
            <h2 className="text-2xl font-bold mb-3">{t("bulkImportDeposits.title")}</h2>

            <div className="mb-3">
                <label className="block font-medium mb-2">{t("bulkImportDeposits.pasteJsonLabel")}</label>
                <InputTextarea
                    value={textAreaValue}
                    onChange={onTextChange}
                    rows={6}
                    className="w-full"
                    placeholder={t("bulkImportDeposits.jsonPlaceholder")}
                />
                {textError && <div className="text-sm text-red-500 mt-1">{textError}</div>}
            </div>

            <div className="mb-3">
                <label className="block font-medium mb-2">{t("bulkImportDeposits.uploadFileLabel")}</label>
                <div className="flex items-center gap-1">
                    <label
                        htmlFor="file-upload"
                        className="bg-green-500 text-white px-3 py-1 rounded border border-green-700 hover:bg-green-600 transition cursor-pointer"
                    >
                        {t("bulkImportDeposits.chooseFile")}
                    </label>
                    <span
                        className={`px-3 py-1 rounded text-sm ${
                            fileName ? "text-gray-700" : "text-gray-400"
                        }`}
                    > {fileName || t("bulkImportDeposits.noFileChosen")}
                    </span>
                </div>
                <input
                    id="file-upload"
                    type="file"
                    accept=".json,application/json"
                    onChange={onFileChange}
                    className="hidden"
                />

                {fileError && <div className="text-sm text-red-500 mt-1">{fileError}</div>}
            </div>


            <div className="flex gap-3 mt-4">
                <Button
                    label={t("bulkImportDeposits.importButton")}
                    icon="pi pi-check"
                    onClick={onImportClick}
                    disabled={isSubmitting}
                    className="p-button-primary"
                />
                <Button
                    label={t("bulkImportDeposits.cancelButton")}
                    icon="pi pi-times"
                    onClick={onClose}
                    className="p-button-secondary"
                />
            </div>
        </div>
    );
}

export default BulkImportDeposits;
