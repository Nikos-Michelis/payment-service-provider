import { useState } from "react";
import { Card, CardHeader, CardTitle, CardContent } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { Separator } from "@/components/ui/separator";
import {Loader2, Plus} from "lucide-react";
import ShippingRadio from "@/components/button/ShippingRadio.tsx";
import type {ShippingMethodId} from "@/context/interface/CartProps.ts";
import {useCreateMutation} from "@/services/mutations.ts";
import {toast} from "sonner";

interface OrderSummaryProps {
    subtotal: number;
    onCheckout?: () => void;
}

const BASE_URL = import.meta.env.VITE_BACKEND_BASE_URL;
const CHECKOUT_URL = `${BASE_URL}/stripe/payment/pay`;

const OrderCard = ({ subtotal }: OrderSummaryProps) => {
    const [shippingMethod, setShippingMethod] = useState<ShippingMethodId>("HOME_DELIVERY");
    const [discountCode, setDiscountCode] = useState("");

    const stripeCustomerPortalMutation = useCreateMutation({
        successMessage: undefined,
        showError: false
    });

    const { isPending } = stripeCustomerPortalMutation;

    const handleCheckout = () => {
        stripeCustomerPortalMutation.mutate(
            {
                url: CHECKOUT_URL,
                options: {
                    withBearer: true,
                    withIdempotency: true
                },
            },
            {
                onSuccess: (response) => {
                    redirectTo(response);
                },
                onError: (error) => {
                    toast.error(error.message ?? "Something went wrong");
                }
            }
        );
    };

    const redirectTo = (url:string) => {
        return window.location.assign(url);
    }

    const shippingCost = 0;
    const total = subtotal + shippingCost;

    return (
        <Card>
            <CardHeader>
                <CardTitle>Delivery</CardTitle>
            </CardHeader>
            <CardContent className="space-y-6">
                <ShippingRadio value={shippingMethod} onChange={setShippingMethod} />

                <Separator />

                <div className="space-y-2 text-sm">
                    <div className="flex justify-between">
                        <span className="text-muted-foreground">Subtotal</span>
                        <span className="font-medium">${subtotal.toFixed(2)}</span>
                    </div>
                    <div className="flex justify-between">
                        <p className="font-semibold">Total <span className="text-xs text-muted-foreground">(includes VAT)</span></p>
                        <span className="font-semibold">${total.toFixed(2)}</span>
                    </div>
                </div>

                <div className="relative">
                    <Input
                        placeholder="Have a discount code or gift card?"
                        value={discountCode}
                        onChange={(e) => setDiscountCode(e.target.value)}
                        className="pr-9"
                    />
                    <Plus className="absolute right-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
                </div>

                <Button className="w-full gap-2" size="lg" onClick={handleCheckout} disabled={isPending}>
                    {isPending && <Loader2 className="h-14 w-14 animate-spin text-muted-foreground" />}
                    Continue to checkout
                </Button>
            </CardContent>
        </Card>
    );
};

export default OrderCard;