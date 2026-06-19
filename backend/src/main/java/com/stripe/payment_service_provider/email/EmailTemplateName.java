package com.stripe.payment_service_provider.email;

import lombok.Getter;

@Getter
public enum EmailTemplateName {
    ACTIVATE_ACCOUNT("activate_account"),
    FAILED_PAYMENT("payment_failed"),
    VERIFY_ACCOUNT("verify_account"),
    RESET_PASSWORD("reset_password"),
    CREATE_SUBSCRIPTION("create_subscription"),
    UPDATE_SUBSCRIPTION("update_subscription"),
    CANCELLED_SUBSCRIPTION("cancelled_subscription"),
    NOTIFICATION_SUBSCRIPTION("notification_subscription"),
    RENEWAL_SUBSCRIPTION("renewal_subscription"),;

    private final String name;
    EmailTemplateName(String name){
        this.name = name;
    }

}
