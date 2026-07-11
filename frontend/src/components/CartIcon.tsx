import { Button } from "@/components/ui/button";
import { ShoppingCart } from "lucide-react";
import { NavLink } from "react-router";
import useCart from "@/hooks/useCart.ts";

const CartIcon = () => {
    const { totalItems } = useCart();

    return (
        <NavLink to="/cart">
            <Button variant="ghost" size="icon" className="relative" aria-label="View cart">
                <ShoppingCart className="h-5 w-5" />
                {totalItems > 0 && (
                    <span className="absolute -right-1 -top-1 flex h-4 min-w-4 items-center justify-center rounded-full bg-primary px-1 text-[10px] font-medium text-primary-foreground">
                        {totalItems > 99 ? "99+" : totalItems}
                    </span>
                )}
            </Button>
        </NavLink>
    );
};

export default CartIcon;