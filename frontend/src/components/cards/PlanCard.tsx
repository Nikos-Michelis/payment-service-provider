import React from 'react';
import {Card, CardContent, CardFooter, CardHeader} from '@/components/ui/card.tsx';
import {Button} from '@/components/ui/button.tsx';
import {Badge} from '@/components/ui/badge.tsx';
import {Check, Star, TrendingUp, Zap} from 'lucide-react';
import {Separator} from "@/components/ui/separator.tsx";
import {useCreateMutation} from "@/services/mutations.ts";
import {toast} from "sonner";

export type BillingCycle = 'MONTH' | 'YEAR';

export interface Price {
    stripe_price_id: string;
    amount: number;
    currency: string;
    billing_cycle: BillingCycle;
}

export interface Feature {
    id: string;
    name: string;
}

export interface PlanCardProps {
    product_id: string;
    name: string;
    type: string;
    description: string;
    prices: Price[];
    features: Feature[];
    billingCycle: BillingCycle;
}

interface SessionResponse {
    session_url: string;
}

const BASE_URL = import.meta.env.VITE_BACKEND_BASE_URL;
const CREATE_SUBSCRIPTION_URL = `${BASE_URL}/stripe/payment/subscription/checkout`;

const PlanCard: React.FC<PlanCardProps> = (
    {
        product_id,
        name,
        type,
        description,
        features,
        prices,
        billingCycle
    }) => {
    const isPopular = name === 'Professional';
    const selectedPrice = prices.find(p => p.billing_cycle === billingCycle);
    const priceId = selectedPrice?.stripe_price_id;
    const amount = selectedPrice?.amount ?? 0;
    const currency = selectedPrice?.currency ?? 'USD';

    const stripeCustomerPortalMutation = useCreateMutation({
        successMessage: undefined,
        showError: false
    });

    const handleSelectPlan = () => {
        stripeCustomerPortalMutation.mutate(
            {
                url: CREATE_SUBSCRIPTION_URL,
                data: { productId: product_id, priceId: priceId },
                options: {
                    withCredentials: true,
                    withBearer: true,
                    withIdempotency: true
                },
            },
            {
                onSuccess: (response:SessionResponse) => {
                    redirectTo(response?.session_url);
                },
                onError: (error) => {
                    toast.error(error.message ?? "Something went wrong");
                }
            }
        );
    };

    const formatPrice = (amount: number, currency: string): string =>
        new Intl.NumberFormat('en-US', {
            style: 'currency',
            currency: currency.toUpperCase(),
        }).format(amount / 100);

    const getPlanIcon = (type: string) => {
        switch (type) {
            case 'BASIC':      return <Zap className="w-5 h-5"/>;
            case 'PRO': return <TrendingUp className="w-5 h-5"/>;
            case 'UNLIMITED':   return <Star className="w-5 h-5"/>;
            default:             return <Zap className="w-5 h-5"/>;
        }
    };

    const redirectTo = (url:string) => {
        return window.location.assign(url);
    }

    return (
        <Card className={`relative flex flex-col transition-all hover:-translate-y-1 hover:shadow-lg ${
            isPopular ? "border-primary border-2 shadow-xl scale-105" : ""}`}>

            {isPopular && (
                <div className="absolute -top-4 left-1/2 -translate-x-1/2">
                    <Badge className="bg-primary text-primary-foreground px-4 py-1">
                        Most Popular
                    </Badge>
                </div>
            )}

            <CardHeader className="space-y-3">
                <div className={`w-10 h-10 rounded-lg flex items-center justify-center ${
                    isPopular ? "bg-primary text-primary-foreground" : "bg-secondary text-secondary-foreground"}`}>
                    {getPlanIcon(type)}
                </div>

                <div>
                    <h3 className="text-xl font-bold text-foreground">{name}</h3>
                    <p className="mt-1 text-sm text-muted-foreground">{description}</p>
                </div>

                <div>
                    <div className="flex items-baseline gap-1">
                        <span className="text-4xl font-bold text-foreground">
                            {formatPrice(amount, currency)}
                        </span>
                        <span className="text-sm text-muted-foreground">
                            /{billingCycle === "MONTH" ? "mo" : "yr"}
                        </span>
                    </div>

                    {billingCycle === "YEAR" && (
                        <p className="mt-1 text-xs text-muted-foreground">
                            {formatPrice(amount / 12, currency)} per month, billed annually
                        </p>
                    )}
                </div>
            </CardHeader>

            <Separator/>

            <CardContent className="flex-1">
                <ul className="space-y-3">
                    {features.map((feature, index) => (
                        <li key={index} className="flex items-center gap-2 text-foreground">
                            <Check className="h-4 w-4 shrink-0 text-primary"/>
                            {feature.name}
                        </li>
                    ))}
                </ul>
            </CardContent>

            <CardFooter>
                <Button
                    onClick={handleSelectPlan}
                    disabled={stripeCustomerPortalMutation.isPending}
                    variant={isPopular ? "default" : "outline"}
                    className={`w-full ${isPopular ? "bg-primary text-primary-foreground hover:bg-primary/90" : ""}`}
                >
                    {stripeCustomerPortalMutation.isPending ? "Redirecting..." : isPopular ? "Get Started Now" : "Choose Plan"}
                </Button>
            </CardFooter>
        </Card>
    );
};

export default PlanCard;