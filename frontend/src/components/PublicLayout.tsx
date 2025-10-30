import React from "react";
import { Outlet } from "react-router-dom";
import Header from "./Header";
import Footer from "./Footer";

const PublicLayout: React.FC = () => {
	return (
		<div className="flex flex-col min-h-screen bg-surface-c">
			<Header />
			<main className="flex-grow-1 p-4">
				<Outlet />
			</main>
			<Footer />
		</div>
	);
};

export default PublicLayout;
