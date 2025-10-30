import React from "react";
import {Chart} from "primereact/chart";
import {depositColors} from "../../lib/utils";
import {InvestmentDTO,InvestmentHistoryDTO} from "../../api";
import { useState } from "react";
import {useEffect} from "react";
import {useAuthentication} from "../../contexts/AuthenticationProvider";


interface InvestmentChartProps {
    total: number
}

export const InvestmentChart: React.FC<InvestmentChartProps> = ({ total }) => {
    const [investments, setInvestments] = useState<InvestmentDTO[]>([]);
    const [history, setHistory] = useState<InvestmentHistoryDTO[]>([]);
    const { user, isAuthenticated } = useAuthentication();

    useEffect(() => {

        if (!user?.id) {
            return;
        }

        const token = localStorage.getItem("authToken");

        const fetchData = async () => {
            try {
                const [invRes, histRes] = await Promise.all([
                    fetch(`http://localhost:8080/api/users/${user.id}/investment`, {
                        headers: {
                            "Content-Type": "application/json",
                            Authorization: `Bearer ${token}`,
                        },
                    }),
                    fetch(`http://localhost:8080/api/users/${user.id}/investments/history`, {
                        headers: {
                            "Content-Type": "application/json",
                            Authorization: `Bearer ${token}`,
                        },
                    }),
                ]);

                if (!invRes.ok || !histRes.ok) throw new Error("Network error");

                const invData = await invRes.json();
                const histData = await histRes.json();

                setInvestments(invData);
                setHistory(histData);
            } catch (error) {
                console.error("Failed to fetch investments:", error);
            }
        };

        fetchData(); // initial load
        const interval = setInterval(fetchData, 5000); // every 5s

        return () => clearInterval(interval); // cleanup on unmount
    }, [user?.id]);

    const dates = Array.from(
        new Set(
            history
                .map(h => h.date)           // map to date
                .filter((d): d is string => !!d) // filter out undefined/null
                .map(d => {
                    const date = new Date(d);
                    // Round to nearest 30 minutes
                    const rounded = new Date(date);
                    rounded.setMinutes(Math.floor(date.getMinutes() / 30) * 30, 0, 0);
                    return rounded.toISOString();})
        )
    ).sort((a, b) => new Date(a).getTime() - new Date(b).getTime());

    const datasets = investments.map((inv, index) => {
        const invHistory = history.filter(h => h.investmentId === inv.id);

        const data = dates.map(dateLabel => {
            const h = invHistory.find(
                h => h.date && new Date(h.date).toISOString().slice(0,16) === dateLabel.slice(0,16)
            );
            const balance = Number(h?.balance ?? 0);

            return balance;
        });

        return {
            label: `Investment ${index + 1}`,
            data,
            fill: false,
            borderColor: depositColors[index % depositColors.length],
            tension: 0.4,
            pointBackgroundColor: depositColors[index % depositColors.length],
        };
    }).filter(Boolean) as any[];

    const data = { labels: dates.map(d =>
            new Date(d).toLocaleString([], { hour: "2-digit", minute: "2-digit", month: "short", day: "numeric" })
        ),
        datasets};

    const options = {
        plugins: { legend: { display: true, labels: { color: "#d9d9d9" } } },
        scales: {
            x: { ticks: { color: "#d9d9d9" }, grid: { color: "#333" } },
            y: { ticks: { color: "#d9d9d9" }, grid: { color: "#333" } },
        },
    };

    return (
        <div className="flex flex-col items-center w-full max-w-2xl mx-auto mt-6">

        <h2 className="text-2xl font-semibold text-gray-100 mb-2">

            {total.toFixed(2)} RON

        </h2>

        <div className="w-full bg-[#1e1e2f] p-4 rounded-2xl shadow-md">

            <Chart type="line" data={data} options={options}/>

        </div>

    </div>
    );
}