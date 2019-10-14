package com.customAnnotations.ch02;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface LogAno {

    LogType logType();

    //包含秘密之类的log不存参数
    boolean isPrivateLog() default false;
}
