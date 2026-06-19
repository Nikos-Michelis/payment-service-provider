import { AlertTriangle, ServerCrash, ShieldAlert } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Separator } from "@/components/ui/separator";

interface FallbackComponentProps {
    code?: number | string;
    heading?: string;
    message?: string;
    error?: string;
    onReset?: () => void;
}

const FallbackComponent = (
    {
        code = "",
        heading = "Something went wrong",
        message = "An unexpected error occurred.",
        error = "",
        onReset,
    }: FallbackComponentProps) => {
    const Icon = code === 500 ? ServerCrash
        : code === 403 ? ShieldAlert
            : AlertTriangle;

    return (
        <div className="flex flex-col items-center justify-center min-h-[60vh] text-center px-4">
            {code && (
                <h1 className="text-8xl font-extrabold text-muted-foreground/30 mb-4">
                    {code}
                </h1>
            )}

            <Icon className="w-12 h-12 text-destructive mb-4" />

            {heading && (
                <h2 className="text-2xl font-bold tracking-tight mb-2">
                    {heading}
                </h2>
            )}

            <Separator className="my-4 w-1/2" />

            {message && (
                <p className="text-muted-foreground text-sm max-w-md mb-2">
                    {message}
                </p>
            )}

            {error && (
                <p className="text-xs text-destructive font-mono bg-destructive/10 px-3 py-2 rounded-md max-w-md break-all">
                    {error}
                </p>
            )}

            <Separator className="my-4 w-1/2" />

            <div className="flex gap-3 mt-2">
                {onReset && (
                    <Button onClick={onReset} variant="default">
                        Try Again
                    </Button>
                )}
                <Button variant="outline" onClick={() => window.location.href = "/"}>
                    Go Home
                </Button>
            </div>
        </div>
    );
};

export default FallbackComponent;