import type {AxiosError} from "axios";

export type AuthStep =
    | "login"
    | "register"
    | "otp"
    | "forgot-password"
    | "reset-password"
    | "oauth-register";


export interface AuthFormProps {
    onForgot?: (response: object) => void;
    onLogin?: (response: object) => void;
    onRegister?: (response: object) => void;
    onError: (error: AxiosError) => void;
    onStepChange?: (step: AuthStep) => void;
    serverError?: AxiosError | null;
    otpId?: string | null;
    rememberMe?: boolean;
}
