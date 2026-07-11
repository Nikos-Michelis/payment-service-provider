import {type QueryKey, useMutation, type UseMutationOptions, useQueryClient} from "@tanstack/react-query";
import {handleDelete, handlePatch, handlePost, handlePut} from "@/services/api.jsx";
import { toast } from "sonner";
import type {AxiosRequestConfig} from "axios";
import loginForm from "@/components/forms/LoginForm.tsx";

interface RequestOptions {
    withCredentials?: boolean;
    withXSRFToken?: boolean;
    withBearer?: boolean;
    withIdempotency?: boolean;
}

interface ApiErrorResponse {
    error: string;
}

interface ApiError extends Error {
    response?: {
        data?: ApiErrorResponse;
    };
}

interface MutationVariables<TData> {
    url: string;
    data?: TData;
    options?: RequestOptions;
}

interface MutationProps {
    successMessage?: string;
    errorMessage?: string;
    showError?: boolean;
    queryKeysToInvalidate?: (string | number)[][];
    mutationOptions?: Record<string, () => void>;
}

export const useCreateMutation = <TData, TResponse>(
    {
        successMessage,
        errorMessage = "Oops! Something went wrong...",
        showError = true,
        queryKeysToInvalidate = [],
        mutationOptions = {},
    }: MutationProps = {}
) => {
    const queryClient = useQueryClient();

    return useMutation<TResponse, ApiError, MutationVariables<TData>>({
        mutationFn: ({ url, data, options }) => handlePost(url, data, options),

        onSuccess: () => {
            if (successMessage) toast.success(successMessage);
        },

        onError: (error) => {
            if (showError) {
                const msg = error.response?.data?.error ?? errorMessage;
                toast.error(msg);
            }
        },

        onSettled: async (_, error) => {
            if (!error && queryKeysToInvalidate.length > 0) {
                await Promise.all(
                    queryKeysToInvalidate.map((key) =>
                        queryClient.invalidateQueries({ queryKey: key })
                    )
                );
            }
        },

        ...mutationOptions,
    });
};

export const useDeleteMutation = <TResponse>(
    {
        successMessage,
        errorMessage = "Oops! Something went wrong...",
        showError = true,
        queryKeysToInvalidate = [],
        mutationOptions = {},
    }: MutationProps = {}
) => {
    const queryClient = useQueryClient();

    return useMutation<TResponse, ApiError, MutationVariables<object>>({
        mutationFn: ({ url, options }) => handleDelete(url, options),

        onSuccess: (data, variables, context) => {
            const { onSuccess } = mutationOptions
            if (successMessage) toast.success(successMessage);
            if(onSuccess) onSuccess(data, variables, context)
        },

        onError: (error) => {
            if (showError) {
                const msg = error.response?.data?.error ?? errorMessage;
                toast.error(msg);
            }
        },

        onSettled: async (_, error) => {
            if (!error && queryKeysToInvalidate.length > 0) {
                await Promise.all(
                    queryKeysToInvalidate.map((key) =>
                        queryClient.invalidateQueries({ queryKey: key })
                    )
                );
            }
        },

        ...mutationOptions,
    });
};


export const useUpdateMutation = <TData, TResponse = unknown>(
    {
        successMessage = "Operation successful!",
        errorMessage = "Oops! Something went wrong...",
        showError = true,
        queryKeysToInvalidate = [],
        mutationOptions = {},
    }: MutationProps = {}
) => {
    const queryClient = useQueryClient();

    return useMutation<TResponse, ApiError, MutationVariables<TData>>({
        mutationFn: ({ url, data, options }) => handlePut(url, data, options),

        onSuccess: () => {
            if (successMessage) toast.success(successMessage);
        },

        onError: (error) => {
            if (showError) {
                const msg = error.response?.data?.error ?? errorMessage;
                toast.error(msg);
            }
        },

        onSettled: async (_, error, variables) => {
            const { data } = variables;
            if (!error && queryKeysToInvalidate.length > 0) {
                await Promise.all(
                    queryKeysToInvalidate.flatMap((key) => [
                        queryClient.invalidateQueries({ queryKey: key }),
                        queryClient.invalidateQueries({
                            queryKey: [...key, data?.id],
                        }),
                    ])
                );
            }
        },

        ...mutationOptions,
    });
};


export const usePatchMutation = <TData, TResponse = unknown>(
    {
        successMessage = "Operation successful!",
        errorMessage = "Oops! Something went wrong...",
        showError = true,
        queryKeysToInvalidate = [],
        mutationOptions = {},
    }: MutationProps = {}
) => {
    const queryClient = useQueryClient();

    return useMutation<TResponse, ApiError, MutationVariables<TData>>({
        mutationFn: ({ url, data, options }) => handlePatch(url, data, options),

        onSuccess: () => {
            if (successMessage) toast.success(successMessage);
        },

        onError: (error) => {
            if (showError) {
                const msg = error.response?.data?.error ?? errorMessage;
                toast.error(msg);
            }
        },

        onSettled: async (_, error, variables) => {
            const { data } = variables;
            if (!error && queryKeysToInvalidate.length > 0) {
                await Promise.all(
                    queryKeysToInvalidate.flatMap((key) => [
                        queryClient.invalidateQueries({ queryKey: key }),
                        queryClient.invalidateQueries({
                            queryKey: [...key, data?.id],
                        }),
                    ])
                );
            }
        },

        ...mutationOptions,
    });
};