package com.pig.mvcframework.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Documented
@Retention(RUNTIME)
@Target(TYPE)
/** 
* 描述：注解Service
* @author zhengjinlei 
* @version 2019年1月11日 上午10:42:00 
*/
public @interface PigService {
	String value() default "";
}

