export type MockUser = {
    id: string
    customerId: string
    name: string
    email: string
    company: string
    phone: string
    location: string
    avatar?: string
    role: string
    memberSince: string
    emailVerified: boolean
    timezone: string
}

export type MockSubscription = {
    id: string
    planName: string
    status: 'active' | 'canceling' | 'past_due'
    amount: number
    currency: string
    interval: 'month' | 'year'
    nextBillingDate: string
    cancelAtPeriodEnd: boolean
    trialEndsAt?: string
    features: {
        teamMembers: number
        storage: string
        projects: string
        apiCalls: string
    }
}

export type MockPaymentMethod = {
    id: string
    brand: string
    last4: string
    expiryMonth: number
    expiryYear: number
    isDefault: boolean
}

export type MockInvoice = {
    id: string
    number: string
    amount: number
    currency: string
    status: 'paid' | 'pending' | 'failed'
    date: string
    hostedInvoiceUrl: string
    invoicePdf: string
}

export const mockUser: MockUser = {
    id: 'usr_01JY2D4X8K',
    customerId: 'cus_SaaS9xY7Qa21',
    name: 'Alex Morgan',
    email: 'alex.morgan@acmecloud.io',
    company: 'Acme Cloud Technologies',
    phone: '+1 (415) 555-0199',
    location: 'San Francisco, California',
    role: 'Founder & CEO',
    memberSince: '2023-04-18T12:00:00Z',
    emailVerified: true,
    timezone: 'America/Los_Angeles',
    avatar:
        'https://images.unsplash.com/photo-1500648767791-00dcc994a43e'
}

export const mockSubscription: MockSubscription = {
    id: 'sub_1QX9P4LkdIwHu7ix',
    planName: 'Pro',
    status: 'active',
    amount: 2900,
    currency: 'USD',
    interval: 'month',
    nextBillingDate: '2026-06-15T12:00:00Z',
    cancelAtPeriodEnd: false,
    features: {
        teamMembers: 25,
        storage: '250 GB',
        projects: 'Unlimited',
        apiCalls: '1M/month'
    }
}

export const mockPaymentMethods: MockPaymentMethod[] = [
    {
        id: 'pm_1',
        brand: 'Visa',
        last4: '4242',
        expiryMonth: 12,
        expiryYear: 2028,
        isDefault: true
    },
    {
        id: 'pm_2',
        brand: 'Mastercard',
        last4: '4444',
        expiryMonth: 8,
        expiryYear: 2027,
        isDefault: false
    },
    {
        id: 'pm_3',
        brand: 'American Express',
        last4: '3005',
        expiryMonth: 3,
        expiryYear: 2029,
        isDefault: false
    }
]

export const mockInvoices: MockInvoice[] = [
    {
        id: 'inv_001',
        number: 'INV-2026-001',
        amount: 2900,
        currency: 'USD',
        status: 'paid',
        date: '2026-05-15T12:00:00Z',
        hostedInvoiceUrl: '#',
        invoicePdf: '#'
    },
    {
        id: 'inv_002',
        number: 'INV-2026-002',
        amount: 2900,
        currency: 'USD',
        status: 'paid',
        date: '2026-04-15T12:00:00Z',
        hostedInvoiceUrl: '#',
        invoicePdf: '#'
    },
    {
        id: 'inv_003',
        number: 'INV-2026-003',
        amount: 2900,
        currency: 'USD',
        status: 'pending',
        date: '2026-03-15T12:00:00Z',
        hostedInvoiceUrl: '#',
        invoicePdf: '#'
    },
    {
        id: 'inv_004',
        number: 'INV-2026-004',
        amount: 2900,
        currency: 'USD',
        status: 'failed',
        date: '2026-02-15T12:00:00Z',
        hostedInvoiceUrl: '#',
        invoicePdf: '#'
    }
]