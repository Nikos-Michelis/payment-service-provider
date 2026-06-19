import { GoogleLogin, type CredentialResponse } from "@react-oauth/google";
import {useCreateMutation} from "@/services/mutations.ts";
import {useQueryClient} from "@tanstack/react-query";
import useAuth from "@/hooks/useAuth.ts";
import type {AuthFormProps} from "@/components/forms/interface/AuthFormInterfae.ts";

const BASE_URL  = import.meta.env.VITE_BACKEND_BASE_URL;
const GOOGLE_OAUTH_URL = `${BASE_URL}/oauth2/login/google`

function GoogleLoginButton({ onStepChange, onError, serverError }: AuthFormProps) {
    const queryClient = useQueryClient();
    const { setJwtToken, setIdToken, setOauthProfile } = useAuth();

    const signInWithGoogleMutation = useCreateMutation({
        successMessage: undefined
    });

    const handleGoogleLoginResponse = ((response) => {
        const { credential: idToken } = response;
        signInWithGoogleMutation.mutate(
            { url: GOOGLE_OAUTH_URL, data: { idToken } },
            {
                onSuccess: (response) => {
                    queryClient.removeQueries({ queryKey: ["user"] });
                    if (response.token) {
                        setJwtToken(response.token);
                        return;
                    }
                    setIdToken(idToken);
                    setOauthProfile(response);
                    onStepChange("oauth-register")
                },
                onError: (error) => onError(error),
            }
        );
    });


    return (
        <div className="flex justify-center">
            <GoogleLogin
                onSuccess={(credentialResponse) => {
                    handleGoogleLoginResponse(credentialResponse);
                }}
                onError={() => {
                    console.error("Unable to login with Google provider.");
                }}
                text="continue_with"
                useOneTap={false}
            />
        </div>
    );
}

export default GoogleLoginButton;