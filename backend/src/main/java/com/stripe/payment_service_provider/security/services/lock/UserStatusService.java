package com.stripe.payment_service_provider.security.services.lock;

import com.stripe.payment_service_provider.user.model.User;

public interface UserStatusService {
    void disableAccount(User user);
    boolean isAccountLocked(User savedUser);
    void lockAccount(User user, boolean hasGradualLock);
    void resetAccount(User user);
    void unlockAccount(User user);
    void attemptsIncrement(User user);
    String getLockExpiration(User user);
}
