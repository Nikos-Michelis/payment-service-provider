export type SubscriptionPlan = {
    id: string
    name: string
    monthlyPrice: number
    yearlyPrice: number
    description: string
    popular?: boolean
    features: string[]
    limitations?: string[]
    badge?: string
    stripePriceIdMonthly: string
    stripePriceIdYearly: string
}

export const mockPlans: SubscriptionPlan[] = [
    {
        id: "prod_Tzm0z1oN8ouFSi",
        name: "MoonkeyEU - Basic",
        monthlyPrice: 9,
        yearlyPrice: 90,
        description:
            "Perfect for individuals, freelancers and early-stage creators.",
        badge: "BEGINNER",
        features: [
            "1 Workspace",
            "5 GB Storage",
            "Basic Analytics Profile",
            "Community Support",
            "Unlimited Tasks",
            "Email Notifications",
            "Stripe Billing Access",
            "Single Team Member"
        ],
        limitations: [
            "No AI automation",
            "No advanced reports",
            "No custom branding"
        ],
        stripePriceIdMonthly: "price_starter_monthly",
        stripePriceIdYearly: "price_starter_yearly"
    },

    {
        id: "pro",
        name: "Pro",
        monthlyPrice: 29,
        yearlyPrice: 290,
        description:
            "Advanced subscription designed for growing startups and SaaS companies.",
        popular: true,
        badge: "MOST POPULAR",
        features: [
            "Unlimited Workspaces",
            "100 GB Storage",
            "Advanced Analytics",
            "Priority Email Support",
            "AI Workflow Automation",
            "Unlimited Team Members",
            "Advanced Billing Portal",
            "Custom Domains",
            "API Access",
            "Revenue Insights",
            "Usage Metrics"
        ],
        limitations: [
            "No dedicated infrastructure"
        ],
        stripePriceIdMonthly: "price_pro_monthly",
        stripePriceIdYearly: "price_pro_yearly"
    },

    {
        id: "enterprise",
        name: "Enterprise",
        monthlyPrice: 99,
        yearlyPrice: 990,
        description:
            "Enterprise-grade billing infrastructure for high-scale organizations.",
        badge: "ENTERPRISE",
        features: [
            "Unlimited Everything",
            "Dedicated Infrastructure",
            "Unlimited Storage",
            "Custom AI Pipelines",
            "24/7 Premium Support",
            "Dedicated Account Manager",
            "Custom Billing Flows",
            "Advanced Fraud Protection",
            "Priority API Rate Limits",
            "SOC2 Compliance",
            "Audit Logs",
            "Advanced Team Permissions",
            "Custom Integrations",
            "White-label Platform"
        ],
        stripePriceIdMonthly: "price_enterprise_monthly",
        stripePriceIdYearly: "price_enterprise_yearly"
    }
]