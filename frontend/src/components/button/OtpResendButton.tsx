import { Button } from "@/components/ui/button.tsx";
import useCountDown from "@/hooks/useCountDown.tsx";
import { Loader2 } from "lucide-react";

interface OtpResendButtonProps {
    handleOtpResend: () => void;
    status: {
        isPending: boolean;
        isError: boolean;
    };
    delay: number;
}

const OtpResendButton = ({ handleOtpResend, status, delay }: OtpResendButtonProps) => {
    const { seconds } = useCountDown(delay, 1000);
    const isDisabled = seconds > 0;
    const { isPending } = status;

    if (isPending) {
        return (
            <Button type="button" variant="link" className="p-0 h-auto text-sm" disabled>
                <Loader2 className="h-4 w-4 animate-spin" />
            </Button>
        );
    }

    if (isDisabled) {
        return (
            <span className="text-sm text-muted-foreground">
                Resend in {seconds}s
            </span>
        );
    }

    return (
        <Button
            type="button"
            variant="link"
            className="p-0 h-auto text-sm"
            onClick={handleOtpResend}
        >
            Resend OTP
        </Button>
    );
};

export default OtpResendButton;