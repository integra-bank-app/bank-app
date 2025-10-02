import { Carousel } from "primereact/carousel";
import { useUserBalances} from "../hooks/useUserBalances";
import { TotalBalanceSlide } from "../components/TotalBalanceSlide";
import { AccountSlide } from "../components/AccountSlide";
import { useUserContext } from "../lib/hooks";
import { Title } from "../components/TitleComponent";

export default function UserMainPage() {
    const userId = useUserContext().user.uuid;
    
    const {accounts, balances, totalBalance} = useUserBalances(userId);

    const totalBalanceItem = "TotalBalance";
    const carouselItems = [...accounts, totalBalanceItem];

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