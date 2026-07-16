import {createContext, useMemo} from "react";
import { useSimpleQuery } from "@/services/queries.ts";
import type {Subscription} from "@/pages/interface/ProfileProps.ts";
import useAuth from "@/hooks/useAuth.ts";

export const SubscriptionContext = createContext(null);

const BASE_URL  = import.meta.env.VITE_BACKEND_BASE_URL;
const SUBSCRIPTION_URL = `${BASE_URL}/stripe/payment/subscriptions`

export const SubscriptionProvider = ({ children }) => {
    const { user } = useAuth();
    const subscriptionQueryData = useSimpleQuery<Subscription[]>({
        url: `${SUBSCRIPTION_URL}`,
        cacheKey: "subscription",
        queryOptions:{
            enabled: !!user,
            retry: false
        },
    });

    const subscription = subscriptionQueryData?.data?.[0];
    const payment_method = subscription?.payment_method;

    const providerValues = useMemo(() =>
        ({subscription, payment_method}), [subscription, payment_method]
    );

    return (
        <SubscriptionContext.Provider value={providerValues}>
            {children}
        </SubscriptionContext.Provider>
    );
}