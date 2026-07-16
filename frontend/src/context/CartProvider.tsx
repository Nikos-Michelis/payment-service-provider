import {createContext, useMemo, type ReactNode, useCallback} from "react";
import type { CartItem } from "@/context/interface/CartProps.ts";
import useAuth from "@/hooks/useAuth.ts";
import {useCreateMutation, useDeleteMutation, usePatchMutation} from "@/services/mutations.ts";
import {useSimpleQuery} from "@/services/queries.ts";

const BASE_URL  = import.meta.env.VITE_BACKEND_BASE_URL;
const CART_URL = `${BASE_URL}/shop/cart/items`

interface CartContextValue {
    items: CartItem[];
    totalItems: number;
    subtotal: number;
    status: Record<string, boolean>;
    addItem: (sku: string, quantity?: number) => void;
    removeItem: (uuid: string) => void;
    updateQuantity: (uuid: string, sku: string, quantity: number) => void;
    clearCart: () => void;
}

export const CartContext = createContext<CartContextValue | undefined>(undefined);

const CartProvider = ({ children }: { children: ReactNode }) => {
    const {user} = useAuth();

    const queryData = useSimpleQuery({
        url: `${CART_URL}`,
        cacheKey: "cart",
        options: { withBearer: true },
        queryOptions: {
            enabled: !!user
        },
    });

    const cart = queryData?.data ?? {};
    const items = cart?.cartItems ?? [];

    const addItemMutation = useCreateMutation({
        successMessage: "Item added successfully!",
        queryKeysToInvalidate: [["cart"]],
    });

    const updateItemQuantityMutation = usePatchMutation({
        successMessage: "Item quantity updated successfully!",
        queryKeysToInvalidate: [["cart"]],
    });

    const removeItemMutation = useDeleteMutation({
        successMessage: "Item removed successfully!",
        queryKeysToInvalidate: [["cart"]]
    });

    const clearCartMutation = useDeleteMutation({
        successMessage: "Cart clear successfully!",
        queryKeysToInvalidate: [["cart"]]
    });

    const handleAddItem = useCallback((sku: string, quantity: number) => {
        addItemMutation.mutate({ data: { sku: sku, quantity: quantity }, url:  CART_URL, options: { withBearer: true }});
    }, [addItemMutation]);

    const handleUpdateItem = useCallback(( orderLineUUID: string, sku:string, quantity: number ) => {
        updateItemQuantityMutation.mutate({ data: { sku: sku, quantity: quantity }, url: `${CART_URL}/${orderLineUUID}`, options: { withBearer: true }});
    }, [updateItemQuantityMutation]);

    const handleRemoveItem = useCallback((orderLineUUID: string) => {
        removeItemMutation.mutate({ url: `${CART_URL}/${orderLineUUID}`, options: { withBearer: true } });
    }, [removeItemMutation]);

    const handleClearCart = useCallback(() => {
        clearCartMutation.mutate({ url:  CART_URL, options: { withBearer: true }});
    }, [clearCartMutation]);

    const addItem = useCallback((sku: string, quantity: number = 1) => { handleAddItem(sku, quantity); }, [handleAddItem]);
    const removeItem = useCallback((uuid: string) => { handleRemoveItem(uuid); }, [handleRemoveItem]);
    const updateQuantity = useCallback((uuid: string, sku: string, quantity: number) => { handleUpdateItem(uuid, sku, quantity); }, [handleUpdateItem]);
    const clearCart = useCallback(() => {handleClearCart()}, [handleClearCart]);

    const value = useMemo<CartContextValue>(() => {
        const totalItems = cart?.totalItems ?? 0;
        const subtotal = cart?.subtotal ?? 0;
        const status = {
            query: {
                isPending: queryData.isPending,
                isFetching: queryData.isFetching
            }
        };
        return {
            items,
            totalItems,
            subtotal,
            status,
            addItem,
            removeItem,
            updateQuantity,
            clearCart,
        };
    }, [cart, items, queryData, addItem, removeItem, updateQuantity, clearCart]);

    return <CartContext.Provider value={value}>{children}</CartContext.Provider>;
};
export default CartProvider