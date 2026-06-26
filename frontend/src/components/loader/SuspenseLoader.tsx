import {type ReactNode, Suspense} from "react";

interface CardContentProps {
    fallbackComponent: ReactNode;
    children: ReactNode;
}

const SuspenseLoader = ({fallbackComponent, children,}: CardContentProps) => (
    <Suspense fallback={fallbackComponent}>
        {children}
    </Suspense>
);

export default SuspenseLoader;