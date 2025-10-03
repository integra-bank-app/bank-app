import {useState} from "react";
import {Button} from "primereact/button";
import {useNavigate} from "react-router-dom";
import {useUserContext} from "../lib/hooks";
import AddUserToBranchDialog from "../components/AddUserToBranchDialog";
import {useTranslation} from "react-i18next";

function AdminPage() {
    const {user} = useUserContext();
    const [showAddUser, setShowAddUser] = useState(false);
    const navigate = useNavigate();
    const {t} = useTranslation();

    return (
        <section className="flex flex-column align-items-center p-4 gap-4">
            <h1 className="text-5xl font-bold">{t("adminPage.hello")}, {user.firstName}</h1>

            <div className="grid w-full" style={{maxWidth: "600px"}}>
                <div className="col-12 md:col-4">
                    <Button
                        label={t("adminPage.addUserLabel")}
                        onClick={() => setShowAddUser(true)}
                        className="w-full"
                    />
                </div>
                <div className="col-12 md:col-4">
                    <Button label={t("adminPage.exportUserLabel")} className="w-full"/>
                </div>
                <div className="col-12 md:col-4">
                    <Button label={t("adminPage.settingsLabel")} className="w-full"/>
                </div>
            </div>

            <div className="w-full" style={{maxWidth: "800px"}}></div>

            <AddUserToBranchDialog
                branchId={user.branchId}
                visible={showAddUser}
                onHide={() => setShowAddUser(false)}
                onUserAdded={() => navigate("/users")}
            />
        </section>
    );
}

export default AdminPage;
