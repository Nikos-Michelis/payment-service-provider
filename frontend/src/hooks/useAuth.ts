import { useContext } from "react";
import { SecurityContext } from "@/context/AuthProvider";


const useAuth = () => {
    const context = useContext(SecurityContext);
    if (!context) throw new Error("useAuth must be used within AuthProvider");
    return context;
};

export default useAuth;