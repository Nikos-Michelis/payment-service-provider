import {Controller, useForm} from "react-hook-form";
import z from "zod";
import {zodResolver} from "@hookform/resolvers/zod";
import {Field, FieldError, FieldGroup, FieldLabel} from "@/components/ui/field.tsx";
import {Input} from "@/components/ui/input.tsx";
import {Button} from "@/components/ui/button.tsx";
import {Checkbox} from "@/components/ui/checkbox.tsx";
import {useCreateMutation} from "@/services/mutations.ts";
import {Loader2} from "lucide-react";
import type {AuthFormProps} from "@/components/forms/interface/AuthFormInterfae.ts";

const loginSchema = z.object({
    email: z.string().email("Please enter a valid email address."),
    password: z
        .string()
        .min(8, "Password must be at least 8 characters.")
        .regex(/[A-Z]/, "Password must contain at least one uppercase letter.")
        .regex(/[0-9]/, "Password must contain at least one number."),
    rememberMe: z.boolean(),
})

const BASE_URL  = import.meta.env.VITE_BACKEND_BASE_URL;
const LOGIN_URL = `${BASE_URL}/auth/authenticate`

const LoginForm = ({ serverError, onForgot, onLogin, onError }: AuthFormProps) => {
    const form = useForm<z.infer<typeof loginSchema>>({
        defaultValues: {
            email: "",
            password: "",
            rememberMe: false
        },
        resolver: zodResolver(loginSchema)
    })

    const loginMutation = useCreateMutation({
        successMessage: undefined
    });

    const isPending = loginMutation.isPending;

    const handleLogin = (credentials = null) => {
        loginMutation.mutate(
            { data: credentials, url: LOGIN_URL },
            {
                onSuccess: (response) => {
                    if (response?.token) onLogin?.(response);
                },
                onError: (err) => onError?.(err),
            }
        );
    };


    const onSubmit = (data) => {
        handleLogin(data);
    };

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
                                    type="email"
                                    aria-invalid={fieldState.invalid}
                                    placeholder="example@gmail.com"
                                />
                                {fieldState.invalid && (
                                    <FieldError errors={[fieldState.error]} />
                                )}
                            </Field>
                        )}
                    />
                    <Controller
                        name="password"
                        control={form.control}
                        render={({ field, fieldState }) => (
                            <Field data-invalid={fieldState.invalid}>
                                <FieldLabel htmlFor="form-rhf-demo-title">
                                    Password
                                </FieldLabel>
                                <Input
                                    {...field}
                                    id="form-rhf-demo-title"
                                    type="password"
                                    aria-invalid={fieldState.invalid}
                                />
                                {fieldState.invalid && (
                                    <FieldError errors={[fieldState.error]} />
                                )}
                            </Field>
                        )}
                    />
                    <div className="flex align-items-center">
                        <Controller
                            name="rememberMe"
                            control={form.control}
                            render={({ field }) => (
                                <div className="container flex items-center justify-between">
                                    <label
                                        htmlFor="remember-me"
                                        className="flex items-center gap-2 cursor-pointer text-sm text-muted-foreground"
                                    >
                                        <Checkbox
                                            id="remember-me"
                                            checked={field.value}
                                            onCheckedChange={(checked) => field.onChange(!!checked)}
                                        />
                                        <span>Remember me</span>
                                    </label>
                                    <Button
                                        type="button"
                                        variant="link"
                                        onClick={onForgot}
                                        className="text-sm font-medium text-primary hover:underline underline-offset-4 transition-colors"
                                    >
                                        Forgot password?
                                    </Button>
                                </div>
                            )}
                        />
                    </div>
                    <Button type="submit">
                        {isPending &&
                            <Loader2 className="h-4 w-4 animate-spin" />
                        }
                        Sign in
                    </Button>
                </FieldGroup>
            </form>
        </div>
    );
};

export default LoginForm;