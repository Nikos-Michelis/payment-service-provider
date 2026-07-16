import { NavLink } from "react-router";
import { Button } from "@/components/ui/button";
import { Separator } from "@/components/ui/separator";
import {Trash2, Mail, ShoppingCart} from "lucide-react";
import useCart from "@/hooks/useCart.ts";
import OrderCard from "@/components/cards/OrderCard";
import CartItem from "@/components/line/CartItem";
import SpinnerLoader from "@/components/loader/SpinnerLoader.tsx";

const CartPage = () => {
    const { items, totalItems, subtotal, status, clearCart } = useCart();
    const {isFetching, isPending} = status.query;
    console.log(status)
    if (isFetching || isPending) {
        return <SpinnerLoader/>
    }

    if (items.length === 0) {
        return (
            <div className="mx-auto flex max-w-6xl flex-col items-center justify-center gap-3 px-4 py-24 text-center">
                <ShoppingCart className="h-10 w-10 text-muted-foreground" />
                <p className="text-lg font-medium">Your cart is empty</p>
                <p className="text-sm text-muted-foreground">
                    Items you add to your cart will show up here.
                </p>
                <NavLink to="/shop">
                    <Button className="mt-2">Continue shopping</Button>
                </NavLink>
            </div>
        );
    }

    return (
        <div className="mx-auto grid max-w-6xl gap-8 px-4 py-8 lg:grid-cols-[1fr_360px]">
            <div>
                <div className="mb-2 flex items-center justify-between">
                    <h1 className="text-2xl font-semibold">
                        My cart <span className="text-base font-normal text-muted-foreground">{totalItems} items</span>
                    </h1>
                    <Button variant="link" className="gap-1 text-destructive" onClick={clearCart}>
                        <Trash2 className="h-4 w-4" />
                        Empty cart
                    </Button>
                </div>

                <Separator />

                <div className="max-h-160 divide-y overflow-y-auto pr-2">
                    {items.map((item) => (
                        <CartItem key={item.uuid} item={item} />
                    ))}
                </div>

                <Separator className="mb-6" />

                <div className="flex flex-col gap-3 sm:flex-row">
                    <NavLink to="/shop" className="flex-1">
                        <Button variant="outline" className="w-full">
                            Continue shopping
                        </Button>
                    </NavLink>
                    <Button variant="outline" className="flex-1 gap-2">
                        <Mail className="h-4 w-4" />
                        Email cart
                    </Button>
                </div>
            </div>

            <OrderCard subtotal={subtotal} />
        </div>
    );
};

export default CartPage;