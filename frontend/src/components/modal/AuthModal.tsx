import React, { useState } from "react";
import { Button } from "@/components/ui/button";
import OAuthUsernameForm from "@/components/forms/OAuthUsernameForm.tsx";
import RegisterForm from "@/components/forms/RegisterForm.tsx";
import LoginForm from "@/components/forms/LoginForm.tsx";
import Modal from "@/components/modal/Modal.jsx.tsx";
import ForgotPasswordForm from "@/components/forms/ForgotPasswordForm.tsx";
import {Avatar, AvatarFallback} from "@/components/ui/avatar.tsx";
import {Separator} from "@/components/ui/separator.tsx";
import GoogleLoginButton from "@/components/button/GoogleLoginButton.tsx";
import VerifyOtp from "@/components/forms/VerifyOtp.tsx";
import type {AuthStep} from "@/components/forms/interface/AuthFormInterfae.ts";
import type {AxiosError} from "axios";


interface AuthModalProps {
    open: boolean;
    onOpenChange: (open: boolean) => void;
}

const titles: Record<AuthStep, string> = {
    login: "Welcome back",
    register: "Create an account",
    otp: "Verify your email",
    "forgot-password": "Forgot password",
    "reset-password": "Set new password",
    "oauth-register": "Choose a username",
};

const AuthModal = ({ open, onOpenChange }: AuthModalProps) => {
    const [step, setStep] = useState<AuthStep>("login");
    const [serverError, setServerError] = useState<AxiosError | null>(null);
    const [otpId, setOtpId] = useState<string | null>(null);


    const onStepChange = (s: AuthStep) => {
        setServerError(null);
        setStep(s);
    };

    const showBack = !['login'].includes(step);

    const onLoginSuccess = (response) => {
        setOtpId(response.token);
        setStep("otp");
    }

    const onClose = () => {
        setServerError(null);
        setStep("login");
    }

    return (
        <Modal open={open} onOpenChange={onOpenChange}>
            <Modal.Button className="navbar__user-link btn--transparent">
                <Avatar>
                    <AvatarFallback>AS</AvatarFallback>
                </Avatar>
            </Modal.Button>
            <Modal.Content
                title={titles[step]}
                showBack={showBack}
                onBack={() => setStep("login")}
            >
                <div className="px-8 pb-8 space-y-5">
                    { step === "login" && (
                        <>
                            <LoginForm
                                onForgot={() => onStepChange("forgot-password")}
                                onLogin={(response) => onLoginSuccess(response)}
                                onError={(error) => setServerError(error)}
                                serverError={serverError}
                            />
                            <div className="dialog__info fs-small-300">
                                <div className="flex items-center gap-3 my-6">
                                    <Separator className="flex-1" />
                                    <span className="text-xs text-muted-foreground">or</span>
                                    <Separator className="flex-1" />
                                </div>
                                <div className="flex justify-center">
                                    <div className="overflow-hidden rounded-2xl border border-border shadow-sm">
                                        <GoogleLoginButton
                                            onStepChange={onStepChange}
                                            onError={(error) => setServerError(error)}
                                            serverError={serverError}
                                        />
                                    </div>
                                </div>
                                <div className="text-center my-6">
                                    You can create your account
                                    <Button
                                        variant="link"
                                        className="text-sm font-medium text-primary hover:underline underline-offset-4 transition-colors"
                                        onClick={() => onStepChange("register")}>
                                        here
                                    </Button>
                                </div>
                            </div>
                        </>
                    )}

                    { step === "register" && (
                        <RegisterForm
                            onRegister={() => onStepChange("login")}
                            onError={(error) => setServerError(error)}
                            serverError={serverError}
                        />
                    )}

                    { step === "otp" && (
                        <div className="space-y-6">
                           <VerifyOtp
                               otpId={otpId}
                               rememberMe={false}
                               onError={(error) => setServerError(error)}
                               serverError={serverError}
                           />
                        </div>
                    )}

                    { step === "forgot-password" && (
                        <ForgotPasswordForm
                            onStepChange={() => onStepChange("reset-password") }
                            onError={(error) => setServerError(error)}
                            serverError={serverError}
                        />
                    )}

                    { step === "reset-password" &&
                        <div className="">
                            <p>You will soon receive a link to reset your password via email. Don&#39;t forget to check your inbox!</p>
                        </div>
                    }

                    { step === "oauth-register" && (
                        <OAuthUsernameForm
                            onStepChange={() => onStepChange("forgot-password")}
                            onError={(error) => setServerError(error)}
                            serverError={serverError}
                        />
                    )}
                </div>
            </Modal.Content>
        </Modal>
    );
};
export default AuthModal