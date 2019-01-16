package com.pig.mvcframework.servlet;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.pig.mvcframework.annotation.PigAutowired;
import com.pig.mvcframework.annotation.PigController;
import com.pig.mvcframework.annotation.PigRequestMapping;
import com.pig.mvcframework.annotation.PigService;

/** 
* 描述：启动入口类
* @author zhengjinlei 
* @version 2019年1月11日 上午10:21:21 
*/
public class PigDispatcherServlet extends HttpServlet{
	private static final long serialVersionUID = 1L;
	private static final String LOCATION = "contextConfigLocation";
	//加载配置文件
	private Properties properties = new Properties();
	//扫描类名
	private List<String> classNames = new ArrayList<String>();
	//保存所有初始化的bean
	private Map<String, Object> ioc = new HashMap<String, Object>();
	//保存所有url和方法映射关系
	private	Map<String, Method> handlerMapping = new HashMap<String, Method>();

	public PigDispatcherServlet() {
	}

	public void init(ServletConfig config) throws ServletException{
		//1、加载配置文件
		loadConfig(config.getInitParameter(LOCATION));
		//2、扫描所有相关类
		scanClasses(properties.getProperty("scanPackage"));
		//3、初始化所有类的实例，保存IOC容器中
		initIoc();
		//4、依赖注入
		doAutowired();
		//5、构造handlerMapping
		initHandlerMapping();
		System.out.println("pigmvc is init!");
	}
	
	
	private void initHandlerMapping() {
		if(ioc.isEmpty()) return ;
		for(Entry<String, Object> entry : ioc.entrySet()){
			Class<?> clazz = entry.getValue().getClass();
			if(!clazz.isAnnotationPresent(PigController.class)) continue ;
			String baseUrl = "";
			if(clazz.isAnnotationPresent(PigRequestMapping.class)){
				PigRequestMapping requestMapping = clazz.getAnnotation(PigRequestMapping.class);
				baseUrl = requestMapping.value();
			}
			
			Method[] methods = clazz.getMethods();
			for (Method method : methods) {
				if(!method.isAnnotationPresent(PigRequestMapping.class)) continue ;
				PigRequestMapping requestMapping = method.getAnnotation(PigRequestMapping.class);
				String url = ("/" + baseUrl + "/" +requestMapping.value()).replaceAll("/+", "/");
				handlerMapping.put(url, method);
				System.out.println("mapped:" + url + ":" + method);
			}
		}
	}

	private void doAutowired() {
		if(ioc.isEmpty()) return ;
		for(Entry<String, Object> entry : ioc.entrySet()){
			Field[] fields = entry.getValue().getClass().getDeclaredFields();
			for (Field field : fields) {
				if(!field.isAnnotationPresent(PigAutowired.class)) continue ;
				PigAutowired autowired = field.getAnnotation(PigAutowired.class);
				String beanName = autowired.value().trim();
				if("".equals(beanName)){
					beanName = field.getType().getName();
				}
				field.setAccessible(true);
				try {
					field.set(entry.getValue(), ioc.get(beanName));
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private void initIoc() {
		if(classNames.size() == 0) return ;
		try {
			for(String className : classNames){
				Class<?> clazz = Class.forName(className);
				if(clazz.isAnnotationPresent(PigController.class)){
					String beanName = toFirstLowerCase(clazz.getSimpleName());
					ioc.put(beanName, clazz.newInstance());
				}else if(clazz.isAnnotationPresent(PigService.class)){
					PigService service = clazz.getAnnotation(PigService.class);
					String beanName = service.value();
					if(!"".equals(beanName.trim())){
						ioc.put(beanName, clazz.newInstance());
						continue;
					}
					//如果没设置value，就按照接口类型来设置
					Class<?>[] interfaces = clazz.getInterfaces();
					for (Class<?> i : interfaces) {
						ioc.put(i.getName(), clazz.newInstance());
					}
				}else{
					continue;
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void scanClasses(String packageName) {
		URL url = this.getClass().getClassLoader().getResource("/"+packageName.replaceAll("\\.", "/"));
		File dir = new File(url.getFile());
		for(File file :dir.listFiles()){
			if(file.isDirectory()){
				scanClasses(packageName + "." + file.getName());
			}else{
				classNames.add(packageName + "." + file.getName().replace(".class", "").trim());
			}
		}
	}

	private void loadConfig(String location) {
		InputStream is = null;
		try {
			is = this.getClass().getClassLoader().getResourceAsStream(location);
			properties.load(is);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(null != is)
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doDispatcher(req, resp);
	}
	
	private void doDispatcher(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		if(this.handlerMapping.isEmpty()) return ;
		String url = req.getRequestURI();
		String contextPath = req.getContextPath();
		url = url.replace(contextPath, "").replaceAll("/+", "/");
		if(!this.handlerMapping.containsKey(url)){
			resp.getWriter().write("404 not found!");
			return ;
		}
		Map<String, String[]> parameterMap = req.getParameterMap();
		Method method = this.handlerMapping.get(url);
		Class<?>[] parameterTypes = method.getParameterTypes();
		int len = parameterTypes.length;
		Object[] paramValue = new Object[len];
		for(int i=0; i<len; i++){
			Class parameterType = parameterTypes[i];
			if(parameterType == HttpServletRequest.class){
				paramValue[i] = req;
				continue ;
			}else if(parameterType == HttpServletResponse.class){
				paramValue[i] = resp;
				continue ;
			}else if(parameterType == String.class){
				for(Entry<String, String[]> param : parameterMap.entrySet()){
					String value = Arrays.toString(param.getValue())
								   .replaceAll("\\[|\\]", "")
								   .replaceAll("\\&", ",");
					paramValue[i++] = value;
				}
			}else if(parameterType == Integer.class){
				
			}
			/*else{
				for(Entry<String, Object> param : parameterMap.entrySet()){
					Object value = Arrays.toString(param.getValue())
								   .replaceAll("\\[|\\]", "")
								   .replaceAll("\\$", ",");
					Object castValue = parameterType.cast(param.getValue());
					paramValue[i++] = castValue;
				}
			}*/
			
		}
		
		try {
			String beanName = toFirstLowerCase(method.getDeclaringClass().getSimpleName());
			method.invoke(this.ioc.get(beanName), paramValue);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String toFirstLowerCase(String simpleName) {
		char[] array = simpleName.toCharArray();
		array[0] += 32;
		return String.valueOf(array);
	}
}

