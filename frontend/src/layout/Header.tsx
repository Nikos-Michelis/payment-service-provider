import { Button } from "@/components/ui/button";
import {
    NavigationMenu,
    NavigationMenuItem,
    NavigationMenuList,
    NavigationMenuLink,
} from "@/components/ui/navigation-menu";

import { Avatar, AvatarFallback } from "@/components/ui/avatar";
import { NavLink } from "react-router";
import {ThemeToggle} from "@/components/button/ThemeToggle.tsx";
import AuthModal from "@/components/modal/AuthModal.tsx";
import {useState} from "react";
import useAuth from "@/hooks/useAuth.ts";
import CartIcon from "@/components/CartIcon.tsx";

function Header() {
    const {user} = useAuth();
    const [open, setOpen] = useState<boolean>(false)
    return (
        <header className=" flex justify-center border-b bg-background/95 backdrop-blur supports-backdrop-filter:bg-background/60 sticky top-0 z-50">
            <div className="container flex h-16 items-center justify-between">
                <div className="flex items-center gap-8">
                    <h1 className="text-xl font-bold">
                        PayDay
                    </h1>
                    <NavigationMenu>
                        <NavigationMenuList className="flex gap-2">

                            <NavigationMenuItem>
                                <NavigationMenuLink asChild>
                                    <NavLink
                                        to="/pricing"
                                        className="px-3 py-2 text-sm font-medium rounded-md hover:bg-accent hover:text-accent-foreground transition-colors"
                                    >
                                        Pricing
                                    </NavLink>
                                </NavigationMenuLink>
                            </NavigationMenuItem>

                            <NavigationMenuItem>
                                <NavigationMenuLink asChild>
                                    <NavLink
                                        to="/Shop"
                                        className="px-3 py-2 text-sm font-medium rounded-md hover:bg-accent hover:text-accent-foreground transition-colors"
                                    >
                                        Shop
                                    </NavLink>
                                </NavigationMenuLink>
                            </NavigationMenuItem>

                        </NavigationMenuList>
                    </NavigationMenu>
                </div>
                <div className="flex items-center gap-3">
                    <ThemeToggle />
                    <CartIcon />
                    <Button variant="ghost">
                        Support
                    </Button>

                    {
                        !user ? (
                            <AuthModal open={open} onOpenChange={setOpen}/>
                        ) : (
                            <NavLink
                                to="/profile"
                            >
                                <Avatar>
                                    <AvatarFallback>AS</AvatarFallback>
                                </Avatar>
                            </NavLink>
                        )
                    }
                </div>
            </div>
        </header>
    );
}

export default Header;