import {Controller, useForm} from "react-hook-form";
import {zodResolver} from "@hookform/resolvers/zod";
import z from "zod";
import {Field, FieldError, FieldGroup, FieldLabel} from "@/components/ui/field.tsx";
import {Input} from "@/components/ui/input.tsx";
import {Button} from "@/components/ui/button.tsx";
import type {AuthFormProps} from "@/components/forms/interface/AuthFormInterfae.ts";

const registerSchema = z
    .object({
        username: z
            .string()
            .min(3, "Username must be at least 3 characters.")
            .max(20, "Username must be at most 20 characters.")
            .regex(/^[a-zA-Z0-9_]+$/, "Username can only contain letters, numbers, and underscores."),
        email: z.string().email("Please enter a valid email address."),
        password: z
            .string()
            .min(8, "Password must be at least 8 characters.")
            .regex(/[A-Z]/, "Password must contain at least one uppercase letter.")
            .regex(/[0-9]/, "Password must contain at least one number."),
        confirmPassword: z.string(),
    })
    .refine((data) => data.password === data.confirmPassword, {
        message: "Passwords do not match.",
        path: ["confirmPassword"],
    });

type RegisterValues = z.infer<typeof registerSchema>;


const RegisterForm = ({serverError, onLogin}: AuthFormProps) => {
    const form = useForm<RegisterValues>({
        resolver: zodResolver(registerSchema),
        defaultValues: { username: "", email: "", password: "", confirmPassword: "" },
    });

    async function onSubmit(data: z.infer<typeof registerSchema>) {
        console.log(data)
    }

    console.log(onLogin)
    return (
        <form onSubmit={form.handleSubmit(onSubmit)}>
                <FieldGroup>
                    <Controller
                        name="username"
                        control={form.control}
                        render={({ field, fieldState }) => (
                            <Field data-invalid={fieldState.invalid}>
                                <FieldLabel htmlFor="form-username">
                                    Username
                                </FieldLabel>
                                <Input
                                    {...field}
                                    id="form-username"
                                    aria-invalid={fieldState.invalid}
                                    placeholder="e.g. JohnDoe"
                                />
                                {fieldState.invalid && (
                                    <FieldError errors={[fieldState.error]} />
                                )}
                            </Field>
                        )}
                    />
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
                                    aria-invalid={fieldState.invalid}
                                    placeholder="e.g. example@gmail.com"
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
                                    aria-invalid={fieldState.invalid}
                                />
                                {fieldState.invalid && (
                                    <FieldError errors={[fieldState.error]} />
                                )}
                            </Field>
                        )}
                    />
                    <Controller
                        name="confirmPassword"
                        control={form.control}
                        render={({ field, fieldState }) => (
                            <Field data-invalid={fieldState.invalid}>
                                <FieldLabel htmlFor="form-rhf-demo-title">
                                    Repeat Password
                                </FieldLabel>
                                <Input
                                    {...field}
                                    id="form-rhf-demo-title"
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
                    Sign Up
                </Button>
                <p className="text-center text-sm text-muted-foreground">
                    Already have an account?
                    <Button
                        type="button"
                        variant="link"
                        className="text-sm font-medium text-primary hover:underline underline-offset-4 transition-colors"
                        onClick={onLogin}
                    >
                        Sign In
                    </Button>
                </p>
        </form>
    );
};

export default RegisterForm;