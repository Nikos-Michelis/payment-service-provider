import { Button } from "@/components/ui/button";
import { ChevronLeft, ChevronRight, ChevronsLeft, ChevronsRight } from "lucide-react";

const FIRST_PAGE = 1;

interface PaginationProps {
    page: number;
    totalPages: number;
    totalElements: number;
    nextPage: () => void;
    previousPage: () => void;
    firstPage: () => void;
    lastPage: () => void;
    isPending?: boolean;
    isFetching?: boolean;
}

export default function Pagination(
    {
        page,
        totalPages,
        totalElements,
        nextPage,
        previousPage,
        firstPage,
        lastPage,
        isPending,
        isFetching,
    }: PaginationProps) {
    const isBusy = isPending || isFetching;
    const isFirstPage = page === FIRST_PAGE;
    const isLastPage = page === totalPages;

    return (
            <div className="container mt-10 mb-4 flex items-center justify-center gap-4">
                <div className="flex items-center gap-2">
                    {!isFirstPage && (
                        <Button
                            variant="outline"
                            size="icon"
                            disabled={isFirstPage || isBusy}
                            onClick={firstPage}
                        >
                            <ChevronsLeft className="h-4 w-4" />
                        </Button>
                    )}
                    <Button
                        variant="outline"
                        disabled={isFirstPage || isBusy}
                        onClick={previousPage}
                        className="gap-1"
                    >
                        <ChevronLeft className="h-4 w-4" />
                        Prev
                    </Button>
                </div>

                <span className="text-sm text-muted-foreground">
                    Page <span className="font-medium text-foreground">{page}</span> /{" "}
                    <span className="font-medium text-foreground">{totalPages}</span> of{" "}
                    <span className="font-medium text-foreground">{totalElements}</span> results
                </span>

                <div className="flex items-center gap-2">
                    <Button
                        variant="outline"
                        disabled={isLastPage || isBusy}
                        onClick={nextPage}
                        className="gap-1"
                    >
                        Next
                        <ChevronRight className="h-4 w-4" />
                    </Button>
                    {!isLastPage && (
                        <Button
                            variant="outline"
                            size="icon"
                            disabled={isLastPage || isBusy}
                            onClick={lastPage}
                        >
                            <ChevronsRight className="h-4 w-4" />
                        </Button>
                    )}
                </div>
            </div>
    );
}