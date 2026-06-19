package com.stripe.payment_service_provider.subscription.aop;

import com.stripe.payment_service_provider.subscription.service.impl.UserSubscriptionServiceImpl;
import com.stripe.payment_service_provider.settings.exceptions.subscription.SubscriptionRequiredException;
import com.stripe.payment_service_provider.settings.exceptions.subscription.SubscriptionTokenLimitReachedException;
import com.stripe.payment_service_provider.subscription.model.UserSubscription;
import com.stripe.payment_service_provider.subscription.model.SubscriptionStatus;
import com.stripe.payment_service_provider.subscription.service.impl.SubscriptionUsageServiceImpl;
import com.stripe.payment_service_provider.user.model.User;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.time.Instant;
import java.util.*;

@Aspect
@Slf4j
@Component
@RequiredArgsConstructor
public class SubscriptionAspect {

    private final SubscriptionUsageServiceImpl subscriptionUsageService;
    private final UserSubscriptionServiceImpl subscriptionService;

    @Transactional
    @Around("@annotation(com.stripe.payment_service_provider.subscription.aop.Subscribed)")
    public Object enforceSubscription(ProceedingJoinPoint joinPoint) throws Throwable {
        User user = getCurrentUser();

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Subscribed annotation = method.getAnnotation(Subscribed.class);

        UserSubscription userSubscription = subscriptionService.getActiveUserSubscription(user.getStripeCustomer().getSubscriptions())
                .orElseThrow(() -> new ResourceNotFoundException("user subscriptions not found"));

        UserSubscription validateActiveSubscription = validateActiveSubscription(userSubscription, annotation);

        int token = resolveTokenAmount(joinPoint, signature, annotation);

        if (annotation.useToken()) {
            checkIntervalTokenLimit(validateActiveSubscription, token);
        }

        Object result = joinPoint.proceed();

        if (isSuccessfulResponse() && annotation.useToken()) {
            subscriptionUsageService.useFromLimit(validateActiveSubscription, annotation.expenseId(), token);
        }

        return result;
    }

    //Spring Security Authenticated User
    private User getCurrentUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    //Spring Servlet Context Response Status Control (Thread Local)
    private boolean isSuccessfulResponse() {
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getResponse();
        return response != null && Set.of(HttpStatus.OK, HttpStatus.CREATED)
                .stream()
                .map(HttpStatusCode::value)
                .anyMatch(code -> code == response.getStatus());
    }

    private UserSubscription validateActiveSubscription(UserSubscription userSubscription, Subscribed annotation) {
        return Optional.ofNullable(userSubscription)
                .filter(sub -> sub.getStatus() != SubscriptionStatus.CANCELED)
                .filter(sub -> Arrays.asList(annotation.products()).contains(sub.getStripePlan().getPlanType()))
                .filter(sub -> sub.getCurrentPeriodEnd().isAfter(Instant.now()))
                .orElseThrow(SubscriptionRequiredException::new);
    }

    private void checkIntervalTokenLimit(UserSubscription userSubscription, long tokenUsage) {
        long usage = subscriptionUsageService.getUsageByInterval(userSubscription, Instant.now());
        if ((usage + tokenUsage) > userSubscription.getStripePlan().getTokenLimit()) {
            throw new SubscriptionTokenLimitReachedException();
        }
    }

    private int resolveTokenAmount(ProceedingJoinPoint joinPoint, MethodSignature signature, Subscribed annotation) {
        int token = 0;
        Map<String, Object> paramMap = buildMethodParamMap(signature, joinPoint.getArgs());
        EvaluationContext context = buildEvaluationContext(paramMap);
        ExpressionParser parser = new SpelExpressionParser();
        for (SubscriptionRule rule : annotation.rules()) {
            Boolean match = parser.parseExpression(rule.expression()).getValue(context, Boolean.class);
            if (Boolean.TRUE.equals(match)) {
                token += rule.token();
            }
        }
        return token;
    }

    private Map<String, Object> buildMethodParamMap(MethodSignature signature, Object[] args) {
        Map<String, Object> paramMap = new HashMap<>();
        String[] paramNames = signature.getParameterNames();

        for (int i = 0; i < paramNames.length; i++) {
            paramMap.put(paramNames[i], args[i]);
        }
        return paramMap;
    }

    private EvaluationContext buildEvaluationContext(Map<String, Object> paramMap) {
        EvaluationContext context = new StandardEvaluationContext();
        paramMap.forEach(context::setVariable);
        return context;
    }

}