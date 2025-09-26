import { Carousel } from "primereact/carousel";
import { useUserBalances} from "../hooks/useUserBalances";
import { TotalBalanceSlide } from "../components/TotalBalanceSlide";
import { AccountSlide } from "../components/AccountSlide";
import { useUserContext } from "../lib/hooks";

export default function UserMainPage() {
    const userId = useUserContext().user.uuid;
    
    const {accounts, balances, totalBalance} = useUserBalances(userId);

    const carouselItems = [...accounts, "Total Balance"];

    const itemTemplate = (item: string) => {
        if (item === "Total Balance") {
            return <TotalBalanceSlide totalBalance={totalBalance} />;
        } else {
            return <AccountSlide accountId={item} balance={balances[item] ?? null} />;
        }
    };

    return (
        <div className="flex flex-col items-center space-y-4 mt-4">
            <h2 className="text-2xl md:text-3xl font-semibold text-gray-900">
                My Accounts
            </h2>
            <Carousel 
                value={carouselItems} 
                numVisible={1} 
                numScroll={1} 
                itemTemplate={itemTemplate}
                className="custom-carousel"
            />
        </div>
    );
}