import Header from "@/layout/Header";
import Footer from "@/layout/Footer";

import { ErrorBoundary } from "react-error-boundary";
import {Outlet, useLocation} from "react-router";

export default function PageLayout() {
    const location = useLocation();

    return (
        <>
            <Header />
            <main className="flex-1">
                <ErrorBoundary
                    fallback={<div>Something went wrong.</div>}
                    resetKeys={[location.key]}
                >
                    <Outlet />
                </ErrorBoundary>
            </main>
            <Footer />
        </>
    );
}