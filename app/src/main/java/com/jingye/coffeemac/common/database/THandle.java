package com.jingye.coffeemac.common.database;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class THandle  implements InvocationHandler  {

	private Object delegate = null;
	public THandle(Object delegate)
	{
		this.delegate = delegate;  
	}
	public Object invoke(Object proxy, Method method, Object[] args)throws Throwable {
	
		com.jingye.coffeemac.util.log.LogUtil.vendor("execute:" + method.getName());
		Object object = method.invoke(delegate,args);  
		return object;
	}
	public final static Object binding(Object proxy)
	{   
		if(proxy == null)
			return null;
		Object bindObject = null;
		InvocationHandler handler = new THandle(proxy);  
		bindObject =  
			Proxy.newProxyInstance(  
					proxy.getClass().getClassLoader(),  
					proxy.getClass().getInterfaces(),handler); 

		return bindObject;
	}
	
	/**绑定代理请注意构造函数的无参数性
	 * @deprecated 
	 * @param proxyClass
	 * @return
	 */
	public final static Object binding(Class proxyClass)
	{   
		Object proxy = null;
		try {
			proxy = proxyClass.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} 
		return binding(proxy);
	}	

}
