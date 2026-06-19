import {Loader2} from "lucide-react";
import {Navigate} from "react-router";
import useAuth from "@/hooks/useAuth.ts";
import React from "react";

interface ProtectedRoutes {
    children: React.ReactNode;
}

export function ProtectedRoutes({ children } : ProtectedRoutes) {
    const { status } = useAuth();
    const { isFetching, isPending, isSuccess } = status;

    if (isFetching || isPending) {
        return (
            <div className="flex items-center justify-center min-h-screen w-full">
                <Loader2 className="h-14 w-14 animate-spin text-muted-foreground" />
            </div>
        );
    }

    if (!isFetching && !isSuccess) {
        return <Navigate to="/pricing" />;
    }

    return <>{children}</>;
}
