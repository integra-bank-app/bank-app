import React from "react";
import {ConfirmDialog} from "primereact/confirmdialog";

interface ConfirmationDialogProps {
    visible: boolean;
    message?: string;
    onAccept: () => void;
    onReject: () => void;
    onHide: () => void;
}

const ConfirmationDialog: React.FC<ConfirmationDialogProps> =
    ({
         visible,
         message = "Are you sure you want to do this?",    // default message for general ConfirmationDialog
         onAccept,
         onReject,
         onHide,
     }) => {
        return (
            <ConfirmDialog
                visible={visible}                   // whether to make the dialog visible or not
                onHide={onHide}
                message={message}
                header="Confirmation"
                icon="pi pi-exclamation-triangle"
                accept={onAccept}                   // action when user accepts
                reject={onReject}                   // action when user rejects
            />
        );
    };

export default ConfirmationDialog;
