package com.pig.service.Impl;

import com.pig.mvcframework.annotation.PigService;
import com.pig.service.api.IDemoService;

/** 
* 描述：
* @author zhengjinlei 
* @version 2019年1月11日 上午10:50:21 
*/
@PigService("demoService")
public class DemoService implements IDemoService {

	public String sayHello(String name) {
		return "Hello " + name;
	}

}

