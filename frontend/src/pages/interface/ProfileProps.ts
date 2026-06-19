export interface Invoice {
    invoiceStripeId: string;
    subscriptionId: string;
    amountPaid: number;
    currency: string;
    status: "PAID" | "PENDING" | "FAILED";
    billingReason: "SUBSCRIPTION_CREATE" | "SUBSCRIPTION_CYCLE" | "SUBSCRIPTION_UPDATE" | "MANUAL";
    hostedInvoiceUrl: string | "";
    nextPaymentAttempt: string | "";
    invoiceCreatedAt: string;
    finalizedAt: string;
    createdAt: string;
    updatedAt: string;
}

export interface Subscription {
    product: string;
    plan: string;
    subscription_id: string;
    interval: "month" | "year";
    subscribed_on: string;
    next_payment_date: string | "";
    amount: number;
    currency: string;
    payment_method: Record<string, string>;
}