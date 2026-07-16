package com.stripe.payment_service_provider.user.reporitory;

import com.stripe.payment_service_provider.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query("""
        SELECT u
        FROM User u
        INNER JOIN FETCH u.entryMethods s
        INNER JOIN FETCH u.roles r
        LEFT JOIN FETCH r.permissions p
        LEFT JOIN FETCH u.stripeCustomer sc
        LEFT JOIN FETCH sc.stripePaymentMethods
        LEFT JOIN FETCH sc.subscriptions
        WHERE u.email = :email
    """)
    Optional<User> findByEmail(@Param("email") String email);

    @Query("""
        SELECT u
        FROM User u
        INNER JOIN FETCH u.entryMethods s
        INNER JOIN FETCH u.roles r
        LEFT JOIN FETCH r.permissions p
        LEFT JOIN FETCH u.stripeCustomer sc
        LEFT JOIN FETCH sc.subscriptions sb
        WHERE sb.id IS NULL OR sb.status = "CANCELED"
    """)
    List<User> findAllUnsubscribedUsers();

    @Query("""
        SELECT u
        FROM User u
        INNER JOIN FETCH u.entryMethods s
        INNER JOIN FETCH u.roles r
        LEFT JOIN FETCH r.permissions p
        LEFT JOIN FETCH u.stripeCustomer sc
        LEFT JOIN FETCH sc.subscriptions sb
        WHERE sb.id IS NOT NULL AND sb.status != "CANCELED"
    """)
    List<User> findAllSubscribedUsers();

    @Query("""
        SELECT u
        FROM User u
        INNER JOIN FETCH u.entryMethods s
        INNER JOIN FETCH u.roles r
        LEFT JOIN FETCH r.permissions p
        LEFT JOIN FETCH u.stripeCustomer sc
        LEFT JOIN FETCH sc.subscriptions sb
        WHERE sc.stripeCustomerId = :customerId
    """)
    Optional<User> findByCustomerId(@Param("customerId") String customerId);

    @Query("""
        SELECT COUNT(u) > 0
        FROM User u
        WHERE u.email = :email
    """)
    boolean existsByEmail(@Param("email") String email);

    @Query("""
        SELECT COUNT(u) > 0
        FROM User u
        WHERE u.username = :username
    """)
    boolean existsByUsername(@Param("username") String username);
}
