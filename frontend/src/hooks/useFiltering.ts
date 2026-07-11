import {useCallback, useEffect} from "react";
import {useSearchParams} from "react-router";

export function useFiltering(defaultFilters, parseParams) {
    const [filterParams, setSearchParams] = useSearchParams();

    useEffect(() => {
        Object.entries(defaultFilters).forEach(([key, value]) => {
            if (!filterParams.has(key)) {
                filterParams.set(key, value.toString());
            }
        });
        setSearchParams(filterParams, { replace: true });
    }, [defaultFilters, filterParams, setSearchParams]);

    const setFilters = useCallback((filters) => {
        setSearchParams((params) => {
            Object.entries(filters).forEach(([key, value]) => {
                if (value) {
                    params.set(key, value.toString());
                    params.has('offset') && params.set('offset', defaultFilters.offset);
                    params.has('page') && params.set('page', defaultFilters.page);
                } else {
                    params.delete(key);
                }
            });
            return params;
        }, { replace: true });
    }, [defaultFilters.offset, defaultFilters.page, setSearchParams]);

    const resetFilters = useCallback(() => {
        setSearchParams(defaultFilters, { replace: true });
    }, [defaultFilters, setSearchParams]);

    const resetFilterByName = useCallback((name) => {
        setSearchParams((prev) => {
            if (defaultFilters?.[name] != null) {
                prev.set(name, defaultFilters[name]);
            } else {
                prev.delete(name);
            }
            return prev;
        }, { replace: true });
    }, [defaultFilters, setSearchParams]);

    return {
        ...parseParams(filterParams),
        setFilters,
        resetFilters,
        resetFilterByName,
    };
}