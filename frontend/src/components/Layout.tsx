import React from "react";
import { Outlet } from "react-router-dom";
import Header from "./Header";
import Footer from "./Footer";
import Sidebar from "./Sidebar";

const Layout: React.FC = () => {
	return (
		<div className="flex">
			<Sidebar />
			<div className="flex flex-col flex-grow">
				<Header />
				<main className="flex-grow p-4">
					<Outlet />
				</main>
				<Footer />
			</div>
		</div>
	);
};

export default Layout;
