import { Controller, useForm } from "react-hook-form";
import { z } from "zod";
import { zodResolver } from "@hookform/resolvers/zod";
import {
    InputOTP,
    InputOTPGroup,
    InputOTPSlot,
} from "@/components/ui/input-otp";
import { Button } from "@/components/ui/button";
import { Field, FieldError, FieldGroup, FieldLabel } from "@/components/ui/field";
import React, {useEffect} from "react";
import {useCreateMutation} from "@/services/mutations.ts";
import OtpResendButton from "@/components/button/OtpResendButton.tsx";
import {useQueryClient} from "@tanstack/react-query";
import useAuth from "@/hooks/useAuth.ts";
import type {AuthFormProps} from "@/components/forms/interface/AuthFormInterfae.ts";

const otpSchema = z.object({
    otp: z.string().length(6, { message: "OTP must be 6 digits" }),
    token: z.string(),
    rememberMe: z.boolean(),
});

const BASE_URL  = import.meta.env.VITE_BACKEND_BASE_URL;
const RESEND_OTP_URL = `${BASE_URL}/auth/resend-otp`
const VERIFY_OTP_URL = `${BASE_URL}/auth/verify-otp`


const VerifyOtp = ({otpId, rememberMe, onError} : AuthFormProps) => {
    const { setJwtToken } = useAuth();
    const queryClient = useQueryClient();

    const form = useForm<z.infer<typeof otpSchema>>({
        defaultValues: { otp: "", token: otpId ?? "", rememberMe: rememberMe ?? false },
        resolver: zodResolver(otpSchema),
    });
    const { setValue } = form;

    useEffect(() => {
        if (otpId) setValue("token", otpId);
        if (rememberMe) setValue("rememberMe", rememberMe);

    }, [otpId, rememberMe, setValue]);


    const otpVerificationMutation = useCreateMutation({
        successMessage: undefined
    });

    const resendMutation = useCreateMutation({
        successMessage: "OTP resent successfully."
    });

    const handleOtpResend = () => {
        resendMutation.mutate(
            { data: { token: otpId }, url: RESEND_OTP_URL },
            { onSuccess: (res) => setValue("token", res?.token) }
        );
    };

    const handleOtpVerification = (credentials: z.infer<typeof otpSchema>) => {
        otpVerificationMutation.mutate(
            { url: VERIFY_OTP_URL, data: credentials, options: { withCredentials: true } },
            {
                onSuccess: (res) => {
                    setJwtToken(res?.token)
                    queryClient.removeQueries({ queryKey: ["user"] });
                    //handleClose()
                },
                onError: (err) => onError(err),
            }
        );
    };

    const onSubmit = (data: z.infer<typeof otpSchema>) => {
        handleOtpVerification(data);
    };

    return (
        <div className="container px-4 mx-auto my-6">
            <form onSubmit={form.handleSubmit(onSubmit)}>
                <FieldGroup>
                    <Controller
                        name="otp"
                        control={form.control}
                        render={({ field, fieldState }) => (
                            <Field data-invalid={fieldState.invalid}>
                                <FieldLabel>Enter OTP</FieldLabel>
                                    <InputOTP
                                        maxLength={6}
                                        value={field.value}
                                        onChange={field.onChange}
                                        aria-invalid={fieldState.invalid}
                                    >
                                        <div className="container flex flex-col justify-center">
                                            <InputOTPGroup>
                                                <InputOTPSlot index={0} className="w-15 h-14 text-lg" />
                                                <InputOTPSlot index={1} className="w-15 h-14 text-lg" />
                                                <InputOTPSlot index={2} className="w-15 h-14 text-lg" />
                                                <InputOTPSlot index={3} className="w-15 h-14 text-lg" />
                                                <InputOTPSlot index={4} className="w-15 h-14 text-lg" />
                                                <InputOTPSlot index={5} className="w-15 h-14 text-lg" />
                                            </InputOTPGroup>
                                        </div>
                                    </InputOTP>
                                    {fieldState.invalid && (
                                        <FieldError errors={[fieldState.error]} />
                                    )}
                            </Field>
                        )}
                    />
                </FieldGroup>
                <div className="flex items-center justify-end mt-2">
                    <OtpResendButton
                        handleOtpResend={handleOtpResend}
                        status={resendMutation}
                        delay={resendMutation?.error?.response?.data?.delay}
                    />
                </div>
                <Button type="submit" className="w-full gap-2 my-6">
                    Verify OTP
                </Button>
            </form>
        </div>
    );
};

export default VerifyOtp;