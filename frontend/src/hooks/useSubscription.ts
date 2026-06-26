import {SubscriptionContext} from "@/context/SubscriptionProvider.tsx";
import {useContext} from "react";

export const useSubscription = () => {
    const nasaApodContext = useContext(SubscriptionContext);
    if (!nasaApodContext) {
        throw new Error("SubscriptionContext must be used within SubscriptionProvider");
    }
    return nasaApodContext;
};