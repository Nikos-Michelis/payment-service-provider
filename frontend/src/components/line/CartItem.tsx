import { Button } from "@/components/ui/button";
import { Minus, Plus, Heart, Trash2, ImageOff } from "lucide-react";
import useCart from "@/hooks/useCart.ts";
import type {CartItem} from "@/context/interface/CartProps.ts";

interface CartItemRowProps {
    item: CartItem;
}

const CartItem = ({ item }: CartItemRowProps) => {
    const { updateQuantity, removeItem } = useCart();
    const { uuid, title, sku, price, discount, thumbnail_url, quantity, stock } = item;

    const hasDiscount = discount > 0;
    const unitPrice = hasDiscount ? price * (1 - discount/ 100) : price;
    const lineTotal = unitPrice * quantity;

    return (
        <div className="flex flex-col gap-4 py-4 sm:flex-row sm:items-start">
            <div className="h-24 w-24 shrink-0 overflow-hidden rounded-md border bg-muted">
                {thumbnail_url ? (
                    <img src={thumbnail_url} alt={title} className="h-full w-full object-cover" />
                ) : (
                    <div className="flex h-full w-full items-center justify-center text-muted-foreground">
                        <ImageOff className="h-6 w-6" />
                    </div>
                )}
            </div>

            <div className="flex flex-1 flex-col gap-1">
                <p className="text-sm font-medium">{title}</p>
                <p className="text-xs text-muted-foreground">SKU: {sku}</p>

                <div className="mt-2 flex items-center gap-2">
                    <Button
                        variant="outline"
                        size="icon"
                        className="h-7 w-7"
                        onClick={() => updateQuantity(uuid, sku, quantity - 1)}
                        disabled={quantity <= 1}
                    >
                        <Minus className="h-3 w-3" />
                    </Button>
                    <span className="w-6 text-center text-sm font-medium">{quantity}</span>
                    <Button
                        variant="outline"
                        size="icon"
                        className="h-7 w-7"
                        onClick={() => updateQuantity(uuid, sku,  quantity + 1)}
                        disabled={quantity >= stock}
                    >
                        <Plus className="h-3 w-3" />
                    </Button>
                </div>
            </div>

            <div className="flex items-start justify-between gap-4 sm:flex-col sm:items-end">
                <div className="text-right">
                    <p className="text-sm font-semibold">${lineTotal.toFixed(2)}</p>
                    {hasDiscount && (
                        <p className="text-xs text-muted-foreground line-through">
                            ${(price * quantity).toFixed(2)}
                        </p>
                    )}
                </div>
                <div className="flex gap-1">
                    <Button variant="ghost" size="icon" aria-label="Save for later">
                        <Heart className="h-4 w-4" />
                    </Button>
                    <Button
                        variant="ghost"
                        size="icon"
                        aria-label="Remove from cart"
                        onClick={() => removeItem(uuid)}
                    >
                        <Trash2 className="h-4 w-4" />
                    </Button>
                </div>
            </div>
        </div>
    );
};

export default CartItem;