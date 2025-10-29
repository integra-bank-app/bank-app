import {useEffect, useState, useCallback} from "react";
import {Carousel} from "primereact/carousel";
import {Card} from "primereact/card";
import {useAuthentication} from "../contexts/AuthenticationProvider";
import {TotalBalanceSlide} from "../components/TotalBalanceSlide";
import {AccountSlide} from "../components/AccountSlide";
import {Title} from "../components/TitleComponent";
import {useTranslation} from "react-i18next";
import {UserControllerApi} from "../api/api";
import {SendMoney} from "../components/SendMoney";

export default function UserMainPage() {
	const {user, isAuthenticated} = useAuthentication();
	const [accounts, setAccounts] = useState<string[]>([]);
	const [balances, setBalances] = useState<Record<string, number>>({});
	const [totalBalance, setTotalBalance] = useState<number | null>(null);
	const [loading, setLoading] = useState(true);
	const {t} = useTranslation();

	const fetchUserData = useCallback(async () => {
		if (!user?.id) {
			setLoading(false);
			return;
		}

		setLoading(true);

		try {
			const userControllerApi = new UserControllerApi();

			const accountsResponse = await userControllerApi.getUserAccounts(user.id);
			const accountIds = accountsResponse.data;
			setAccounts(accountIds);

			if (!accountIds || accountIds.length === 0) {
				setBalances({});
				setTotalBalance(0);
				setLoading(false);
				return;
			}

			const balancesObj: Record<string, number> = {};
			for (const accountId of accountIds) {
				try {
					const balanceResponse = await userControllerApi.getUserAccountBalance(
						user.id,
						accountId
					);
					if (balanceResponse.status === 200) {
						balancesObj[accountId] = balanceResponse.data;
					} else {
						balancesObj[accountId] = 0;
					}
				} catch (err) {
					balancesObj[accountId] = 0;
				}
			}

			setBalances(balancesObj);

			const total = Object.values(balancesObj).reduce(
				(sum, balance) => sum + balance,
				0
			);
			setTotalBalance(total);
		} catch (err) {
			console.error("Error fetching user data:", err);
		} finally {
			setLoading(false);
		}
	}, [user]);

	useEffect(() => {
		if (!isAuthenticated || !user) {
			setLoading(false);
			return;
		}
		fetchUserData();
	}, [isAuthenticated, user, fetchUserData]);

	useEffect(() => {
		const handler = () => {
			fetchUserData();
		};
		window.addEventListener("refetchData", handler);
		return () => window.removeEventListener("refetchData", handler);
	}, [fetchUserData]);

	if (loading) {
		return (
			<div
				className="flex justify-content-center align-items-center"
				style={{minHeight: "60vh"}}
			>
				<div className="text-center">
					<i className="pi pi-spinner pi-spin text-4xl text-primary mb-3"></i>
					<h3 className="text-lg font-semibold mb-2">
						{t("userMain.loading")}
					</h3>
				</div>
			</div>
		);
	}

	if (!accounts || accounts.length === 0) {
		return (
			<div className="flex flex-col items-center space-y-4 mt-4">
				<Title>{t("userMain.myAccounts")}</Title>
				<Card className="text-center p-6 shadow-3">
					<div className="mb-4">
						<i className="pi pi-info-circle text-4xl text-blue-500 mb-3"></i>
						<h3 className="text-xl font-semibold mb-2">
							{t("userMain.noAccounts")}
						</h3>
						<p className="text-gray-600 mb-4">{t("userMain.noAccountsDesc")}</p>
					</div>
				</Card>
			</div>
		);
	}

	const totalBalanceItem = "TotalBalance";
	const carouselItems = [totalBalanceItem, ...accounts];

	const itemTemplate = (item: string) => {
		if (item === totalBalanceItem) {
			return <TotalBalanceSlide totalBalance={totalBalance} />;
		} else {
			return <AccountSlide accountId={item} balance={balances[item] ?? null} />;
		}
	};

	return (
		<div className="flex flex-col items-center space-y-4 mt-4">
			<Title>{t("userMain.myAccounts")}</Title>
			<Carousel
				value={carouselItems}
				numVisible={1}
				numScroll={1}
				itemTemplate={itemTemplate}
				className="custom-carousel md:w-1/2 rounded-lg shadow-lg layout-content"
			/>
			<SendMoney />
		</div>
	);
}
