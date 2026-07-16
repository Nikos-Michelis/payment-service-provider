import PricingPage from "./pages/PricingPage.tsx";
import Profile from "./pages/Profile.tsx";
import PageLayout from "@/layout/PageLayout.tsx";
import {BrowserRouter, Navigate, Route, Routes} from "react-router";
import {Toaster} from "sonner";
import {ThemeProvider} from "@/context/ThemeProvider";
import {AuthProvider} from "@/context/AuthProvider.tsx";
import {GoogleOAuthProvider} from "@react-oauth/google";
import {ProtectedRoutes} from "@/layout/ProtectedRoutes.tsx";
import {Layout} from "lucide-react";
import CheckoutSuccess from "@/pages/CheckoutSuccess.tsx";
import {SubscriptionProvider} from "@/context/SubscriptionProvider.tsx";
import ProductsPage from "@/pages/ProductsPage.tsx";
import CartProvider from "@/context/CartProvider.tsx";
import CartPage from "@/pages/CartPage.tsx";

function App() {

    return (
        <GoogleOAuthProvider clientId={import.meta.env.VITE_GOOGLE_CLIENT_ID}>
            <AuthProvider>
                <SubscriptionProvider>
                    <CartProvider>
                        <ThemeProvider>
                            <BrowserRouter>
                                <Routes>
                                    <Route element={<PageLayout />}>
                                        <Route path="/" element={<Navigate to="/pricing" replace />}/>
                                        <Route path="/pricing" element={<PricingPage />} />
                                        <Route path="/profile" element={<ProtectedRoutes><Layout /><Profile /></ProtectedRoutes>} />
                                        <Route path="/Shop" element={<ProductsPage />} />
                                        <Route path="/cart" element={<CartPage />} />
                                        <Route path="/success" element={<CheckoutSuccess />} />
                                    </Route>
                                </Routes>
                                <Toaster />
                            </BrowserRouter>
                        </ThemeProvider>
                    </CartProvider>
                </SubscriptionProvider>
            </AuthProvider>
        </GoogleOAuthProvider>
    );
}

export default App;