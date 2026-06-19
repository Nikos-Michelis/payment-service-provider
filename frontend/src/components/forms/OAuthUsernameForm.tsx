import {Controller, useForm} from "react-hook-form";
import {zodResolver} from "@hookform/resolvers/zod";
import {Button} from "@/components/ui/button.tsx";
import z from "zod";
import {Field, FieldError, FieldGroup, FieldLabel} from "@/components/ui/field.tsx";
import {Input} from "@/components/ui/input.tsx";
import {useCreateMutation} from "@/services/mutations.ts";
import {useQueryClient} from "@tanstack/react-query";
import useAuth from "@/hooks/useAuth.ts";
import type {AuthFormProps} from "@/components/forms/interface/AuthFormInterfae.ts";

const oauthUsernameSchema = z.object({
    username: z
        .string()
        .min(3, "Username must be at least 3 characters.")
        .max(20, "Username must be at most 20 characters.")
        .regex(/^[a-zA-Z0-9_]+$/, "Username can only contain letters, numbers, and underscores."),
});

type OAuthUsernameValues = z.infer<typeof oauthUsernameSchema>;

const BASE_URL  = import.meta.env.VITE_BACKEND_BASE_URL;
const GOOGLE_OAUTH_URL = `${BASE_URL}/oauth2/register/google`

const OAuthUsernameForm = ({onStepChange, onError, serverError}: AuthFormProps) => {
    const { setJwtToken, idToken, oauthProfile } = useAuth();
    const queryClient = useQueryClient();

    const form = useForm<OAuthUsernameValues>({
        resolver: zodResolver(oauthUsernameSchema),
        defaultValues: { username: "" },
    });

    const registerWithGoogleMutation = useCreateMutation({
        successMessage: undefined
    });

    const handleGoogleRegisterResponse = ((response) => {
        console.log(response);
        registerWithGoogleMutation.mutate(
            { url: GOOGLE_OAUTH_URL, data: response },
            {
                onSuccess: (response) => {
                    queryClient.removeQueries({ queryKey: ["user"] });
                    setJwtToken(response.token);
                },
                onError: (error) => onError(error),
            }
        );
    });


    const onSubmit = (data: z.infer<typeof oauthUsernameSchema>) => {
        console.log(data)
        handleGoogleRegisterResponse({
            confirmUsername: data.username,
            email: oauthProfile?.email,
            idToken: idToken
        })
    }

    return (
        <form onSubmit={form.handleSubmit(onSubmit)}>
            <FieldGroup>
                <Controller
                    name="username"
                    control={form.control}
                    render={({ field, fieldState }) => (
                        <Field data-invalid={fieldState.invalid}>
                            <FieldLabel htmlFor="form-rhf-demo-title">
                                Username
                            </FieldLabel>
                            <Input
                                {...field}
                                id="form-rhf-demo-title"
                                aria-invalid={fieldState.invalid}
                                placeholder="e.g. John Doe"
                            />
                            {fieldState.invalid && (
                                <FieldError errors={[fieldState.error]} />
                            )}
                        </Field>
                    )}
                />
                <Button>Sign in</Button>
            </FieldGroup>
        </form>
    );
};

export default OAuthUsernameForm;