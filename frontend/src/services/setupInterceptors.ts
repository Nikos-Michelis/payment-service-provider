import {api, handlePost} from "@/services/api.jsx";
import type { AxiosError, InternalAxiosRequestConfig } from "axios";
import type {QueryClient} from "@tanstack/react-query";
import type {Dispatch, SetStateAction} from "react";

/**
 * setupInterceptors:
 * 1. Automatic Authorization/CSRF header injection.
 * 2. Token Refreshing with "Request Queueing" to prevent multiple refresh calls.
 * 3. Cache invalidation on session failure.
 * * @param {string} token - Current access token.
 * @param {function} setToken - State setter to update the token.
 * @param {string} csrfToken - Current CSRF token.
 * @param {function} setIsSyncingSession - State setter to track refresh status.
 * @param {object} queryClient - React Query client instance.
 */
interface SetupInterceptorsParams {
    jwtToken: string | null;
    setJwtToken: Dispatch<SetStateAction<string | null>>;
    csrfToken: string;
    setIsSyncingJwt: Dispatch<SetStateAction<boolean | false>>;
    queryClient: QueryClient;
}

interface FailedRequest {
    resolve: (token: string | null) => void;
    reject: (error: unknown) => void;
}

interface SuccessResponse {
    access_token: string;
}

interface CustomAxiosRequestConfig extends InternalAxiosRequestConfig {
    withBearer?: boolean;
    withXSRFToken?: boolean;
    withIdempotency?: boolean;
    _retry?: boolean;
    url?: string | undefined;
}

const BASE_URL: string = import.meta.env.VITE_BACKEND_BASE_URL;
const REFRESH_TOKEN_URL = `${BASE_URL}/auth/refresh-token`;
const AUTH_URL = `${BASE_URL}/auth/`

const setupInterceptors =
    (
        params: SetupInterceptorsParams
    ) => {

    const { jwtToken, setJwtToken, csrfToken, setIsSyncingJwt, queryClient} = params;
    let failedQueue: FailedRequest[] = [];
    let isRefreshing = false;

    const processQueue = ((error: unknown, newToken: string | null) => {
        failedQueue.forEach((prom) => {
            if (error) {
                prom.reject(error);
            } else {
                prom.resolve(newToken);
            }
        });
        failedQueue = [];
    })

    const requestIntercept =
        api.interceptors.request.use((config: CustomAxiosRequestConfig) => {
                if (config.withBearer && jwtToken) {
                    config.headers["Authorization"] = `Bearer ${jwtToken}`;
                }
                if (config.withXSRFToken && csrfToken) {
                    config.headers["X-XSRF-TOKEN"] = csrfToken;
                }
                if (config.withIdempotency) {
                    config.headers["Idempotency-Key"] = crypto.randomUUID();
                }
                return config;
            }
        );
    const responseIntercept = api.interceptors.response.use(
        (response) => response, async (error: AxiosError) => {
            const originalRequest: CustomAxiosRequestConfig = error.config as CustomAxiosRequestConfig;
            // Skip refresh logic for auth-related endpoints (login/logout etc.)

            const url = originalRequest.url ?? "";
            if (url.includes(AUTH_URL)) {
                return Promise.reject(error);
            }

            if (error.response?.status === 401 && !originalRequest._retry) {
                // A refresh is ALREADY happening. Return a pending promise and add this request to the queue.
                if (isRefreshing) {
                    return new Promise((resolve, reject) => {
                        failedQueue.push({resolve, reject});
                    }).then((newToken) => {
                        originalRequest.headers["Authorization"] = `Bearer ${newToken}`;
                        return api(originalRequest);
                    }).catch((err) => Promise.reject(err));
                }
                // This is the first request to hit 401 which trigger the refresh call.
                originalRequest._retry = true;
                isRefreshing = true;

                try {
                    const res = await handlePost(REFRESH_TOKEN_URL, {}, {withCredentials: true, withBearer: false})
                    const response = res as SuccessResponse;
                    const newToken = response?.access_token;

                    console.log(newToken)
                    setJwtToken(newToken);
                    processQueue(null, newToken);
                    originalRequest.headers["Authorization"] = `Bearer ${newToken}`;
                    return api(originalRequest);
                } catch (error) {
                    processQueue(error, null);
                    isRefreshing = false;
                    setIsSyncingJwt(false);
                    setJwtToken(null);
                    queryClient.setQueriesData({queryKey: ['user']}, null);
                    return Promise.reject(error);
                } finally {
                    isRefreshing = false;
                    setIsSyncingJwt(false)
                }
            }
            return Promise.reject(error);
        }
    );

    return {requestIntercept, responseIntercept};

}
export default setupInterceptors