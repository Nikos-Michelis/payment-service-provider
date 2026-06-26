import React from "react";
import {LogOut, Loader2 } from "lucide-react";

import {Button} from "@/components/ui/button";
import {Card, CardContent, CardHeader, CardTitle} from "@/components/ui/card";
import useAuth from "@/hooks/useAuth.ts";
import {Avatar, AvatarFallback, AvatarImage} from "@/components/ui/avatar.tsx";
import InvoiceList from "@/components/list/InvoiceList.tsx";
import SpinnerLoader from "@/components/loader/SpinnerLoader.tsx";
import SuspenseLoader from "@/components/loader/SuspenseLoader.tsx";
import SubscriptionSection from "@/components/section/SubscriptionSection.tsx";

const Profile: React.FC = () => {
    const {logout, user, status} = useAuth();
    const {isPending} = status;
    const {username, email} = user;

    const getInitials = (name: string) =>
        name.split(" ").map(n => n[0]).join("").toUpperCase().slice(0, 2);

    return (
        <div className="max-w-6xl mx-auto px-6 py-10 space-y-8">
            <div>
                <h1 className="text-3xl font-bold">Profile</h1>
                <p className="text-muted-foreground">
                    Manage your information, subscription and billing details
                </p>
            </div>

            <Card>
                <CardHeader>
                    <CardTitle>User Information</CardTitle>
                </CardHeader>
                <CardContent className="flex items-center justify-between">
                    <div className="flex items-center gap-2">
                        <Avatar size="lg">
                            <AvatarImage src="#" alt={username} />
                            <AvatarFallback>{getInitials(username)}</AvatarFallback>
                        </Avatar>

                        <div>
                            <p className="font-semibold">{username}</p>
                            <p className="text-sm text-muted-foreground">
                                {email}
                            </p>
                        </div>
                    </div>
                    <div className="flex flex-col">
                        <Button variant="outline">Edit Profile</Button>
                        <Button
                            className="my-2"
                            variant="outline"
                            onClick={() => logout()}
                        >
                            Logout
                            {isPending
                                ? <Loader2 className="h-4 w-4 animate-spin"/>
                                : <LogOut className="h-4 w-4"/>
                            }
                        </Button>
                    </div>
                </CardContent>
            </Card>

            <SubscriptionSection />

            <Card>
                <CardHeader className="flex flex-row items-center justify-between">
                    <CardTitle>Invoices</CardTitle>
                </CardHeader>
                <CardContent className="space-y-3">
                    <SuspenseLoader fallbackComponent={<SpinnerLoader />}>
                         <InvoiceList />
                    </SuspenseLoader>
                </CardContent>
            </Card>
        </div>
    );
};

export default Profile;