export type MockInvoice = {
    id: string
    stripeInvoiceId: string
    invoiceNumber: string

    amount: number
    subtotal: number
    tax: number

    currency: string

    status: 'paid' | 'pending' | 'failed' | 'draft'

    billingReason:
        | 'subscription_create'
        | 'subscription_cycle'
        | 'subscription_update'
        | 'manual'

    hostedInvoiceUrl: string
    invoicePdf: string

    customerName: string
    customerEmail: string

    planName: string

    paymentMethod: {
        brand: string
        last4: string
    }

    createdAt: string
    paidAt?: string | null
    dueDate?: string | null
}

export const mockInvoices: MockInvoice[] = [
    {
        id: 'invoice_local_001',

        stripeInvoiceId: 'in_1RjYzULkdIwHu7ixA001',

        invoiceNumber: 'INV-2026-0001',

        amount: 2900,
        subtotal: 2500,
        tax: 400,

        currency: 'USD',

        status: 'paid',

        billingReason: 'subscription_cycle',

        hostedInvoiceUrl:
            'https://invoice.stripe.com/i/acct_mock/test_invoice_001',

        invoicePdf:
            'https://pay.stripe.com/invoice/acct_mock/pdf_001',

        customerName: 'Alex Morgan',

        customerEmail: 'alex.morgan@acmecloud.io',

        planName: 'Pro',

        paymentMethod: {
            brand: 'Visa',
            last4: '4242'
        },

        createdAt: '2026-05-01T10:15:00Z',

        paidAt: '2026-05-01T10:16:12Z',

        dueDate: '2026-05-01T10:15:00Z'
    },

    {
        id: 'invoice_local_002',

        stripeInvoiceId: 'in_1RjYzULkdIwHu7ixA002',

        invoiceNumber: 'INV-2026-0002',

        amount: 2900,
        subtotal: 2500,
        tax: 400,

        currency: 'USD',

        status: 'paid',

        billingReason: 'subscription_cycle',

        hostedInvoiceUrl:
            'https://invoice.stripe.com/i/acct_mock/test_invoice_002',

        invoicePdf:
            'https://pay.stripe.com/invoice/acct_mock/pdf_002',

        customerName: 'Alex Morgan',

        customerEmail: 'alex.morgan@acmecloud.io',

        planName: 'Pro',

        paymentMethod: {
            brand: 'Mastercard',
            last4: '4444'
        },

        createdAt: '2026-04-01T09:00:00Z',

        paidAt: '2026-04-01T09:02:10Z',

        dueDate: '2026-04-01T09:00:00Z'
    },

    {
        id: 'invoice_local_003',

        stripeInvoiceId: 'in_1RjYzULkdIwHu7ixA003',

        invoiceNumber: 'INV-2026-0003',

        amount: 4900,
        subtotal: 4500,
        tax: 400,

        currency: 'USD',

        status: 'pending',

        billingReason: 'subscription_update',

        hostedInvoiceUrl:
            'https://invoice.stripe.com/i/acct_mock/test_invoice_003',

        invoicePdf:
            'https://pay.stripe.com/invoice/acct_mock/pdf_003',

        customerName: 'Alex Morgan',

        customerEmail: 'alex.morgan@acmecloud.io',

        planName: 'Enterprise',

        paymentMethod: {
            brand: 'Visa',
            last4: '4242'
        },

        createdAt: '2026-03-15T14:10:00Z',

        paidAt: null,

        dueDate: '2026-03-20T14:10:00Z'
    },

    {
        id: 'invoice_local_004',

        stripeInvoiceId: 'in_1RjYzULkdIwHu7ixA004',

        invoiceNumber: 'INV-2026-0004',

        amount: 2900,
        subtotal: 2500,
        tax: 400,

        currency: 'USD',

        status: 'failed',

        billingReason: 'subscription_cycle',

        hostedInvoiceUrl:
            'https://invoice.stripe.com/i/acct_mock/test_invoice_004',

        invoicePdf:
            'https://pay.stripe.com/invoice/acct_mock/pdf_004',

        customerName: 'Alex Morgan',

        customerEmail: 'alex.morgan@acmecloud.io',

        planName: 'Pro',

        paymentMethod: {
            brand: 'American Express',
            last4: '3005'
        },

        createdAt: '2026-02-01T08:00:00Z',

        paidAt: null,

        dueDate: '2026-02-05T08:00:00Z'
    }
]