import React from "react";
import {Download, CheckCircle, AlertCircle, LogOut, Loader2, FileText, CreditCard} from "lucide-react";

import {Button} from "@/components/ui/button";
import {Card, CardContent, CardHeader, CardTitle} from "@/components/ui/card";
import {Badge} from "@/components/ui/badge";
import {Separator} from "@/components/ui/separator";

import useAuth from "@/hooks/useAuth.ts";
import {useCreateMutation} from "@/services/mutations.ts";
import {useNavigate} from "react-router";
import {useParameterizedQuery} from "@/services/queries.ts";
import type {Invoice, Subscription} from "@/pages/interface/ProfileProps.ts";
import {PaymentCard} from "@/components/cards/PaymentMethodCard.tsx";
import {Avatar, AvatarFallback, AvatarImage} from "@/components/ui/avatar.tsx";

interface SessionResponse {
    session_url: string;
}

const BASE_URL  = import.meta.env.VITE_BACKEND_BASE_URL;
const CANCEL_SUBSCRIPTION_URL = `${BASE_URL}/stripe/payment/subscription/cancel`
const ACCOUNT_STRIPE_INVOICES_URL = `${BASE_URL}/stripe/account/billing/invoices`
const SUBSCRIPTION_URL = `${BASE_URL}/stripe/payment/subscription/list`


