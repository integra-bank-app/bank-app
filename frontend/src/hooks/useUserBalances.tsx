import { useEffect, useState } from "react";
import axios from "axios";
import { UserControllerApiFp, Configuration } from "../api";

const config = new Configuration({ basePath: import.meta.env.VITE_BACKEND_API_URL });
const api = UserControllerApiFp(config);

export function useUserBalances( userId: string | null ) {
    const [accounts, setAccounts] = useState<string[]>([]);
    const [balances, setBalances] = useState<Record<string, number>>({});
    const [totalBalance, setTotalBalance] = useState<number | null>(null);

    async function loadData() {
        if (!userId) 
            return;
        const getAccountsFn = await api.getUserAccounts(userId);
        const accountsRes = await getAccountsFn(axios, config.basePath);
        setAccounts(accountsRes.data);

        const balancesObj: Record<string, number> = {};
        for (const accId of accountsRes.data) {
            const getBalanceFn = await api.getUserAccountBalance(userId, accId);
            const balanceRes = await getBalanceFn(axios, config.basePath);
            balancesObj[accId] = balanceRes.data;
        }
        setBalances(balancesObj);

        const getTotalBalanceFn = await api.getUserTotalBalanceById(userId);
        const totalBalanceRes = await getTotalBalanceFn(axios, config.basePath);
        setTotalBalance(totalBalanceRes.data);
    }

    useEffect(() => {
        loadData();
        
        function handleRefetch() {
            loadData();
        }
        window.addEventListener("refetchData", handleRefetch);
        return () => window.removeEventListener("refetchData", handleRefetch);
    }, [userId]);

    return { accounts, balances, totalBalance}
}
