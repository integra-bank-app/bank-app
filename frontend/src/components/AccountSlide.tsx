type AccountSlideProps = {
    accountId: string;
    balance: number | null;
};

export function AccountSlide({ accountId, balance }: AccountSlideProps) {
    return (
        <div className="flex flex-col items-center p-4 bg-surface-100 rounded shadow">
            <h2>Account {accountId}</h2>
            <p>
                Balance: <strong>{balance !== null ? balance : "Loading..."}</strong>
            </p>
        </div>
    );
}