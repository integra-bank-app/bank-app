import { use } from "react";
import { Carousel } from "primereact/carousel";
import { NotificationContext } from "../contexts/NotificationProvider";
import { useUserBalances} from "../hooks/useUserBalances";
import { TotalBalanceSlide } from "../components/TotalBalanceSlide";
import { AccountSlide } from "../components/AccountSlide";

export function UserMainPage() {
    const context = use(NotificationContext);
    if (!context) throw new Error("NotificationContext is null");
    const userId = context.uuid;
    
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
        <div className="flex justify-center items-center">
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