import axios, {type AxiosResponse} from 'axios';

export interface RequestOptions {
    withCredentials?: boolean;
    withXSRFToken?: boolean;
    withBearer?: boolean;
}

const api = axios.create({
    headers: {
        "Content-Type": "application/json",
    },
    withCredentials: true,
    withXSRFToken: true,
    timeout: 60000
});

const handlePost = async <TResponse, TData> (
    url: string,
    data: TData,
    options?: RequestOptions
): Promise<TResponse> => {
    console.log(data);
    const response: AxiosResponse<TResponse> = await api.post(url, data, {...options});
    return response.data;
}

const handleGet = async <TResponse> (
    url: string,
    options?: RequestOptions
): Promise<TResponse> => {
    const response: AxiosResponse<TResponse> = await api.get(url, {...options});
    return response.data;
}

const handlePut = async <TResponse, TData>(
    url: string,
    data: TData,
    options?: RequestOptions
): Promise<TResponse> => {
    const response: AxiosResponse<TResponse> = await api.put(url, data, {...options});
    return response.data;
}

const handleDelete = async <TResponse>(
    url: string,
    options?: RequestOptions
): Promise<TResponse> => {
    const response: AxiosResponse<TResponse> = await api.delete(url, options);
    return response.data;
};

export { api, handlePost, handleGet, handlePut, handleDelete };