package com.ccnt.cado.util;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class BeanCaster {
	@SuppressWarnings("rawtypes")
	public static Object castMap2Bean(Map<String,Object> map,Class clazz){
		Object object = null;
		try {
			object = clazz.newInstance();
			Field[] fields = clazz.getDeclaredFields();
			for(Field field : fields){
				PropertyDescriptor pd = new PropertyDescriptor(field.getName(),clazz);
				Method setMethod = pd.getWriteMethod();
				setMethod.invoke(object, map.get(field.getName()));
			}
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (IntrospectionException e) {
			e.printStackTrace();
		}
		return object;
	}
	@SuppressWarnings("rawtypes")
	public static Map<String,Object> castBean2Map(Object bean){
		Map<String,Object> map = new HashMap<String,Object>();
		Class clazz = bean.getClass();
		Field[] fields = clazz.getDeclaredFields();
		for(Field field : fields){
			
			try {
				PropertyDescriptor pd = new PropertyDescriptor(field.getName(),clazz);
				Method getMethod = pd.getReadMethod();
				map.put(field.getName(), getMethod.invoke(bean));
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (IntrospectionException e) {
				e.printStackTrace();
			}
			
		}
		return map;
	}
	@SuppressWarnings("rawtypes")
	public static DBObject castBean2DBObject(Object bean){
		DBObject dbObject = new BasicDBObject();
		Class clazz = bean.getClass();
		Field[] fields = clazz.getDeclaredFields();
		for(Field field : fields){
			
			try {
				PropertyDescriptor pd = new PropertyDescriptor(field.getName(),clazz);
				Method getMethod = pd.getReadMethod();
				dbObject.put(field.getName(), getMethod.invoke(bean));
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (IntrospectionException e) {
				e.printStackTrace();
			}
			
		}
		return dbObject;
	}
	@SuppressWarnings("rawtypes")
	public static Object castDBObject2Bean(DBObject dbObject,Class clazz){
		Object object = null;
		try {
			object = clazz.newInstance();
			Field[] fields = clazz.getDeclaredFields();
			for(Field field : fields){
				PropertyDescriptor pd = new PropertyDescriptor(field.getName(),clazz);
				Method setMethod = pd.getWriteMethod();
				setMethod.invoke(object, dbObject.get(field.getName()));
			}
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (IntrospectionException e) {
			e.printStackTrace();
		}
		return object;
	}
}
