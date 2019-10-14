package com.customAnnotations.ch01;

import java.lang.annotation.*;

/**
 * 自定义注解
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented  //使用了该注解后，将自定义注解设置为文档说明内容，在生成javadoc时会将该注解加入到文档中
public @interface CurrentUser {
    boolean require() default true; //是否必须
}
