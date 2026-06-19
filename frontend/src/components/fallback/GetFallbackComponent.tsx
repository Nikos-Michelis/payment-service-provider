import FallbackComponent from "@/components/fallback/FallbackComponent.tsx";

interface FallbackProps {
    error?: {
        response?: {
            status?: number;
            data?: {
                error?: string;
                message?: string;
            };
        };
    };
    onReset?: () => void;
}

const ERROR_MAP: Record<number, { heading: string; message: string }> = {
    400: { heading: "Bad Request",          message: "The request was invalid." },
    401: { heading: "Unauthorized",         message: "You need to log in to access this." },
    403: { heading: "Forbidden",            message: "You don't have permission to access this." },
    404: { heading: "Page Not Found",       message: "The page you're looking for doesn't exist." },
    500: { heading: "Internal Server Error",message: "Something went wrong on our end." },
    503: { heading: "Service Unavailable",  message: "The service is temporarily unavailable." },
};

const GetFallbackComponent = ({ error, onReset }: FallbackProps) => {
    const status = error?.response?.status;
    const errorDetail = error?.response?.data?.error ?? error?.response?.data?.message;
    const config = ERROR_MAP[status] ?? {
        heading: "We'll be back soon!",
        message: "Our website is down for maintenance.",
    };

    return (
        <FallbackComponent
            code={status}
            heading={config.heading}
            message={config.message}
            error={errorDetail}
            onReset={onReset}
        />
    );
};

export default GetFallbackComponent;