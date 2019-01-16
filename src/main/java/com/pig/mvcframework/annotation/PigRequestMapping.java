package com.pig.mvcframework.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Documented
@Retention(RUNTIME)
@Target({ TYPE, METHOD })
/** 
* 描述：注解RequestMapping
* @author zhengjinlei 
* @version 2019年1月11日 上午10:39:44 
*/
public @interface PigRequestMapping {
	String value() default "";
}

