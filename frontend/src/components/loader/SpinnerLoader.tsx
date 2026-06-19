import {Loader2} from "lucide-react";

const SpinnerLoader = () => {
    return (
        <div className="flex items-center justify-center min-h-screen w-full">
            <Loader2 className="h-14 w-14 animate-spin text-muted-foreground" />
        </div>
    );
}

export default SpinnerLoader;