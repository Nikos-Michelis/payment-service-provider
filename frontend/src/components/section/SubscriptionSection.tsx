import SuspenseLoader from "@/components/loader/SuspenseLoader.tsx";
import SpinnerLoader from "@/components/loader/SpinnerLoader.tsx";
import SubscriptionCard from "@/components/cards/SubscriptionCard.tsx";
import {PaymentCard} from "@/components/cards/PaymentMethodCard.tsx";
import {useParameterizedQuery} from "@/services/queries.ts";
import type {Subscription} from "@/pages/interface/ProfileProps.ts";

const BASE_URL  = import.meta.env.VITE_BACKEND_BASE_URL;
const SUBSCRIPTION_URL = `${BASE_URL}/stripe/payment/subscription/list`

const SubscriptionSection = () => {

    const subscriptionQueryData = useParameterizedQuery<Subscription[]>({
        url: `${SUBSCRIPTION_URL}`,
        params: `subscription-}`,
        cacheKey: "subscription",
        queryOptions: {
            suspense: true
        },
        enableBoundary: false
    });

    const subscription = subscriptionQueryData?.data?.[0] ?? {};
    const { payment_method } = subscription;

    return (
        <>
            <SuspenseLoader fallbackComponent={<SpinnerLoader />}>
                <SubscriptionCard subscription={subscription}/>
            </SuspenseLoader>

            <SuspenseLoader fallbackComponent={<SpinnerLoader />}>
                <PaymentCard {...payment_method}/>
            </SuspenseLoader>
        </>
    )
}

export default SubscriptionSection;