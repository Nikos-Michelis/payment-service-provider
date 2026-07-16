import {Loader2, Wallet} from "lucide-react";
import {Card, CardContent, CardHeader, CardTitle} from "@/components/ui/card.tsx";
import type {PaymentMethodProps} from "@/components/cards/interface/PaymentMethodProps.ts";
import {Button} from "@/components/ui/button.tsx";
import {PaymentIcon} from "react-svg-credit-card-payment-icons";
import {useCreateMutation} from "@/services/mutations.ts";

interface SessionResponse {
    session_url: string;
}

const BRAND_LABELS: Record<string, string> = {
    visa: "Visa", mastercard: "Mastercard", amex: "Amex",
    discover: "Discover", jcb: "JCB", unionpay: "UnionPay",
};

const BRAND_ICONS: Record<string, string> = {
    visa: "Visa",
    mastercard: "Mastercard",
    amex: "Amex",
    discover: "discover",
    jcb: "jcb",
    unionpay: "nionpay",
};

const BASE_URL  = import.meta.env.VITE_BACKEND_BASE_URL;
const ACCOUNT_STRIPE_SETTINGS_URL = `${BASE_URL}/stripe/account/billing/setting`

export function PaymentCard({ type, brand, last4, email }: PaymentMethodProps ) {

    const accountSettingsMutation = useCreateMutation({
        successMessage: undefined
    });

    const handleStripeAccountSettings = () => {
        accountSettingsMutation.mutate(
            { data: {}, url: ACCOUNT_STRIPE_SETTINGS_URL, options: { withCredentials: true, withBearer: true, withIdempotency: true } },
            {
                onSuccess: (response:SessionResponse) => {
                    const sessionUrl: string = response?.session_url
                    redirectTo(sessionUrl);
                },
            }
        );
    };

    const redirectTo = (url:string) => {
        return window.location.assign(url);
    }

    const getLabel = (): string => {
        switch (type) {
            case "card":
                return `${BRAND_LABELS[brand] ?? brand} •••• ${last4}`;
            case "paypal":
                return `PayPal • ${email}`;
            case "apple_pay":
                return "Apple Pay";
            case "google_pay":
                return "Google Pay";
        }
    };

    const getIcon = () => {
        if (type === "card" && brand) {
            return (<PaymentIcon type={BRAND_ICONS[brand]} style={{ width: 38 }}/>);
        }

        if (type === "paypal") {
            return <PaymentIcon type="Paypal" style={{ width: 38 }} />;
        }

        if (type === "apple_pay") {
            return <PaymentIcon type="apple-pay" style={{ width: 38 }} />;
        }

        if (type === "google_pay") {
            return <PaymentIcon type="google-pay" style={{ width: 38 }} />;
        }

        return <Wallet className="h-4 w-4" />;
    };

    return (
        <Card>
            <CardHeader>
                <CardTitle>Payment</CardTitle>
            </CardHeader>
            <CardContent className="space-y-6">
                <div className="flex justify-between items-center">
                    <div className="flex items-center gap-3">
                        {getIcon()}
                        <span className="text-sm font-medium">{getLabel()}</span>
                    </div>
                    <Button variant="secondary" onClick={() => handleStripeAccountSettings()}>
                        {accountSettingsMutation.isPending && <Loader2 className="h-4 w-4 animate-spin"/>} Update
                    </Button>
                </div>
            </CardContent>
        </Card>
    );
}