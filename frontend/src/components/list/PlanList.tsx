import {useSimpleQuery} from "@/services/queries.ts";
import PlanCard from "@/components/cards/PlanCard.tsx";
const BASE_URL  = import.meta.env.VITE_BACKEND_BASE_URL;
const PLANS_URL = `${BASE_URL}/public/plans`

const PlanList = ({ billingCycle }: { billingCycle: 'MONTH' | 'YEAR' }) => {
    const planQueryData = useSimpleQuery({
        url: `${PLANS_URL}`,
        cacheKey: "plans",
        queryOptions: {
            suspense: true
        },
        enableBoundary: false
    });

    const plans = planQueryData?.data ?? [];

    return (
        <>
            {plans.map((plan) => (
                <PlanCard
                    key={plan.id}
                    {...plan}
                    billingCycle={billingCycle}
                />
            ))}
        </>
    );
};

export default PlanList;