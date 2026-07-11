import {Card, CardContent, CardFooter, CardHeader, CardTitle} from "@/components/ui/card.tsx";
import {Check, ImageOff, PackageX, Star} from "lucide-react";
import {Badge} from "@/components/ui/badge.tsx";
import {Button} from "@/components/ui/button.tsx";
import {useState} from "react";
import useCart from "@/hooks/useCart.ts";

const ProductCard = ({ product }) => {
    const {
        title,
        brand,
        category,
        price,
        discountPercentage,
        rating,
        stock,
        availabilityStatus,
        thumbnailUrl,
    } = product;
    const { addItem } = useCart();

    const [justAdded, setJustAdded] = useState(false);
    const handleAddToCart = () => {
        addItem(product.sku, 1);
        setJustAdded(true);
        setTimeout(() => setJustAdded(false), 1500);
    };

    const hasDiscount = discountPercentage && Number(discountPercentage) > 0;
    const discountedPrice = hasDiscount
        ? (Number(price) * (1 - Number(discountPercentage) / 100)).toFixed(2)
        : null;

    const isOutOfStock = stock === 0;

    return (
        <Card className="flex flex-col overflow-hidden pt-0 gap-3 transition-shadow hover:shadow-md">
            <div className="relative aspect-square w-full bg-muted">
                {thumbnailUrl ? (
                    <img
                        src={thumbnailUrl}
                        alt={title}
                        loading="lazy"
                        className="h-full w-full object-cover"
                    />
                ) : null}
                <div
                    className="absolute inset-0 hidden items-center justify-center bg-muted text-muted-foreground"
                    style={{ display: thumbnailUrl ? "none" : "flex" }}
                >
                    <ImageOff className="h-8 w-8" />
                </div>

                {hasDiscount && (
                    <Badge className="absolute left-2 top-2 bg-red-600 text-white hover:bg-red-600">
                        -{Number(discountPercentage).toFixed(0)}%
                    </Badge>
                )}
                {isOutOfStock && (
                    <Badge variant="secondary" className="absolute right-2 top-2 gap-1">
                        <PackageX className="h-3 w-3" />
                        Out of stock
                    </Badge>
                )}
            </div>

            <CardHeader className="px-4 py-0">
                <div className="flex items-start justify-between gap-2">
                    <CardTitle className="line-clamp-2 text-sm font-medium leading-snug">
                        {title}
                    </CardTitle>
                    {rating != null && (
                        <div className="flex shrink-0 items-center gap-1 text-xs text-muted-foreground">
                            <Star className="h-3.5 w-3.5 fill-yellow-400 text-yellow-400" />
                            {Number(rating).toFixed(1)}
                        </div>
                    )}
                </div>
                <p className="text-xs text-muted-foreground">
                    {brand || "Unbranded"} {category?.name ? `· ${category.name}` : ""}
                </p>
            </CardHeader>

            <CardContent className="flex flex-1 flex-col gap-2 px-4 py-0">
                <div className="flex items-baseline gap-2">
                    {hasDiscount ? (
                        <>
                            <span className="text-base font-semibold">${discountedPrice}</span>
                            <span className="text-xs text-muted-foreground line-through">${Number(price).toFixed(2)}</span>
                        </>
                    ) : (
                        <span className="text-base font-semibold">${Number(price).toFixed(2)}</span>
                    )}
                </div>

                {availabilityStatus && !isOutOfStock && (
                    <Badge variant="outline" className="w-fit text-xs font-normal text-muted-foreground">
                        {availabilityStatus}
                    </Badge>
                )}
            </CardContent>

            <CardFooter className="px-4 pb-4">
                <Button
                    className="w-full"
                    size="sm"
                    disabled={isOutOfStock}
                    onClick={handleAddToCart}
                >
                    {isOutOfStock ? (
                        "Unavailable"
                    ) : justAdded ? (
                        <>
                            <Check className="h-4 w-4" />
                            Added
                        </>
                    ) : (
                        "Add to cart"
                    )}
                </Button>
            </CardFooter>
        </Card>
    );
};

export default ProductCard;