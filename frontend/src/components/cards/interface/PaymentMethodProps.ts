import type {PaymentType} from "react-svg-credit-card-payment-icons";

export interface PaymentMethodProps {
    type?: string;
    brand?: PaymentType;
    last4?: string;
    email?: string;
}