package com.gxx.rpc.utils;

import java.util.HashMap;
import java.util.Map;

/** 
 * 类型转换工具
 * @author Gxx
 */
public class TypeParseUtil {

	/**
	 * 转类型字符串到类型对象
	 * @param types
	 * @return
	 * @throws Throwable
	 */
	public static Map<String, Object> parseTypeString2Class(String[] types, Object[] args) throws Throwable {
		/**
		 * 结果
		 */
		Map<String, Object> result = new HashMap<>();
		/**
		 * 参数类型，String 转 Class
		 */
		Class<?>[] classTypes = null;
		
		if(null != types) {
			classTypes = new Class<?>[types.length];
			for(int i=0;i<types.length;i++) {
				/**
				 * 先判断基础类型
				 */
				if("byte".equals(types[i])) {
					classTypes[i] = byte.class;
					if(null == args[i]) {
						args[i] = getBasicTypeDefaultValue(byte.class);
					}
				} else if("short".equals(types[i])) {
					classTypes[i] = short.class;
					if(null == args[i]) {
						args[i] = getBasicTypeDefaultValue(short.class);
					}
				} else if("int".equals(types[i])) {
					classTypes[i] = int.class;
					if(null == args[i]) {
						args[i] = getBasicTypeDefaultValue(int.class);
					}
				} else if("long".equals(types[i])) {
					classTypes[i] = long.class;
					if(null == args[i]) {
						args[i] = getBasicTypeDefaultValue(long.class);
					}
				} else if("float".equals(types[i])) {
					classTypes[i] = float.class;
					if(null == args[i]) {
						args[i] = getBasicTypeDefaultValue(float.class);
					}
				} else if("double".equals(types[i])) {
					classTypes[i] = double.class;
					if(null == args[i]) {
						args[i] = getBasicTypeDefaultValue(double.class);
					}
				} else if("boolean".equals(types[i])) {
					classTypes[i] = boolean.class;
					if(null == args[i]) {
						args[i] = getBasicTypeDefaultValue(boolean.class);
					}
				} else if("char".equals(types[i])) {
					classTypes[i] = char.class;
					if(null == args[i]) {
						args[i] = getBasicTypeDefaultValue(char.class);
					}
				} else {
					/**
					 * 反射类型
					 */
					classTypes[i] = Class.forName(types[i]);
				}
			}
		}

		result.put("classTypes", classTypes);
		result.put("args", args);
		return result;
	}
	
	/**
	 * 判断是否基础类型
	 * @param type
	 * @return
	 */
	public static boolean isBasicType(Class<?> type) {
		return byte.class.equals(type) || short.class.equals(type) || int.class.equals(type) ||
				long.class.equals(type) || float.class.equals(type) || double.class.equals(type) ||
				boolean.class.equals(type) || char.class.equals(type);
	}
	
	/**
	 * 返回基础类型默认值
	 * @return
	 */
	public static Object getBasicTypeDefaultValue(Class<?> type) {
		if(byte.class.equals(type)) {
			byte temp = 0;
			return temp;
		} else if(short.class.equals(type)) {
			short temp = 0;
			return temp;
		} else if(int.class.equals(type)) {
			int temp = 0;
			return temp;
		} else if(long.class.equals(type)) {
			long temp = 0;
			return temp;
		} else if(float.class.equals(type)) {
			float temp = 0;
			return temp;
		} else if(double.class.equals(type)) {
			double temp = 0;
			return temp;
		} else if(boolean.class.equals(type)) {
			boolean temp = false;
			return temp;
		} else if(char.class.equals(type)) {
			char temp = 0;
			return temp;
		}
		return null;
	}
}
