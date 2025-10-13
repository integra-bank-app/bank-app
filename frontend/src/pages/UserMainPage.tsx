import { useEffect, useState, useCallback } from "react";
import { Carousel } from "primereact/carousel";
import { Card } from "primereact/card";
import { useAuthentication } from "../contexts/AuthenticationProvider";
import { TotalBalanceSlide } from "../components/TotalBalanceSlide";
import { AccountSlide } from "../components/AccountSlide";
import { Title } from "../components/TitleComponent";

export default function UserMainPage() {
    const { user, isAuthenticated } = useAuthentication();
    const [accounts, setAccounts] = useState<string[]>([]);
    const [balances, setBalances] = useState<Record<string, number>>({});
    const [totalBalance, setTotalBalance] = useState<number | null>(null);
    const [loading, setLoading] = useState(true);

    const fetchUserData = useCallback(async () => {
        if (!user?.id) {
            setLoading(false);
            return;
        }

        setLoading(true);

        try {
            const token = localStorage.getItem('authToken');
            if (!token) {
                throw new Error('No auth token found');
            }

            const accountsResponse = await fetch(`http://localhost:8080/api/users/${user.id}/accounts`, {
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json'
                }
            });

            if (!accountsResponse.ok) {
                throw new Error('Failed to fetch accounts');
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
                                'Authorization': `Bearer ${token}`,
                                'Content-Type': 'application/json'
                            }
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

            const total = Object.values(balancesObj).reduce((sum, balance) => sum + balance, 0);
            setTotalBalance(total);

        } catch (err) {
            console.error('Error fetching user data:', err);
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
            <div className="flex justify-content-center align-items-center" style={{ minHeight: '60vh' }}>
                <div className="text-center">
                    <i className="pi pi-spinner pi-spin text-4xl text-primary mb-3"></i>
                    <h3 className="text-lg font-semibold mb-2">Loading your accounts...</h3>
                </div>
            </div>
        );
    }

    if (!accounts || accounts.length === 0) {
        return (
            <div className="flex flex-col items-center space-y-4 mt-4">
                <Title>My Accounts</Title>
                <Card className="text-center p-6 shadow-3">
                    <div className="mb-4">
                        <i className="pi pi-info-circle text-4xl text-blue-500 mb-3"></i>
                        <h3 className="text-xl font-semibold mb-2">No Accounts Found</h3>
                        <p className="text-gray-600 mb-4">
                            You don't have any accounts yet. Please contact your bank to set up an account.
                        </p>
                    </div>
                </Card>
            </div>
        );
    }

    const totalBalanceItem = "TotalBalance";
    const carouselItems = [totalBalanceItem, ...accounts]; // Total Balance primul

    const itemTemplate = (item: string) => {
        if (item === totalBalanceItem) {
            return <TotalBalanceSlide totalBalance={totalBalance} />;
        } else {
            return <AccountSlide accountId={item} balance={balances[item] ?? null} />;
        }
    };

    return (
        <div className="flex flex-col items-center space-y-4 mt-4">
            <Title>My Accounts</Title>
            <Carousel
                value={carouselItems}
                numVisible={1}
                numScroll={1}
                itemTemplate={itemTemplate}
                className="custom-carousel md:w-1/2 rounded-lg shadow-lg layout-content"
            />
        </div>
    );
}