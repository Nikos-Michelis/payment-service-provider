export interface CartItem {
    id: number;
    uuid: string;
    title: string;
    sku: string;
    price: number;
    discount: number;
    thumbnail_url: string;
    stock: number;
    quantity: number;
}

export type ShippingMethodId = "HOME_DELIVERY" | "STORE_PICKUP" | "LOCKER_PICKUP";

export interface ShippingOption {
    id: ShippingMethodId;
    label: string;
    cost: number;
    available: boolean;
}