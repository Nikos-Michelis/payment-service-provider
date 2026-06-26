import { useSimpleQuery } from "@/services/queries.ts";
import type {Invoice} from "@/pages/interface/ProfileProps.ts";
import {Badge} from "@/components/ui/badge.tsx";
import {AlertCircle, CheckCircle, CreditCard, Download, FileText} from "lucide-react";
import {Button} from "@/components/ui/button.tsx";

const BASE_URL = import.meta.env.VITE_BACKEND_BASE_URL;
const ACCOUNT_STRIPE_INVOICES_URL = `${BASE_URL}/stripe/account/billing/invoices`;

const InvoiceList = () => {
    const invoiceQueryData = useSimpleQuery<Invoice[]>({
        url: ACCOUNT_STRIPE_INVOICES_URL,
        cacheKey: "invoices",
        queryOptions: {
            suspense: true
        },
        enableBoundary: false
    });

    const invoices = invoiceQueryData?.data ?? [];

    const formatCurrency = (amount: number, currency: string) =>
        new Intl.NumberFormat("en-US", {
            style: "currency",
            currency: currency ?? "USD",
        }).format(amount / 100);

    const redirectTo = (url: string) => window.location.assign(url);

    if (invoices.length === 0) {
        return (
            <div className="flex flex-col items-center justify-center py-12 text-center">
                <FileText className="w-12 h-12 text-muted-foreground mb-4"/>
                <h3 className="text-lg font-semibold mb-2">No Invoices Yet</h3>
                <p className="text-muted-foreground">
                    Your invoices will appear here once you have an active subscription.
                </p>
            </div>
        );
    }

    return (
        <div className="space-y-3">
            {invoices.map((invoice) => (
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
                                {formatCurrency(invoice.amountPaid, invoice.currency)}
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
                                    {invoice.status === "PAID" && <CheckCircle className="w-3 h-3"/>}
                                    {invoice.status === "FAILED" && <AlertCircle className="w-3 h-3"/>}
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
                                <CreditCard className="w-3 h-3 mr-1"/>
                                Pay Now
                            </Button>
                        )}
                        <Button
                            size="icon"
                            variant="ghost"
                            onClick={() => redirectTo(invoice.hostedInvoiceUrl)}
                            disabled={!invoice.hostedInvoiceUrl}
                        >
                            <Download className="w-4 h-4"/>
                        </Button>
                    </div>
                </div>
            ))}
        </div>
    );
};

export default InvoiceList;