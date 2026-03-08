package com.example.pointsystem.infrastructure.redis;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.Ordered;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.core.annotation.Order;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.lang.reflect.UndeclaredThrowableException;

/**
 * @MemberPointLock 을 처리하는 AOP Aspect 입니다.
 */
@Aspect
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@RequiredArgsConstructor
public class MemberPointLockAspect {

    private static final ExpressionParser PARSER = new SpelExpressionParser();
    private static final ParameterNameDiscoverer PARAMETER_NAMES = new DefaultParameterNameDiscoverer();

    private final MemberPointLockManager memberPointLockManager;

    @Around("@annotation(memberPointLock)")
    public Object lock(ProceedingJoinPoint joinPoint, MemberPointLock memberPointLock) throws Throwable {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        MethodBasedEvaluationContext context = new MethodBasedEvaluationContext(
                joinPoint.getTarget(),
                method,
                joinPoint.getArgs(),
                PARAMETER_NAMES
        );

        Object memberKey = PARSER.parseExpression(memberPointLock.key()).getValue(context);
        Long memberId = toMemberId(memberKey);

        try {
            return memberPointLockManager.executeWithLock(memberId, () -> {
                try {
                    return joinPoint.proceed();
                } catch (RuntimeException | Error e) {
                    throw e;
                } catch (Throwable e) {
                    throw new UndeclaredThrowableException(e);
                }
            });
        } catch (UndeclaredThrowableException e) {
            throw e.getUndeclaredThrowable();
        }
    }

    private Long toMemberId(Object memberKey) {
        switch (memberKey) {
            case null -> throw new IllegalArgumentException("락 키의 회원 식별자를 찾을 수 없습니다.");
            case Number number -> {
                return number.longValue();
            }
            case String text when !text.isBlank() -> {
                try {
                    return Long.parseLong(text);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("락 키의 회원 식별자 형식이 올바르지 않습니다: " + text, e);
                }
            }
            default -> {
            }
        }

        throw new IllegalArgumentException("락 키의 회원 식별자 타입을 지원하지 않습니다: " + memberKey.getClass().getSimpleName());
    }
}
