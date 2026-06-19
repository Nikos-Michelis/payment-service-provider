import {Controller, useForm} from "react-hook-form";
import z from "zod";
import {zodResolver} from "@hookform/resolvers/zod";
import {Field, FieldError, FieldGroup, FieldLabel} from "@/components/ui/field.tsx";
import {Input} from "@/components/ui/input.tsx";
import {Button} from "@/components/ui/button.tsx";
import {useCreateMutation} from "@/services/mutations.ts";
import {Loader2} from "lucide-react";
import type {AuthFormProps} from "@/components/forms/interface/AuthFormInterfae.ts";


const forgotPasswordSchema = z.object({
    email: z.string().email("Please enter a valid email address."),
})

const BASE_URL  = import.meta.env.VITE_BACKEND_BASE_URL;
const FORGOT_PASSWORD_URL = `${BASE_URL}/auth/forgot-password`

const ForgotPasswordForm = ({serverError, onForgot, onError, }: AuthFormProps) => {
    const form = useForm<z.infer<typeof forgotPasswordSchema>>({
        defaultValues: {
            email: "",
        },
        resolver: zodResolver(forgotPasswordSchema)
    })

    const forgotPasswordMutation = useCreateMutation({
        successMessage: undefined
    });
    const isPending = forgotPasswordMutation.isPending;

    const handleForgotPassword = (credentials: z.infer<typeof forgotPasswordSchema>) => {
        forgotPasswordMutation.mutate(
            { data: credentials, url: FORGOT_PASSWORD_URL },
            {
                onSuccess: (response) => {
                    onForgot?.(response);
                },
                onError: (err) => onError(err),
            }
        );
    };
    const onSubmit = (data: z.infer<typeof forgotPasswordSchema>) => {
        handleForgotPassword(data);
    }

    return (
        <div className="container px-4 mx-auto my-6">
            <form onSubmit={form.handleSubmit(onSubmit)}>
                <FieldGroup>
                    <Controller
                        name="email"
                        control={form.control}
                        render={({ field, fieldState }) => (
                            <Field data-invalid={fieldState.invalid}>
                                <FieldLabel htmlFor="form-rhf-demo-title">
                                    Email
                                </FieldLabel>
                                <Input
                                    {...field}
                                    id="form-rhf-demo-title"
                                    placeholder="e.g. example@gmail.com"
                                    aria-invalid={fieldState.invalid}
                                />
                                {fieldState.invalid && (
                                    <FieldError errors={[fieldState.error]} />
                                )}
                            </Field>
                        )}
                    />
                </FieldGroup>
                <Button type="submit" className="w-full gap-2 my-6">
                    {isPending &&
                        <Loader2 className="h-4 w-4 animate-spin" />
                    }
                    Continue
                </Button>
            </form>
        </div>
    );
};

export default ForgotPasswordForm;