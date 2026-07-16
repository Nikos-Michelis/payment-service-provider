import { useSearchParams, useNavigate } from "react-router";
import { CheckCircle, Loader2, Receipt } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardFooter, CardHeader } from "@/components/ui/card";
import { Separator } from "@/components/ui/separator";
import { Badge } from "@/components/ui/badge";
import {useParameterizedQuery} from "@/services/queries.ts";

interface CheckoutSession {
    sessionId: string;
    email: string;
    name: string;
    billingCycle: string;
    amountPaid: number;
    currency: string;
    interval: string;
    status: string;
}

const BASE_URL  = import.meta.env.VITE_BACKEND_BASE_URL;
const CHECKOUT_SUCCESS_URL = `${BASE_URL}/stripe/payment/session`

const CheckoutSuccess = () => {
    const [searchParams] = useSearchParams();
    const navigate = useNavigate();
    const sessionId = searchParams.get("session_id");

    const queryData = useParameterizedQuery<CheckoutSession>({
        url: `${CHECKOUT_SUCCESS_URL}/${sessionId}`,
        params: `invoices-${sessionId}`,
        cacheKey: "invoices",
        enableBoundary: false
    });

    const { isPending, isError } = queryData;
    const session: CheckoutSession | null = queryData?.data ?? null;

    if (!sessionId) {
        navigate("/");
        return;
    }

    if (isPending) {
        return (
            <div className="flex items-center justify-center min-h-screen">
                <Loader2 className="h-8 w-8 animate-spin text-muted-foreground" />
            </div>
        );
    }

    if (isError || !session) {
        return (
            <div className="flex items-center justify-center min-h-screen">
                <Card className="w-full max-w-md text-center p-6">
                    <p className="text-muted-foreground">Could not load session details.</p>
                    <Button className="mt-4" onClick={() => navigate("/")}>Go Home</Button>
                </Card>
            </div>
        );
    }

    return (
        <div className="flex items-center justify-center min-h-screen bg-muted/30 px-4">
            <Card className="w-full max-w-md shadow-lg">
                <CardHeader className="flex flex-col items-center text-center pt-8 pb-4">
                    <div className="w-16 h-16 rounded-full bg-green-100 flex items-center justify-center mb-4">
                        <CheckCircle className="w-8 h-8 text-green-600" />
                    </div>
                    <h1 className="text-2xl font-bold">Payment Successful!</h1>
                    <p className="text-muted-foreground text-sm mt-1">
                        Thank you for your purchase. Your subscription is now active.
                    </p>
                </CardHeader>

                <CardContent className="space-y-4 px-6">
                    <Separator />

                    <div className="space-y-3 text-sm">
                        <div className="flex justify-between">
                            <span className="text-muted-foreground">Email</span>
                            <span className="font-medium">{session.email}</span>
                        </div>
                        <div className="flex justify-between">
                            <span className="text-muted-foreground">Plan</span>
                            <span className="font-medium">{session.name}</span>
                        </div>
                        <div className="flex justify-between">
                            <span className="text-muted-foreground">Billing</span>
                            <Badge variant="secondary">
                                {session.billingCycle === "MONTH" ? "Monthly" : "Yearly"}
                            </Badge>
                        </div>
                        <div className="flex justify-between">
                            <span className="text-muted-foreground">Amount</span>
                            <span className="font-bold">
                                {new Intl.NumberFormat("en-US", {
                                    style: "currency",
                                    currency: session.currency.toUpperCase() ?? "USD",
                                }).format(session.amountPaid / 100)}
                            </span>
                        </div>
                    </div>

                    <Separator />

                    <p className="text-xs text-muted-foreground text-center">
                        A confirmation email has been sent to <span className="font-medium">{session?.email}</span>
                    </p>
                </CardContent>

                <CardFooter className="flex flex-col gap-2 px-6 pb-8">
                    <Button variant="outline" className="w-full" onClick={() => navigate("/profile")}>
                        <Receipt className="w-4 h-4 mr-2" />
                        View Invoices
                    </Button>
                </CardFooter>
            </Card>
        </div>
    );
};

export default CheckoutSuccess;