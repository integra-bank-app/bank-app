type TotalBalanceSlideProps = {
    totalBalance: number | null;
};

export function TotalBalanceSlide({ totalBalance }: TotalBalanceSlideProps) {
    return (
        <div className="flex flex-col items-center p-4 bg-surface-100 rounded shadow">
            <h2>Total Balance</h2>
            <p>
                Balance: <strong>{totalBalance !== null ? totalBalance : "Loading..."}</strong>
            </p>
        </div>
    );
}