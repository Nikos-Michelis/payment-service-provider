import { RadioGroup, RadioGroupItem } from "@/components/ui/radio-group";
import { Label } from "@/components/ui/label";
import { Truck, Store, PackageCheck } from "lucide-react";
import type { ShippingOption, ShippingMethodId } from "@/types/cart.types.ts";

const SHIPPING_OPTIONS: (ShippingOption & { icon: typeof Truck })[] = [
    {
        id: "HOME_DELIVERY",
        label: "Home delivery",
        cost: 0,
        available: true,
        icon: Truck,
    },
    {
        id: "LOCKER_PICKUP",
        label: "Locker pickup",
        cost: 0,
        available: true,
        icon: PackageCheck,
    },
    {
        id: "STORE_PICKUP",
        label: "Store pickup",
        cost: 0,
        available: true,
        icon: Store,
    },
];

interface ShippingOptionsProps {
    value: ShippingMethodId;
    onChange: (value: ShippingMethodId) => void;
}

const ShippingRadio = ({ value, onChange }: ShippingOptionsProps) => {
    return (
        <RadioGroup
            value={value}
            onValueChange={(v) => onChange(v as ShippingMethodId)}
            className="gap-3"
        >
            {SHIPPING_OPTIONS.map((option) => {
                const Icon = option.icon;
                const isSelected = value === option.id;
                return (
                    <Label
                        key={option.id}
                        htmlFor={option.id}
                        className={`flex cursor-pointer items-center gap-3 rounded-md border px-4 py-3 transition-colors ${
                            isSelected ? "border-primary" : "border-border"
                        } ${!option.available ? "cursor-not-allowed opacity-50" : ""}`}
                    >
                        <RadioGroupItem value={option.id} id={option.id} disabled={!option.available} />
                        <Icon className="h-5 w-5 text-muted-foreground" />
                        <span className="flex-1 text-sm font-medium">{option.label}</span>
                        <span className="text-sm font-medium text-emerald-600">
                            {option.cost === 0 ? "FREE" : `$${option.cost.toFixed(2)}`}
                        </span>
                    </Label>
                );
            })}
        </RadioGroup>
    );
};

export default ShippingRadio;