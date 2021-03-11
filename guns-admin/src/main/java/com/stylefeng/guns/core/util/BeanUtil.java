package com.stylefeng.guns.core.util;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

public class BeanUtil {
	
	public static void copyProperties(Object dest, Object orig) throws IllegalAccessException, InvocationTargetException {
		BeanUtils.copyProperties(dest, orig);
	}
	
	public static Object getProperty(Object dest, String name) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException  {
		return PropertyUtils.getSimpleProperty(dest, name);
	}

	/**
	 * 合并对象，将src的值合并入des，如果值为null，则不覆盖
	 * @param <T>
	 * @param srcObj
	 * @param desObj
	 */
	public static <T> void mergeObject(T srcObj, T desObj) {
		if(srcObj == null || desObj == null) return;

		Field[] fs = srcObj.getClass().getDeclaredFields();
		for (int i=0; i<fs.length; i++) {
			try{
				fs[i].setAccessible(true);
				Object value = fs[i].get(srcObj);
				if(null != value){
					fs[i].set(desObj, value);
				}
				fs[i].setAccessible(false);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}

	public static <T> void mergeNotSameClassObject(T srcObj, T desObj) throws Exception {
		if(srcObj == null || desObj == null) return;

		Field[] fs = srcObj.getClass().getDeclaredFields();
		Field[] fs2 = desObj.getClass().getDeclaredFields();
		Class<?> clazz = desObj.getClass();
		if(fs2!=null&&fs2.length>0) {
			for (int i = 0; i < fs.length; i++) {
				fs[i].setAccessible(true);
				Object value = fs[i].get(srcObj);
				if (null != value) {
					for(int j=0;j<fs2.length;j++)
					{
						fs2[j].setAccessible(true);
						if (fs2[j].getName().equals(fs[i].getName())) {
//							String methodName=fs2[j].getName();
//							methodName="set"+methodName.substring(0,1).toUpperCase()+methodName.substring(1);
//							Class type=fs2[j].getType();
//							Method setRecordStatusMethod = clazz.getMethod(methodName, type);
//							setRecordStatusMethod.invoke(desObj, value);
                                fs2[j].set(desObj, value);
						}
						fs2[j].setAccessible(false);
					}
				}
				fs[i].setAccessible(false);
			}
		}
	}
}
