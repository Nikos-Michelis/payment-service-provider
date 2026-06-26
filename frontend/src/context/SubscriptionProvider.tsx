import {createContext, useMemo} from "react";
import {useParameterizedQuery} from "@/services/queries.ts";
import type {Subscription} from "@/pages/interface/ProfileProps.ts";

export const SubscriptionContext = createContext(null);

const BASE_URL  = import.meta.env.VITE_BACKEND_BASE_URL;
const SUBSCRIPTION_URL = `${BASE_URL}/stripe/payment/subscription/list`

export const SubscriptionProvider = ({ children }) => {

    const subscriptionQueryData = useParameterizedQuery<Subscription[]>({
        url: `${SUBSCRIPTION_URL}`,
        params: `subscription`,
        cacheKey: "subscription",
        queryOptions: {
            suspense: true
        },
        enableBoundary: false
    });

    const providerValues = useMemo(()=> ({ subscriptionQueryData }),[subscriptionQueryData])

    return (
        <SubscriptionContext.Provider value={providerValues}>
            {children}
        </SubscriptionContext.Provider>
    );
}