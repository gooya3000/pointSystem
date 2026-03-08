package com.example.pointsystem.infrastructure.redis;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 회원 단위 분산 락이 필요한 메서드에 적용합니다.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MemberPointLock {

    /**
     * SpEL 기반 회원 식별자 표현식입니다. 기본값은 첫 번째 인자입니다.
     */
    String key() default "#p0";
}
