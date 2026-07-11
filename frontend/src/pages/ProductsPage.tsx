import SuspenseLoader from "@/components/loader/SuspenseLoader.tsx";
import SpinnerLoader from "@/components/loader/SpinnerLoader.tsx";
import ProductList from "@/components/list/ProductsList.tsx";

const ProductsPage = () => {

    return (
        <div className="mx-auto max-w-6xl px-4 py-8">
            <div className="mb-6 flex items-end justify-between">
                <div>
                    <h1 className="text-2xl font-semibold">Products</h1>
                </div>
            </div>
            <SuspenseLoader fallbackComponent={<SpinnerLoader />}>
                <ProductList />
            </SuspenseLoader>
        </div>
    );
};

export default ProductsPage;