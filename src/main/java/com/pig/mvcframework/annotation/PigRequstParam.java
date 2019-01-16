package com.pig.mvcframework.annotation;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Documented
@Retention(RUNTIME)
@Target(PARAMETER)
/** 
* 描述：注解RequstParam
* @author zhengjinlei 
* @version 2019年1月11日 上午10:44:37 
*/
public @interface PigRequstParam {
	String value() default "";
}

