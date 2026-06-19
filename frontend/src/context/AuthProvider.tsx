import React, {createContext, useState, useCallback, useEffect, useMemo} from "react";
import { useQueryClient, useMutation } from "@tanstack/react-query";
import {handlePost} from "@/services/api.jsx";
import {useSimpleQuery} from "@/services/queries.jsx";
import {toast} from "sonner";
import {useAxiosInterceptors} from "@/hooks/useAxiosInterceptors";

interface AuthProviderProps {
    children: React.ReactNode;
}

interface AuthResponse {
    token?: string;
    username?: string;
}

interface AuthUser {
    id: string;
    username: string;
    email: string;
}

interface AuthError {
    data?: object;
}

type AuthProviderState = {
    jwtToken: null;
    setJwtToken: React.Dispatch<React.SetStateAction<string | null>>;
    idToken: null,
    setIdToken: React.Dispatch<React.SetStateAction<string | null>>,
    oauthProfile: null,
    setOauthProfile: React.Dispatch<React.SetStateAction<object | null>>,
    user: AuthUser;
    status: {
        isPending: boolean;
        isFetching: boolean;
        isError: boolean;
        isSuccess: false;
    };
    logout: () => void;
    csrfToken: string;
}

const BASE_URL = import.meta.env.VITE_BACKEND_BASE_URL;
const LOGOUT_URL = `${BASE_URL}/user/logout`;
const ACCOUNT_URL = `${BASE_URL}/user/my-account`;
const CSRF_TOKEN_URL = `${BASE_URL}/csrf/token`;

export const SecurityContext = createContext(null);


export const AuthProvider = ({ children }: AuthProviderProps) => {
    const queryClient = useQueryClient();
    const [jwtToken, setJwtToken] = useState<string | null>(null);
    const [idToken, setIdToken] = useState<string | null>(null);
    const [oauthProfile, setOauthProfile] = useState<object | null>(null);
    const [isSyncingJwt, setIsSyncingJwt] = useState<boolean>(false);
    const csrfQuery
        = useSimpleQuery({
        url: CSRF_TOKEN_URL,
        cacheKey: "csrf",
        refetchOnWindowFocus: false,
        staleTime: 15 * 60 * 1000,
        queryOptions:{
            retry: 1,
        },
        options: { withCredentials: true, withBearer: false }
    });
    const csrfToken:string = csrfQuery?.data?.token;
    const userQuery
        = useSimpleQuery({
        url: ACCOUNT_URL,
        cacheKey: "user",
        refetchOnWindowFocus: false,
        staleTime: 15 * 60 * 1000,
        queryOptions:{
            enabled: !!csrfToken,
            retry: false
        },
        options: { withCredentials: false, withBearer: true }
    });

    const logoutMutation = useMutation({
        mutationFn: ({ data, url, options }) => handlePost(url, data, options),
    });

    const logout = useCallback( () => {
        logoutMutation.mutate(
            { url: LOGOUT_URL, options: { withCredentials: true, withBearer: false }},
            {
                onSuccess: () => {
                    setJwtToken(null);
                    queryClient.removeQueries({ queryKey: ["user"] });
                    queryClient.removeQueries({ queryKey: ["csrf"] });
                    toast.success("You have been logged out successfully. See you next time!");
                },
                onError: (error) => {
                    const authError: AuthError = error?.response?.data;
                    toast.error(authError?.error ?? "Something went wrong");
                },
            }
        )
    }, [logoutMutation, queryClient]);

    useAxiosInterceptors({jwtToken, csrfToken, setJwtToken, setIsSyncingJwt});

    useEffect(() => {
        if (userQuery.isSuccess && userQuery.data) {
            const { username } = userQuery.data as AuthResponse;
            toast.success(`Welcome, ${username}!`);
        }
    }, [userQuery.isSuccess, userQuery.data]);

    const providerValues: AuthProviderState = useMemo(() => {
        const isFetching = (csrfQuery.isFetching) || (userQuery?.isFetching);
        const isAuthenticated = !!jwtToken && !!userQuery.data;
        const authUser: AuthUser = userQuery.data as AuthUser;
        return {
            jwtToken,
            setJwtToken,
            idToken,
            setIdToken,
            oauthProfile,
            setOauthProfile,
            user: authUser ?? undefined,
            status: {
                isPending: csrfQuery.isPending || userQuery?.isPending,
                isFetching: isFetching || isSyncingJwt,
                isError: userQuery.isError || csrfQuery.isError,
                isSuccess: isAuthenticated && !isSyncingJwt
            },
            logout,
            csrfToken,
        };
    }, [
        csrfQuery.isFetching, csrfQuery.isPending, csrfQuery.isError,
        userQuery?.isFetching, userQuery.data, userQuery?.isPending, userQuery.isError,
        jwtToken, isSyncingJwt, csrfToken, logout
    ]);
    return (
        <SecurityContext.Provider value={providerValues}>
            {children}
        </SecurityContext.Provider>
    );
};