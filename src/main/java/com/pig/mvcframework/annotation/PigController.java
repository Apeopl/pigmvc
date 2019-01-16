package com.pig.mvcframework.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** 
* 描述：注解controller
* @author zhengjinlei 
* @version 2019年1月11日 上午10:32:10 
*/
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PigController {
	String value() default "";
}

