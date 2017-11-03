package com.jingye.coffeemac.util.multicard;

import java.lang.reflect.Method;

import android.text.TextUtils;

/**
 * 反射工具类
 * 通过反射获得对应函数功能
 * @author gzb
 */
public class ReflectionUtil {

    /**
     * 通过类名，运行指定方法
     * @param cName         类名
     * @param methodName    方法名
     * @param params        参数值
     * @return 失败返回null
     */
    /*
    public static Object invokeByClassName(String cName, String methodName, Object[] params) {
        
    	if(TextUtils.isEmpty(cName)
        		|| TextUtils.isEmpty(methodName)){
        		return null;
        }
    	
        Object retObject = null;
        
        try {
            // 加载指定的类
            Class cls = Class.forName(cName);    
            
            // 利用newInstance()方法，获取构造方法的实例
            Constructor ct = cls.getConstructor(null);
            Object obj = ct.newInstance(null);    

            // 根据方法名获取指定方法的参数类型列表
            Class paramTypes[] = _getParamTypes(cls, methodName);
            
            // 获取指定方法
            Method meth = cls.getMethod(methodName, paramTypes);
            meth.setAccessible(true);
            
            // 调用指定的方法并获取返回值为Object类型
            if(params == null){
            	retObject = meth.invoke(obj);
            } else {
            	retObject = meth.invoke(obj, params);
            }
            
        } catch (Exception e) {
        	e.printStackTrace();
        }
        
        return retObject;
    }*/
    
    /**
     * 通过类对象，运行指定方法
     * @param obj 类对象
     * @param methodName 方法名
     * @param params 参数值
     * @return 失败返回null
     */
    public static Object invokeByObject(Object obj, String methodName, Object[] params) {
    	
    	if(obj == null
    		|| TextUtils.isEmpty(methodName)){
    		return null;
    	}
    	
        Class cls = obj.getClass();
        Object retObject = null;
        
        try {

            // 根据方法名获取指定方法的参数类型列表
            Class paramTypes[] = _getParamTypes(cls, methodName);
            
            // 获取指定方法
            Method meth = cls.getMethod(methodName, paramTypes);
            meth.setAccessible(true);
            
            // 调用指定的方法并获取返回值为Object类型
            if(params == null){
            	retObject = meth.invoke(obj);
            } else {
            	retObject = meth.invoke(obj, params);
            }
        } catch (Exception e) {
        	e.printStackTrace();
        }
        
        return retObject;
    }
    
    /**
     * 获取参数类型，返回值保存在Class[]中
     * @param cls 类
     * @param mName 方法名字
     * @return 返回产生类型列表
     */
    private static Class[] _getParamTypes(Class cls, String mName) {
        Class[] cs = null;
        
        /*
         * Note: 由于我们一般通过反射机制调用的方法，是非public方法
         * 所以在此处使用了getDeclaredMethods()方法
         */
        Method[] mtd = cls.getDeclaredMethods();    
        for (int i = 0; i < mtd.length; i++) {
            if (!mtd[i].getName().equals(mName)) {    // 不是我们需要的参数，则进入下一次循环
                continue;
            }
            
            cs = mtd[i].getParameterTypes();
        }
        return cs;
    }
}