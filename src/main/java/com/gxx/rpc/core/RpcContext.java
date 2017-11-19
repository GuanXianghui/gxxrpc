package com.gxx.rpc.core;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.FutureTask;

import com.gxx.rpc.spring.BaseRefrence;

/** 
 * rpc上下文
 * @author Gxx
 */
public class RpcContext {

	/**
	 * 客户端应用名称
	 */
	public static ThreadLocal<String> clientApplicationName = new ThreadLocal<String>();

	/**
	 * 客户端IP
	 */
	public static ThreadLocal<String> clientIp = new ThreadLocal<String>();

	/**
	 * 本地应用名称
	 */
	public static String localApplicationName;
	
	/**
	 * 本地ip
	 */
	public static String localIp;

	/**
	 * 服务端应用名称
	 */
	public static ThreadLocal<String> serverApplicationName = new ThreadLocal<String>();

	/**
	 * 服务端IP
	 */
	public static ThreadLocal<String> serverIp = new ThreadLocal<String>();

	/**
	 * 服务端端口
	 */
	public static ThreadLocal<Integer> serverPort = new ThreadLocal<Integer>();

	/**
	 * [隐式参数集合]-线程独享
	 */
	public static ThreadLocal<Map<String, Object>> shadowParameterMap = new ThreadLocal<Map<String, Object>>() {

		/**
		 * 初始化
		 */
		@Override
		protected Map<String, Object> initialValue() {
			return new HashMap<>();
		}
	};

	/**
	 * 异步任务
	 */
	public static ThreadLocal<FutureTask<Object>> futureTask = new ThreadLocal<FutureTask<Object>>();

	/**
	 * [uuid]-线程独享
	 */
	public static ThreadLocal<String> uuid = new ThreadLocal<String>() {

		/**
		 * 初始化
		 */
		@Override
		protected String initialValue() {
			return UUID.randomUUID().toString();
		}
	};

	/**
	 * 泛化调用引用集合
	 */
	public static Map<String, BaseRefrence> generalBaseRefrenceMap = new HashMap<>();
}
