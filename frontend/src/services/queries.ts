import {useErrorBoundary} from "react-error-boundary";
import {handleGet} from "@/services/api.jsx";
import {keepPreviousData, useQuery} from "@tanstack/react-query";

interface RequestOptions {
    withCredentials?: boolean;
    withXSRFToken?: boolean;
    Csrf?: boolean;
}

interface ParameterizedQueryProps {
    url: string;
    params?: string | Record<string, never>;
    cacheKey: string;
    staleTime?: number;
    placeholderData?: typeof keepPreviousData;
    refetchInterval?: number;
    refetchOnWindowFocus?: boolean;
    retry?: number;
    enableBoundary?: boolean;
    queryOptions?: Record<string, unknown>;
    options?: RequestOptions;
}

export const useParameterizedQuery = <TData>(
    {
        url,
        params = '',
        cacheKey,
        staleTime = 5 * 60 * 1000,
        placeholderData = keepPreviousData,
        refetchInterval = 30 * 60 * 1000,
        refetchOnWindowFocus = true,
        retry = 2,
        enableBoundary = true,
        queryOptions = {},
        options = { withCredentials: false, withXSRFToken: false }
    }: ParameterizedQueryProps) => {
    const { showBoundary } = useErrorBoundary();
    return useQuery<TData>({
        queryKey: [cacheKey, params],
        queryFn: () => handleGet<TData>(url, options),
        placeholderData: placeholderData,
        refetchInterval: refetchInterval,
        refetchOnWindowFocus: refetchOnWindowFocus,
        refetchOnReconnect: true,
        staleTime: staleTime,
        retry: retry,
        ...queryOptions,
        throwOnError: (error) => {
            if (enableBoundary) {
                showBoundary(error?.message);
                return true;
            }
            return false;
        }
    });
};

export const useSimpleQuery = <TData>(
    {
        url,
        cacheKey,
        staleTime = 5 * 60 * 1000,
        placeholderData = keepPreviousData,
        refetchInterval = 30 * 60 * 1000,
        refetchOnWindowFocus = true,
        queryOptions= {},
        options = { withCredentials: false, withXSRFToken: false }
    }: ParameterizedQueryProps) => {
    return useQuery<TData>({
        queryKey: [cacheKey],
        queryFn: () => handleGet<TData>(url, options),
        placeholderData: placeholderData,
        refetchInterval: refetchInterval,
        refetchOnWindowFocus: refetchOnWindowFocus,
        refetchOnReconnect: true,
        staleTime: staleTime,
        ...queryOptions,
    });
};