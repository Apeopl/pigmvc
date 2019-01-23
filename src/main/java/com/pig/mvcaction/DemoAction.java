package com.pig.mvcaction;
/** 
* 描述：测试demo
* @author zhengjinlei 
* @version 2019年1月11日 上午10:48:18 
*/

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.pig.mvcframework.annotation.PigAutowired;
import com.pig.mvcframework.annotation.PigController;
import com.pig.mvcframework.annotation.PigRequestMapping;
import com.pig.mvcframework.annotation.PigRequstParam;
import com.pig.service.api.IDemoService;
@PigController
@PigRequestMapping("/demo")
public class DemoAction {
	@PigAutowired(value="demoService")
	private IDemoService demoService;
	
	@PigRequestMapping("/query")
	public void query(HttpServletRequest req, HttpServletResponse resp, @PigRequstParam("name") String name){
		String result = demoService.sayHello(name);
		try {
			System.out.println("返回结果");
			resp.getWriter().write(result);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@PigRequestMapping("/add")
	public void add(HttpServletRequest req, HttpServletResponse resp, @PigRequstParam("a") String a, @PigRequstParam("b") String b){
		try {
			resp.getWriter().write(a + "+" + b + "=" + (a+b));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

