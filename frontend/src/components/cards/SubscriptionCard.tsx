import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card.tsx";
import { Badge } from "@/components/ui/badge.tsx";
import { CheckCircle, Loader2, Sparkles } from "lucide-react";
import { Separator } from "@/components/ui/separator.tsx";
import { Button } from "@/components/ui/button.tsx";
import { useCreateMutation } from "@/services/mutations.ts";
import { useNavigate } from "react-router";

const BASE_URL = import.meta.env.VITE_BACKEND_BASE_URL;
const CANCEL_SUBSCRIPTION_URL = `${BASE_URL}/stripe/payment/subscription/cancel`;

interface SessionResponse {
    session_url: string;
}

const SubscriptionCard = ({ subscription }) => {
    const navigate = useNavigate();

    const hasActiveSubscription = Boolean(subscription?.plan);

    const subscriptionCancelMutation = useCreateMutation({
        successMessage: undefined
    });

    const handleSubscriptionCancel = () => {
        subscriptionCancelMutation.mutate(
            { data: {}, url: CANCEL_SUBSCRIPTION_URL, options: { withCredentials: true, withBearer: true, withIdempotency: true } },
            {
                onSuccess: (response: SessionResponse) => {
                    const sessionUrl: string = response?.session_url;
                    redirectTo(sessionUrl);
                },
            }
        );
    };

    const formatCurrency = (amount: number, currency: string) =>
        new Intl.NumberFormat("en-US", {
            style: "currency",
            currency: currency ?? "USD",
        }).format(amount / 100);

    const redirectTo = (url: string) => window.location.assign(url);

    if (!hasActiveSubscription) {
        return (
            <Card>
                <CardHeader>
                    <CardTitle>Subscription</CardTitle>
                </CardHeader>
                <CardContent>
                    <div className="flex flex-col items-center text-center gap-3 py-6">
                        <div className="w-10 h-10 rounded-full bg-muted flex items-center justify-center">
                            <Sparkles className="w-5 h-5 text-muted-foreground" />
                        </div>
                        <div>
                            <p className="font-medium">No active subscription</p>
                            <p className="text-sm text-muted-foreground">
                                Upgrade to unlock premium features and get the most out of your account.
                            </p>
                        </div>
                        <Button onClick={() => navigate("/pricing")}>
                            View Plans
                        </Button>
                    </div>
                </CardContent>
            </Card>
        );
    }

    const { plan, interval, next_payment_date, amount, currency } = subscription;

    return (
        <Card>
            <CardHeader>
                <CardTitle>Active Subscription</CardTitle>
            </CardHeader>
            <CardContent className="space-y-6">
                <div className="flex items-center justify-between">
                    <div>
                        <div className="flex items-center gap-2 mb-2">
                            <Badge className="gap-1">
                                <CheckCircle className="w-3 h-3" />
                                Active
                            </Badge>
                        </div>
                        <p className="text-xl font-semibold">
                            {plan} Plan
                        </p>
                        <p className="text-muted-foreground">
                            {(amount && currency) && formatCurrency(amount, currency)} / {interval}
                        </p>
                    </div>
                    <div className="text-right">
                        <p className="text-sm text-muted-foreground">Next billing</p>
                        <p className="font-medium">
                            {next_payment_date}
                        </p>
                    </div>
                </div>
                <Separator />
                <div className="flex flex-wrap gap-3">
                    <Button onClick={() => navigate("/pricing")}>
                        Pricing
                    </Button>
                    <Button variant="destructive" onClick={handleSubscriptionCancel}>
                        {subscriptionCancelMutation?.isPending && <Loader2 className="h-4 w-4 animate-spin" />} Cancel Subscription
                    </Button>
                </div>
            </CardContent>
        </Card>
    );
};

export default SubscriptionCard;