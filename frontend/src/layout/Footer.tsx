import { Separator } from "@/components/ui/separator";
import {Button} from "@/components/ui/button.tsx";

function Footer() {
    return (
        <footer className="bg-background">
            <Separator />
            <div className="flex justify-center">
                <div className="container py-6">
                    <div className="flex flex-col items-center justify-between gap-4 text-sm text-muted-foreground md:flex-row">
                        <p>
                            © {new Date().getFullYear()} Lumina. All rights reserved.
                        </p>

                        <div className="flex items-center gap-6">
                            <Button variant="link" className="h-auto p-0">
                                Privacy
                            </Button>

                            <Button variant="link" className="h-auto p-0">
                                Terms
                            </Button>

                            <Button variant="link" className="h-auto p-0">
                                Contact
                            </Button>
                        </div>
                    </div>
                </div>
            </div>
        </footer>
    );
}

export default Footer;