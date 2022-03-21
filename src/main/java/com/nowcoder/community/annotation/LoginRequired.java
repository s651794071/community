package com.nowcoder.community.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD) // 声明该注解应该写在方法上
@Retention(RetentionPolicy.RUNTIME) // 声明该注解有效时期
public @interface LoginRequired {
    // 里面什么也不用写，这个注解相当于起到一个标识作用
}
