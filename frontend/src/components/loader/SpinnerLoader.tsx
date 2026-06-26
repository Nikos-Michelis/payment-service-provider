import {Loader2} from "lucide-react";

const SpinnerLoader = () => {
    return (
        <div className="col-span-full flex justify-center items-center py-16">
            <Loader2 className="h-14 w-14 animate-spin text-muted-foreground" />
        </div>
    );
}

export default SpinnerLoader;