const Profile: React.FC = () => {
    const {logout, user, status} = useAuth();
    const navigate = useNavigate();

    const {isPending} = status;
    const {id, username, email} = user;

    const formatCurrency = (amount: number, currency:string) =>
        new Intl.NumberFormat("en-US", {
            style: "currency",
            currency: currency ?? "USD",
        }).format(amount / 100);


    const subscriptionCancelMutation = useCreateMutation({
        successMessage: undefined
    });

    const handleSubscriptionCancel = () => {
        subscriptionCancelMutation.mutate(
            { data: {}, url: CANCEL_SUBSCRIPTION_URL, options: { withCredentials: true, withBearer: true, withIdempotency: true } },
            {
                onSuccess: (response:SessionResponse) => {
                    const sessionUrl: string = response?.session_url
                    redirectTo(sessionUrl);
                },
            }
        );
    };

    const invoiceQueryData = useParameterizedQuery<Invoice[]>({
        url: `${ACCOUNT_STRIPE_INVOICES_URL}`,
        params: `invoices-${id}`,
        cacheKey: "invoices",
        queryOptions:{
            enabled: !!user,
            suspense: true

        },
        enableBoundary: false
    });

    const invoices = invoiceQueryData?.data ?? [];

    const subscriptionQueryData = useParameterizedQuery<Subscription[]>({
        url: `${SUBSCRIPTION_URL}`,
        params: `subscription-${id}`,
        cacheKey: "subscription",
        queryOptions: {
            enabled: !!user,
            suspense: true
        },
        enableBoundary: false
    });

    const subscription = subscriptionQueryData?.data?.[0];
    const { plan, interval, next_payment_date, amount, currency, payment_method } = subscription ?? {};

    const redirectTo = (url:string) => {
        return window.location.assign(url);
    }

    const getInitials = (name: string) =>
        name.split(" ").map(n => n[0]).join("").toUpperCase().slice(0, 2);

    return (
        <div className="max-w-6xl mx-auto px-6 py-10 space-y-8">
            <div>
                <h1 className="text-3xl font-bold">Profile</h1>
                <p className="text-muted-foreground">
                    Manage your information, subscription and billing details
                </p>
            </div>

            <Card>
                <CardHeader>
                    <CardTitle>User Information</CardTitle>
                </CardHeader>

                <CardContent className="flex items-center justify-between">
                    <div className="flex items-center gap-2">
                        <Avatar size="lg">
                            <AvatarImage src="#" alt={username} />
                            <AvatarFallback>{getInitials(username)}</AvatarFallback>
                        </Avatar>

                        <div>
                            <p className="font-semibold">{username}</p>
                            <p className="text-sm text-muted-foreground">
                                {email}
                            </p>
                        </div>
                    </div>
                    <div className="flex flex-col">
                        <Button variant="outline">Edit Profile</Button>
                        <Button
                            className="my-2"
                            variant="outline"
                            onClick={() => logout()}
                        >
                            Logout
                            {isPending
                                ? <Loader2 className="h-4 w-4 animate-spin"/>
                                : <LogOut className="h-4 w-4"/>
                            }
                        </Button>
                    </div>
                </CardContent>
            </Card>

            <Card>
                <CardHeader>
                    <CardTitle>Active Subscription</CardTitle>
                </CardHeader>
                <CardContent className="space-y-6">
                    {subscription ? (
                        <>
                            <div className="flex items-center justify-between">
                                <div>
                                    <div className="flex items-center gap-2 mb-2">
                                        <Badge className="gap-1">
                                            <CheckCircle className="w-3 h-3"/>
                                            Active
                                        </Badge>
                                    </div>
                                    <p className="text-xl font-semibold">
                                        {plan} Plan
                                    </p>
                                    <p className="text-muted-foreground">
                                        {( amount && currency ) && formatCurrency(amount, currency)} / {interval}
                                    </p>
                                </div>
                                <div className="text-right">
                                    <p className="text-sm text-muted-foreground">Next billing</p>
                                    <p className="font-medium">
                                        {next_payment_date}
                                    </p>
                                </div>
                            </div>
                            <Separator/>
                            <div className="flex flex-wrap gap-3">
                                <Button onClick={() => navigate("/pricing")}>
                                    Pricing
                                </Button>
                                <Button variant="destructive" onClick={handleSubscriptionCancel}>
                                    { subscriptionCancelMutation?.isPending && <Loader2 className="h-4 w-4 animate-spin"/> } Cancel Subscription
                                </Button>
                            </div>
                        </>
                    ) : (
                        <div className="flex flex-col items-center justify-center py-12 text-center">
                            <AlertCircle className="w-12 h-12 text-muted-foreground mb-4" />
                            <h3 className="text-lg font-semibold mb-2">No Active Subscription</h3>
                            <p className="text-muted-foreground mb-6">
                                You don't have an active subscription. Choose a plan to get started.
                            </p>
                            <Button onClick={() => navigate("/pricing")}>
                                View Plans
                            </Button>
                        </div>
                    )}
                </CardContent>
            </Card>

            <PaymentCard {...payment_method}/>

            <Card>
                <CardHeader className="flex flex-row items-center justify-between">
                    <CardTitle>Invoices</CardTitle>
                </CardHeader>
                <CardContent className="space-y-3">
                    {invoices?.length > 0 ? (
                        invoices.map((invoice) => (
                            <div
                                key={invoice.invoiceStripeId}
                                className="flex items-center justify-between p-4 border rounded-lg"
                            >
                                    <div className="flex items-center gap-4">
                                        <div className="w-20 h-10 rounded-md bg-muted flex items-center justify-center">
                                        <span className="text-xs font-bold">
                                            {new Date(invoice.invoiceCreatedAt).toLocaleDateString("en-US", {
                                                month: "short",
                                                day: "numeric",
                                            })}
                                        </span>
                                    </div>
                                    <div>
                                        <p className="text-xs text-muted-foreground font-mono">
                                            #{invoice.invoiceStripeId}
                                        </p>
                                        <p className="font-medium">
                                            {formatCurrency(invoice?.amountPaid, invoice?.currency)}
                                        </p>
                                        <div className="flex items-center gap-2 text-sm text-muted-foreground">
                                        <span>
                                            {new Date(invoice.finalizedAt).toLocaleDateString("en-US", {
                                                year: "numeric",
                                                month: "short",
                                                day: "numeric",
                                            })}
                                        </span>
                                            <Badge
                                                variant={
                                                    invoice.status === "PAID" ? "default"
                                                        : invoice.status === "PENDING" ? "secondary"
                                                            : "destructive"
                                                }
                                                className="gap-1"
                                            >
                                                {invoice.status === "PAID" && <CheckCircle className="w-3 h-3" />}
                                                {invoice.status === "FAILED" && <AlertCircle className="w-3 h-3" />}
                                                {invoice.status}
                                            </Badge>
                                        </div>
                                    </div>
                                </div>
                                <div className="flex items-center gap-2">
                                    {invoice.status !== "PAID" && invoice.hostedInvoiceUrl && (
                                        <Button
                                            size="sm"
                                            variant="destructive"
                                            onClick={() => redirectTo(invoice.hostedInvoiceUrl)}
                                        >
                                            <CreditCard className="w-3 h-3 mr-1" />
                                            Pay Now
                                        </Button>
                                    )}
                                    <Button
                                        size="icon"
                                        variant="ghost"
                                        onClick={() => redirectTo(invoice?.hostedInvoiceUrl)}
                                        disabled={!invoice.hostedInvoiceUrl}
                                    >
                                        <Download className="w-4 h-4" />
                                    </Button>
                                </div>
                            </div>
                        ))
                    ) : (
                        <div className="flex flex-col items-center justify-center py-12 text-center">
                            <FileText className="w-12 h-12 text-muted-foreground mb-4" />
                            <h3 className="text-lg font-semibold mb-2">No Invoices Yet</h3>
                            <p className="text-muted-foreground">
                                Your invoices will appear here once you have an active subscription.
                            </p>
                        </div>
                    )}
                </CardContent>
            </Card>
        </div>
    );
};

export default Profile;