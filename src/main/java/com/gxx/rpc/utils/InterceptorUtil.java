package com.gxx.rpc.utils;

import java.util.HashMap;
import java.util.Map;

import com.gxx.rpc.core.Interceptor;

/** 
 * 拦截器工具类
 * 
 * @author Gxx
 */
public class InterceptorUtil {

	/**
	 * 拦截器类
	 */
	public static Map<String, Interceptor> interceptorMap = new HashMap<>();
}
