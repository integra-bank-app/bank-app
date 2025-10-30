import React from "react";
import { Outlet } from "react-router-dom";
import Header from "./Header";
import Footer from "./Footer";
import Sidebar from "./Sidebar";
const ProtectedLayout: React.FC = () => {
	return (
		<div className="flex bg-surface-c">
			<div className="hidden md:flex">
				<Sidebar />
			</div>

			<div className="flex flex-col flex-grow-1 h-screen overflow-y-auto">
				<Header />

				<main className="flex-grow p-4">
					<Outlet />
				</main>
				<Footer />
			</div>
		</div>
	);
};

export default ProtectedLayout;
