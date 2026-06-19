import {
    Dialog,
    DialogContent,
    DialogDescription,
    DialogTitle,
    DialogTrigger,
    DialogClose,
} from "@/components/ui/dialog";
import {Button} from "@/components/ui/button";
import {ArrowLeft, X} from "lucide-react";
import React from "react";

export default function Modal({
                                  open,
                                  onOpenChange,
                                  children,
                              }: {
    open: boolean;
    onOpenChange: (open: boolean) => void;
    children: React.ReactNode;
}) {
    return (
        <Dialog open={open} onOpenChange={onOpenChange}>
            {children}
        </Dialog>
    );
}

interface ModalContentProps {
    title?: string;
    showBack?: boolean;
    description?: string;
    closeIcon?: React.ReactNode;
    onBack?: () => void;
    className?: string;
    children: React.ReactNode;
}

function ModalContent(
    {
        title,
        showBack,
        description = "",
        closeIcon,
        onBack,
        className = "",
        children,
    }: ModalContentProps) {
    return (
        <DialogContent
            className={`sm:max-w-lg gap-0 p-0 [&>button]:hidden ${className}`}
        >
            <div className="flex items-center justify-between border-b px-6 py-4">
                <div className="flex items-center gap-2">
                    {showBack && (
                        <Button
                            type="button"
                            variant="ghost"
                            size="icon"
                            onClick={onBack}
                        >
                            <ArrowLeft className="h-4 w-4"/>
                        </Button>
                    )}

                    {title && (
                        <DialogTitle className="text-lg font-semibold">
                            {title}
                        </DialogTitle>
                    )}
                </div>

                <DialogDescription className="sr-only">
                    {description || `Details about ${title}`}
                </DialogDescription>

                <DialogClose asChild>
                    <Button
                        type="button"
                        variant="ghost"
                        size="icon"
                    >
                        {closeIcon ?? <X className="h-4 w-4"/>}
                    </Button>
                </DialogClose>
            </div>

            <div className="p-6">
                {children}
            </div>
        </DialogContent>
    );
}

Modal.Button = DialogTrigger;
Modal.Close = DialogClose;
Modal.Content = ModalContent;