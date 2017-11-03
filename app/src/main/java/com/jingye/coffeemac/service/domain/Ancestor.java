package com.jingye.coffeemac.service.domain;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.jingye.coffeemac.common.json.JSONArray;
import com.jingye.coffeemac.common.json.JSONObject;
import com.jingye.coffeemac.util.Base64;

/**
 * 该类提供AIDL服务协议domain,同时继承该祖先类具备了序列化互通的能力
 * 如果在极端要求效率的情况，请自行实现toString方法和parse方法
 * @author zhousq
 *
 */
public class Ancestor implements Serializable{

	public  String toString() {
		return writeObject(this);
//		return generateObjectBody(this); 
	}
	
	/**根据对象序列化成文本
	 *
	 */
	public static String generateObjectBody(Object object){
		JSONObject jsonObject = new JSONObject(object);
		return jsonObject.toString(); 
	}
	/**根据文本赋值对象
	 * 
	 */
	private void parse(String body) {
//		parseObject(body);
//		try {
//			JSONObject jsonObject = new JSONObject(body);
//			jsonObject.toBean(this);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}
	
	/**
	 * 根据文本序列成对象
	 * @param body
	 * @param clz
	 * @return
	 */
	public static <T> T parseObject(String body,Class clz){
		Object object =  null; 
		try {
			JSONObject jsonObject = new JSONObject(body);
			object =  jsonObject.toBean(clz);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return (T)object;
	}
	
	/**
	 * 传入对象队列生成序列化文本
	 * @param objects
	 * @return
	 */
	public static String generateListBody(List objects){
		JSONArray arrays = new JSONArray();
		for(int i = 0; i < objects.size(); i++) {
			JSONObject jsonObject = new JSONObject(objects.get(i));
			arrays.put(jsonObject) ;
		}
		return arrays.toString();
	}
	
	/**
	 * 根据文本发射到对象列表
	 * @param body
	 * @param clz
	 * @return
	 */
	public static List parseList(String body,Class clz){
		List  objects = new ArrayList();
		try {
			JSONArray arrays = new JSONArray(body);
			for(int i=0;i<arrays.length();i++)
			{
				JSONObject jsonObject = arrays.getJSONObject(i);
				Object object =  jsonObject.toBean(clz);
				objects.add(object);

			}

		} catch (Exception e) {
			e.printStackTrace(); 
		}
		return objects;
	}

	public void assign(Object source){
		copyProperties(this, source);
	}
	
	public static void copyProperties(Object target,Object source){

		/*分别获得源对象和目标对象的Class类型对象,Class对象是整个反射机制的源头和灵魂！
          Class对象是在类加载的时候产生,保存着类的相关属性，构造器，方法等信息
		 */
		Class sourceClz = source.getClass();
		Class targetClz = target.getClass();
		//得到Class对象所表征的类的所有属性(包括私有属性)
		Field[] fields = sourceClz.getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			String fieldName = fields[i].getName();
			Field targetField  = null;
			try {
				//得到targetClz对象所表征的类的名为fieldName的属性，不存在就进入下次循环
				targetField = targetClz.getDeclaredField(fieldName);
			} catch (SecurityException e) {
				e.printStackTrace();
				break;
			} catch (NoSuchFieldException e) {
				continue;
			}
			if(fields[i].getType()==targetField.getType()){

				//由属性名字得到对应get和set方法的名字
				String getMethodName = "get" + fieldName.substring(0,1).toUpperCase() + fieldName.substring(1);
				String setMethodName = "set" + fieldName.substring(0,1).toUpperCase() + fieldName.substring(1);
				//由方法的名字得到get和set方法的Method对象
				Method getMethod;
				try {
					getMethod = sourceClz.getDeclaredMethod(getMethodName, new Class[]{});
					Method setMethod = targetClz.getDeclaredMethod(setMethodName, fields[i].getType());
					//调用source对象的getMethod方法
					Object result = getMethod.invoke(source, new Object[]{});
					//调用target对象的setMethod方法
					setMethod.invoke(target, result);
				} catch (Exception e) {
					e.printStackTrace();
				}  
			}
		}
	}
	
	/**
	 * 该对象序列化采用了二进制然后Base64 需要对象序列表
	 * implements Serializable
	 */
	public static String writeObject(Object object){
		String body = "";
		ByteArrayOutputStream bos = null;
		ObjectOutputStream oos = null;
		try {
			bos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(bos);
			oos.writeObject(object);
			body = Base64.encode(bos.toByteArray());

		} catch (Exception e) {
			return "";
		}
		finally{
			try{
				if(oos!=null)
					oos.close();
				if(bos!=null)
					bos.close();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}

		return body;
	}
	
	/**
	 * 对象反序列化
	 * @param <T>
	 * @param body
	 * @return
	 */
	public synchronized static <T>T parseObject(String body){
		Object object = null;
		ByteArrayInputStream bis = null;
		ObjectInputStream _oos = null;
		try {
			byte[] data = Base64.decode(body);
			bis = new ByteArrayInputStream(data);
			_oos = new ObjectInputStream(bis);
			object = _oos.readObject();

		} catch (Exception e) {
			e.printStackTrace();
		}
		finally{
			try{
				if(_oos!=null)
					_oos.close();
				if(bis!=null)
					bis.close();
			}
			catch (Exception e) {
				e.printStackTrace();
			}

		}
		return (T)object;
	}
}
