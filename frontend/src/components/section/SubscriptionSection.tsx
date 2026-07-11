import SubscriptionCard from "@/components/cards/SubscriptionCard.tsx";
import {PaymentCard} from "@/components/cards/PaymentMethodCard.tsx";
import {useSubscription} from "@/hooks/useSubscription.ts";


const SubscriptionSection = () => {
    const {subscription, payment_method} = useSubscription()

    return (
        <>
            <SubscriptionCard subscription={subscription}/>
            { payment_method && <PaymentCard {...payment_method} />}
        </>
    )
}

export default SubscriptionSection;