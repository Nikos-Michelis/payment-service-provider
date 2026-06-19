import {Controller, useForm} from "react-hook-form";
import {zodResolver} from "@hookform/resolvers/zod";
import {Button} from "@/components/ui/button.tsx";
import {Field, FieldError, FieldGroup, FieldLabel} from "@/components/ui/field.tsx";
import z from "zod";
import {Input} from "@/components/ui/input.tsx";

const resetPasswordSchema = z
    .object({
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

type ResetPasswordValues = z.infer<typeof resetPasswordSchema>;


const ResetPasswordForm = ({serverError}) => {
    const form = useForm<ResetPasswordValues>({
        resolver: zodResolver(resetPasswordSchema),
        defaultValues: { password: "", confirmPassword: "" },
    });

    async function onSubmit(data: z.infer<typeof resetPasswordSchema>) {
        console.log(data)
    }

    return (
            <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
                <FieldGroup>
                    <Controller
                        name="password"
                        control={form.control}
                        render={({ field, fieldState }) => (
                            <Field data-invalid={fieldState.invalid}>
                                <FieldLabel htmlFor="form-rhf-demo-title">
                                    Bug Title
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
                                    Bug Title
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
                <Button type="submit" className="w-full">
                    "Saving…"
                </Button>
            </form>
    );
};

export default ResetPasswordForm;