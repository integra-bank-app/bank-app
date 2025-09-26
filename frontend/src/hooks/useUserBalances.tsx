import { useEffect, useState } from "react";
import { UserControllerApi, Configuration } from "../api";

export function useUserBalances( userId: string | null ) {
    const [accounts, setAccounts] = useState<string[]>([]);
    const [balances, setBalances] = useState<Record<string, number>>({});
    const [totalBalance, setTotalBalance] = useState<number | null>(null);

    async function fetchAccountsAndBalances() {
        if (!userId) 
            return;
        const api = new UserControllerApi();
        const accounts = await api.getUserAccounts(userId);
        setAccounts(accounts.data);

        const balancesObj: Record<string, number> = {};
        for (const accId of accounts.data) {
            const balance = await api.getUserAccountBalance(userId, accId);
            balancesObj[accId] = balance.data;
        }
        setBalances(balancesObj);

        const totalBalance = await api.getUserTotalBalanceById(userId);
        setTotalBalance(totalBalance.data);
    }

    useEffect(() => {
        fetchAccountsAndBalances();
        
        function handleRefetch() {
            fetchAccountsAndBalances();
        }
        window.addEventListener("refetchData", handleRefetch);
        return () => window.removeEventListener("refetchData", handleRefetch);
    }, [userId]);

    return { accounts, balances, totalBalance}
}
