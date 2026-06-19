export type MockSubscription = {
    id: string
    stripeSubscriptionId: string
    planName: string
    status: 'active' | 'trialing' | 'past_due' | 'canceled'
    interval: 'month' | 'year'
    amount: number
    currency: string

    currentPeriodStart: string
    currentPeriodEnd: string
    nextBillingDate: string

    cancelAtPeriodEnd: boolean
    canceledAt?: string | null
    trialEndsAt?: string | null

    paymentStatus: 'paid' | 'unpaid' | 'requires_action'

    usage: {
        usedStorageGb: number
        totalStorageGb: number

        usedApiCalls: number
        totalApiCalls: number

        usedTeamMembers: number
        totalTeamMembers: number
    }

    features: {
        teamMembers: number
        storage: string
        projects: string
        apiCalls: string
        aiAutomation: boolean
        prioritySupport: boolean
        advancedAnalytics: boolean
    }

    billingDetails: {
        billingEmail: string
        billingName: string
        country: string
        vatNumber?: string
    }
}

export const mockSubscription: MockSubscription = {
    id: 'sub_local_001',

    stripeSubscriptionId: 'sub_1RjYxSLkdIwHu7ixQ2Example',

    planName: 'Pro',

    status: 'active',

    interval: 'month',

    amount: 2900,

    currency: 'USD',

    currentPeriodStart: '2026-05-01T00:00:00Z',

    currentPeriodEnd: '2026-06-01T00:00:00Z',

    nextBillingDate: '2026-06-01T00:00:00Z',

    cancelAtPeriodEnd: false,

    canceledAt: null,

    trialEndsAt: null,

    paymentStatus: 'paid',

    usage: {
        usedStorageGb: 142,
        totalStorageGb: 250,

        usedApiCalls: 684220,
        totalApiCalls: 1000000,

        usedTeamMembers: 18,
        totalTeamMembers: 25
    },

    features: {
        teamMembers: 25,
        storage: '250 GB',
        projects: 'Unlimited',
        apiCalls: '1M/month',

        aiAutomation: true,
        prioritySupport: true,
        advancedAnalytics: true
    },

    billingDetails: {
        billingEmail: 'billing@acmecloud.io',
        billingName: 'Alex Morgan',
        country: 'United States',
        vatNumber: 'US-92838291'
    }
}