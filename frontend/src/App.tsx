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

function App() {

    return (
        <GoogleOAuthProvider clientId={import.meta.env.VITE_GOOGLE_CLIENT_ID}>
            <AuthProvider>
                <ThemeProvider>
                    <BrowserRouter>
                        <Routes>
                            <Route element={<PageLayout />}>
                                <Route path="/" element={<Navigate to="/pricing" replace />}/>
                                <Route path="/pricing" element={<PricingPage />} />
                                <Route path="/dashboard" element={<ProtectedRoutes><Layout /><Profile /></ProtectedRoutes>} />
                                <Route path="/success" element={<CheckoutSuccess />} />
                            </Route>
                        </Routes>
                        <Toaster />
                    </BrowserRouter>
                </ThemeProvider>
            </AuthProvider>
        </GoogleOAuthProvider>
    );
}

export default App;