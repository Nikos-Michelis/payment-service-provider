import {type Dispatch, type SetStateAction, useLayoutEffect} from "react";
import {api} from "@/services/api.jsx";
import { useQueryClient } from "@tanstack/react-query";
import setupInterceptors from "@/services/setupInterceptors.ts";


interface SetupInterceptorsParams {
    jwtToken: string | null;
    setJwtToken: Dispatch<SetStateAction<string | null>>;
    csrfToken: string;
    setIsSyncingJwt: Dispatch<SetStateAction<boolean>>;
}

export const useAxiosInterceptors =
    (
        params: SetupInterceptorsParams
    ) => {
    const { jwtToken, setJwtToken, csrfToken, setIsSyncingJwt } = params;
    const queryClient = useQueryClient();
    useLayoutEffect(() => {

        const { requestIntercept, responseIntercept } =
            setupInterceptors({ jwtToken, setJwtToken, csrfToken, setIsSyncingJwt, queryClient });

        return () => {
            api.interceptors.request.eject(requestIntercept);
            api.interceptors.response.eject(responseIntercept);
        };
    }, [jwtToken, setJwtToken, setIsSyncingJwt, csrfToken, queryClient]);
};