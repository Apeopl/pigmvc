package com.pig.mvcframework.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Documented
@Retention(RUNTIME)
@Target(FIELD)
/** 
* 描述：注解Autowired
* @author zhengjinlei 
* @version 2019年1月11日 上午10:43:35 
*/
public @interface PigAutowired {
	String value() default "";
}

