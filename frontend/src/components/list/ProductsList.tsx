import { useEffect } from "react";
import {useParameterizedQuery} from "@/services/queries.ts";
import type {PagedProductsResponse} from "@/components/list/interface/ProductProps.ts";
import ProductCard from "@/components/cards/ProductCard.tsx";
import usePagination from "@/hooks/usePagination.ts";
import {useSearchParams} from "react-router";
import Pagination from "@/components/pagination/Pagination.tsx";

const BASE_URL = import.meta.env.VITE_BACKEND_BASE_URL;
const PRODUCTS_URL = `${BASE_URL}/public/products`;


const ProductList = () => {
    const [searchParams] = useSearchParams();
    const pagination = usePagination();
    const queryData = useParameterizedQuery({
        url: `${PRODUCTS_URL}?${searchParams}`,
        params: `pagination-${searchParams.toString()}`,
        cacheKey: "products-pagination",
        queryOptions: {
            suspense: true,
            enabled: !!searchParams
        },
    });

    const data = queryData?.data as PagedProductsResponse | undefined;
    const { isPending, isFetching } = queryData;
    const products = data?._embedded?.productDTOes ?? [];

    useEffect(() => {
        if (queryData.data) {
            pagination.setPagination(queryData.data?.page);
        }
    }, [queryData.data, pagination]);

    if (products.length === 0) {
        return (
            <div className="col-span-full flex flex-col items-center justify-center gap-2 py-16 text-muted-foreground">
                <p className="text-sm">No products found.</p>
            </div>
        );
    }

    return (
        <>
            <div className="grid grid-cols-2 gap-4 sm:grid-cols-3 lg:grid-cols-4">
                {products.map((product) => (
                    <ProductCard key={product.uuid} product={product} />
                ))}
            </div>
            {(pagination) && (
                <Pagination
                    {...pagination}
                    isPending={isPending}
                    isFetching={isFetching}
                />
            )}
        </>
    );
};

export default ProductList;