import React, {useState} from 'react';
import {Zap} from 'lucide-react';
import {Badge} from '@/components/ui/badge.tsx';
import {Switch} from '@/components/ui/switch.tsx';
import {Label} from '@/components/ui/label.tsx';
import PlanCard from "@/components/cards/PlanCard.tsx";
import {useSimpleQuery} from "@/services/queries.ts";
import ContentLoader from "@/components/loader/ContentLoader.tsx";
import SpinnerLoader from "@/components/loader/SpinnerLoader.tsx";

const BASE_URL  = import.meta.env.VITE_BACKEND_BASE_URL;
const PLANS_URL = `${BASE_URL}/public/plans`


const PricingPage: React.FC = () => {
    const [billingCycle, setBillingCycle] = useState<'monthly' | 'yearly'>('monthly');

    const planQueryData = useSimpleQuery({
        url: `${PLANS_URL}`,
        params: `plans`,
        cacheKey: "plans",
        queryOptions: {
            suspense: true
        },
        enableBoundary: false
    });

    const plans = planQueryData?.data ?? [];


    return (
        <div className="mx-auto max-w-7xl px-6 py-16">
            <div className="mb-16 space-y-4 text-center">
                <Badge variant="secondary" className="px-4 py-1.5 text-sm">
                    <Zap className="mr-2 h-3.5 w-3.5"/>
                    Flexible Pricing for Every Team
                </Badge>

                <h1 className="text-5xl font-bold text-foreground md:text-6xl">
                    Choose Your Perfect Plan
                </h1>

                <p className="mx-auto max-w-2xl text-xl text-muted-foreground">
                    Start with a plan that fits your needs today. Scale seamlessly as your
                    business grows. All plans include our core features and 24/7 support.
                </p>

                <div className="flex items-center justify-center gap-4 pt-4">
                    <Label
                        htmlFor="billing-toggle"
                        className={
                            billingCycle === "monthly"
                                ? "font-semibold text-foreground"
                                : "text-muted-foreground"
                        }
                    >
                        Monthly
                    </Label>

                    <Switch
                        id="billing-toggle"
                        checked={billingCycle === "yearly"}
                        onCheckedChange={(checked) =>
                            setBillingCycle(checked ? "yearly" : "monthly")
                        }
                    />

                    <Label
                        htmlFor="billing-toggle"
                        className={
                            billingCycle === "yearly"
                                ? "font-semibold text-foreground"
                                : "text-muted-foreground"
                        }
                    >
                        Yearly

                        <Badge
                            variant="secondary"
                            className="ml-2 bg-primary/10 text-primary"
                        >
                            Save 20%
                        </Badge>
                    </Label>
                </div>
            </div>

            <div className="mb-16 grid grid-cols-1 gap-8 md:grid-cols-3">
                <ContentLoader fallbackComponent={<SpinnerLoader/>}>
                    {plans.map((plan) => (
                        <PlanCard
                            key={plan.id}
                            {...plan}
                            billingCycle={billingCycle}
                        />
                    ))}
                </ContentLoader>
            </div>

            <div className="rounded-2xl border border-border bg-card p-8 md:p-12">
                <h2 className="mb-8 text-center text-3xl font-bold text-foreground">
                    Frequently Asked Questions
                </h2>

                <div className="mx-auto grid max-w-5xl grid-cols-1 gap-8 md:grid-cols-2">
                    {[
                        {
                            q: "Can I change my plan later?",
                            a: "Absolutely! You can upgrade or downgrade your plan at any time. Changes take effect immediately, and we'll prorate your billing accordingly.",
                        },
                        {
                            q: "What payment methods do you accept?",
                            a: "We accept all major credit cards (Visa, Mastercard, American Express) and support international payments through Stripe.",
                        },
                        {
                            q: "Is there a free trial?",
                            a: "Yes! All plans come with a 14-day free trial. No credit card required to start. Cancel anytime during the trial period at no charge.",
                        },
                        {
                            q: "What's your refund policy?",
                            a: "We offer a 30-day money-back guarantee. If you're not satisfied with our service, contact us for a full refund within the first month.",
                        },
                    ].map((faq, i) => (
                        <div key={i} className="space-y-2">
                            <h3 className="font-semibold text-foreground">
                                {faq.q}
                            </h3>

                            <p className="text-sm leading-relaxed text-muted-foreground">
                                {faq.a}
                            </p>
                        </div>
                    ))}
                </div>
            </div>
        </div>
    );
};

export default PricingPage;