export interface CategoryDTO {
    id: number | null;
    name: string;
}

export interface Product {
    id: number;
    uuid: string;
    externalId: number;
    category: CategoryDTO;
    title: string;
    description: string;
    brand: string;
    sku: string;
    price: number;
    discountPercentage: number;
    rating: number;
    stock: number;
    weight: number;
    warrantyInformation: string;
    shippingInformation: string;
    availabilityStatus: string;
    returnPolicy: string;
    minimumOrderQuantity: number;
    barcode: string;
    qrCodeUrl: string;
    thumbnailUrl: string;
}

export interface PageInfo {
    number: number;
    size: number;
    totalElements: number;
    totalPages: number;
}

export interface PagedProductsResponse {
    _embedded: {
        productDTOes: Product[];
    };
    _links: Record<string, { href: string }>;
    page: PageInfo;
}