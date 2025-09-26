import { useState } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import { FaUser } from "react-icons/fa";
import { Button } from "primereact/button";
import ConfirmationDialog from "./ConfirmationDialog";

const Header: React.FC = () => {
	const navigate = useNavigate();
	const location = useLocation();
	const [confirmVisible, setConfirmVisible] = useState(false);

	const handleLogout = () => {
		// Handle user tokens here
		navigate("/login");
	};

	const showLogout = location.pathname !== "/login";
	const showUserIcon = location.pathname !== "/login";

	return (
		<header className="p-2 flex items-center justify-center relative">
			{/* Centered user icon */}
			{showUserIcon && <FaUser size={22} />}
			{/* Right side: Logout button */}
			{showLogout && (
				<Button
					className="absolute right-2 font-semibold px-3 py-1 rounded text-sm"
					onClick={() => setConfirmVisible(true)}
				>
					Logout
				</Button>
			)}
			{/* Confirmation Dialog configuration */}
			<ConfirmationDialog
				visible={confirmVisible}
				message="Are you sure you want to log out?"
				onAccept={handleLogout}
				onReject={() => setConfirmVisible(false)}
				onHide={() => setConfirmVisible(false)}
			/>
		</header>
	);
};

export default Header;
