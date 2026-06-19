export type MockPaymentMethod = {
    id: string

    stripePaymentMethodId: string

    type: 'card'

    brand:
        | 'Visa'
        | 'Mastercard'
        | 'American Express'
        | 'Discover'

    last4: string

    expiryMonth: number
    expiryYear: number

    holderName: string

    country: string

    funding: 'credit' | 'debit'

    isDefault: boolean

    fingerprint: string

    createdAt: string
}

export const mockPaymentMethods: MockPaymentMethod[] = [
    {
        id: 'payment_method_local_001',

        stripePaymentMethodId: 'pm_1RjZ0ALkdIwHu7ixPm001',

        type: 'card',

        brand: 'Visa',

        last4: '4242',

        expiryMonth: 12,
        expiryYear: 2028,

        holderName: 'Alex Morgan',

        country: 'US',

        funding: 'credit',

        isDefault: true,

        fingerprint: 'Xt5EWLLDS7FJjR1c',

        createdAt: '2025-11-15T12:00:00Z'
    },

    {
        id: 'payment_method_local_002',

        stripePaymentMethodId: 'pm_1RjZ0ALkdIwHu7ixPm002',

        type: 'card',

        brand: 'Mastercard',

        last4: '4444',

        expiryMonth: 8,
        expiryYear: 2027,

        holderName: 'Alex Morgan',

        country: 'US',

        funding: 'debit',

        isDefault: false,

        fingerprint: 'S1kL90dXxP4YtQ2Z',

        createdAt: '2025-08-02T09:30:00Z'
    },

    {
        id: 'payment_method_local_003',

        stripePaymentMethodId: 'pm_1RjZ0ALkdIwHu7ixPm003',

        type: 'card',

        brand: 'American Express',

        last4: '3005',

        expiryMonth: 3,
        expiryYear: 2029,

        holderName: 'Alex Morgan',

        country: 'US',

        funding: 'credit',

        isDefault: false,

        fingerprint: 'Fp9LkP2xQ8vTn31B',

        createdAt: '2026-01-20T15:45:00Z'
    }
]