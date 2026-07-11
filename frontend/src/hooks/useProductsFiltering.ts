import {useFiltering} from "@/hooks/useFiltering.ts";

const DEFAULT_FILTERS = {
    page: 1,
    limit: 12,
    ordering: 'asc'
};

const parseParams = (params) => ({
    search:          params.get('search'),
    limit:           params.get('limit')           ? parseInt(params.get('limit'))           : DEFAULT_FILTERS.limit,
    ordering:        params.get('ordering')        ?? DEFAULT_FILTERS.ordering,
});

export function useProductFiltering() {
    return useFiltering(DEFAULT_FILTERS, parseParams);
}
