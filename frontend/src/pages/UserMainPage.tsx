import React, {useEffect, useState, useCallback} from "react";
import {Carousel} from "primereact/carousel";
import {Card} from "primereact/card";
import {useAuthentication} from "../contexts/AuthenticationProvider";
import {TotalBalanceSlide} from "../components/TotalBalanceSlide";
import {AccountSlide} from "../components/AccountSlide";
import {Title} from "../components/TitleComponent";
import {useTranslation} from "react-i18next";
import {ScrollPanel} from "primereact/scrollpanel";


interface Transaction {
    transactionId: string;
    transactionType: string;
    description: string;
    amount: number;
    timestamp: string;
    fromUserId: string | null;
    toUserId: string | null;
}

const formatCurrencySuffix = (amount: number): string => {
    const absAmount = Math.abs(amount);
    const numberFormatter = new Intl.NumberFormat('en-US', {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2,
    });
    const formattedNumber = numberFormatter.format(absAmount);
    return `${formattedNumber} RON`;
};

export default function UserMainPage() {
    const {user, isAuthenticated} = useAuthentication();
    const [accounts, setAccounts] = useState<string[]>([]);
    const [balances, setBalances] = useState<Record<string, number>>({});
    const [totalBalance, setTotalBalance] = useState<number | null>(null);
    const [transactions, setTransactions] = useState<Transaction[]>([]);
    const [loading, setLoading] = useState(true);
    const {t} = useTranslation();

    const fetchUserData = useCallback(async () => {
        if (!user?.id) {
            setLoading(false);
            return;
        }

        setLoading(true);

        try {
            const token = localStorage.getItem("authToken");
            if (!token) {
                throw new Error("No auth token found");
            }

            const accountsResponse = await fetch(
                `http://localhost:8080/api/users/${user.id}/accounts`,
                {
                    headers: {
                        Authorization: `Bearer ${token}`,
                        "Content-Type": "application/json",
                    },
                }
            );

            if (!accountsResponse.ok) {
                throw new Error("Failed to fetch accounts");
            }

            const accountIds = await accountsResponse.json();
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
                    const balanceResponse = await fetch(
                        `http://localhost:8080/api/users/${user.id}/accounts/${accountId}`,
                        {
                            headers: {
                                Authorization: `Bearer ${token}`,
                                "Content-Type": "application/json",
                            },
                        }
                    );

                    if (balanceResponse.ok) {
                        const balance = await balanceResponse.json();
                        balancesObj[accountId] = balance;
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

            // Transactions
            const transactionsResponse = await fetch(
                `http://localhost:8080/api/users/${user.id}/transactions`,
                {
                    headers: {
                        Authorization: `Bearer ${token}`,
                        "Content-Type": "application/json",
                    },
                });
            if (transactionsResponse.ok) {
                const fetchedTransactions: Transaction[] = await transactionsResponse.json();
                setTransactions(fetchedTransactions); // Save transactions to state
            } else {
                setTransactions([]); // Set empty array on failure
                console.error("Failed to fetch transactions");
            }

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
            return <TotalBalanceSlide totalBalance={totalBalance}/>;
        } else {
            return <AccountSlide accountId={item} balance={balances[item] ?? null}/>;
        }
    };

    return (
        <div>
            <div className="flex flex-col items-center space-y-4 mt-4">
                <Title>{t("userMain.myAccounts")}</Title>
                <Carousel
                    value={carouselItems}
                    numVisible={1}
                    numScroll={1}
                    itemTemplate={itemTemplate}
                    className="custom-carousel md:w-1/2 rounded-lg shadow-lg layout-content"
                />
            </div>
            <div className="flex flex-col items-center space-y-4 mt-4">
                <Title>{t("userMain.transactionHistoryLabel")}</Title>
                <ScrollPanel style={{width: '70%', height: '70vh'}} className="p-4  rounded-lg shadow-2xl surface-card">
                    {transactions.length > 0 ? (
                        transactions.map((transaction) => (
                            <div
                                key={transaction.transactionId}
                                className="p-3 my-3 flex justify-content-between align-items-center border-b border-gray-200 hover:surface-hover transition-colors transition-duration-150"
                            >
                                <div className="flex flex-col">
                                    <span className="font-semibold text-lg">{transaction.description}</span>
                                    <span
                                        className="text-sm text-gray-500">{new Date(transaction.timestamp).toLocaleDateString()}</span>
                                </div>
                                <span
                                    className={`font-bold text-xl ${transaction.amount < 0 ? 'text-red-500' : 'text-green-500'}`}>
                                        {formatCurrencySuffix(transaction.amount)}
                                </span>

                            </div>
                        ))
                    ) : (
                        <div className="text-center p-5">
                            <p className="text-gray-500">{t("userMain.noTransactions")}</p>
                        </div>
                    )}
                </ScrollPanel>
            </div>
        </div>
    );
}


