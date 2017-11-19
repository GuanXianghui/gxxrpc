package com.gxx.rpc.telnet;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.gxx.rpc.cache.CacheUtil;
import com.gxx.rpc.core.RpcContext;
import com.gxx.rpc.register.RegisterUtils;
import com.gxx.rpc.spring.BaseRefrence;
import com.gxx.rpc.spring.BaseService;
import com.gxx.rpc.spring.SpringUtil;
import com.gxx.rpc.utils.LoadBalanceUtil;
import com.gxx.rpc.utils.ServiceDegradeUtil;

/** 
 * telnet服务治理工具类
 * @author Gxx
 */
public class TelnetManageUtil {

	/**
	 * 日志处理器
	 */
	public static Logger logger = Logger.getLogger(TelnetManageUtil.class);
	
	/**
	 * 常量
	 */
	public static final String COLON = ":";//冒号
	public static final String LINE_SEPERATOR = "\r\n";//换行符
	public static final String EXIT_REQUEST = "exit";//退出请求
	public static final String QUIT_REQUEST = "quit";//退出请求
	public static final String EXIT_RESPONSE = "再见!" + LINE_SEPERATOR;//退出响应
	public static final String LOCAL_APP_NAME_REQUEST = "local_app_name";//查看本地应用名称
	public static final String LOCAL_IP_REQUEST = "local_ip";//查看本地ip
	public static final String LOCAL_REFRENCE_LIST_REQUEST = "local_refrence_list";//查看本地引用列表
	public static final String QUERY_SERVICE_LIST_BY_REFRENCE_REQUEST = "query_service_list_by_refrence";//根据本地引用查找服务列表
	public static final String LOCAL_SERVICE_LIST_REQUEST = "local_service_list";//查看本地服务列表
	public static final String REFRENCE_INVOKE_LIST_REQUEST = "refrence_invoke_list";//查看引用调用次数列表
	public static final String CACHE_COUNT_REQUEST = "cache_count";//查看缓存大小
	public static final String CACHE_CLEAR_REQUEST = "cache_clear";//缓存清空
	public static final String CACHE_CLEAR_TO_REQUEST = "cache_clear_to";//缓存清除到指定大小
	public static final String SERVICE_DEGRADE_LIST_REQUEST = "service_degrade_list";//查看降级服务列表
	public static final String SERVICE_DEGRADE_ADD_REQUEST = "service_degrade_add";//降级服务新增
	public static final String SERVICE_DEGRADE_REMOVE_REQUEST = "service_degrade_remove";//降级服务删除
	public static final String WELCOME_RESPONSE = "您可以尝试输入以下治理命令:" + LINE_SEPERATOR //欢迎响应
			+ "=================" + LINE_SEPERATOR
			+ "[local_app_name]:查看本地应用名称" + LINE_SEPERATOR
			+ "[local_ip]:查看本地ip" + LINE_SEPERATOR
			+ "[local_refrence_list]:查看本地引用列表" + LINE_SEPERATOR
			+ "[query_service_list_by_refrence:引用名称]:根据本地引用查找服务列表" + LINE_SEPERATOR
			+ "[local_service_list]:查看本地服务列表" + LINE_SEPERATOR
			+ "[refrence_invoke_list]:查看引用调用次数列表" + LINE_SEPERATOR
			+ "[cache_count]:查看缓存大小" + LINE_SEPERATOR
			+ "[cache_clear]:缓存清空" + LINE_SEPERATOR
			+ "[cache_clear_to:缓存大小]:缓存清除到指定大小" + LINE_SEPERATOR
			+ "[service_degrade_list]:查看降级服务列表" + LINE_SEPERATOR
			+ "[service_degrade_add:引用名称]:降级服务新增" + LINE_SEPERATOR
			+ "[service_degrade_remove:引用名称]:降级服务删除" + LINE_SEPERATOR
			+ "[exit/quit]:退出" + LINE_SEPERATOR
			+ "=================" + LINE_SEPERATOR ;
	
	/**
	 * 治理请求
	 * @param request
	 * @return
	 */
	public static String manageRequest(String request) {
		if(LOCAL_APP_NAME_REQUEST.equals(request)) {//查看本地应用名称
			return "本地应用名称" + COLON + RpcContext.localApplicationName + LINE_SEPERATOR;
		} else if(LOCAL_IP_REQUEST.equals(request)) {//查看本地ip
			return "本地ip" + COLON + RpcContext.localIp + LINE_SEPERATOR;
		} else if(LOCAL_REFRENCE_LIST_REQUEST.equals(request)) {//查看本地引用列表
			/**
			 * spring管理的所有引用
			 */
			Map<String, BaseRefrence> refrenceMap = (Map<String, BaseRefrence>)SpringUtil.
					getApplicationContext().getBeansOfType(BaseRefrence.class);
			return "本地引用列表" + COLON + refrenceMap.values().toString() + LINE_SEPERATOR;
		} else if(request.startsWith(QUERY_SERVICE_LIST_BY_REFRENCE_REQUEST + COLON)) {//根据本地引用查找服务列表
			/**
			 * 获取本地引用名称
			 */
			String refrenceName = request.substring(request.indexOf(COLON) + 1);
			/**
			 * 获取引用服务
			 */
			List<BaseService> serviceList = RegisterUtils.getRefrenceServiceMap().get(refrenceName);
			if(null == serviceList) {
				serviceList = new ArrayList<>();
			}
			return "根据本地引用[" + refrenceName + "]查找服务列表" + COLON + serviceList.toString() + LINE_SEPERATOR;
		} else if(LOCAL_SERVICE_LIST_REQUEST.equals(request)) {//查看本地服务列表
			/**
			 * spring管理的所有服务
			 */
			Map<String, BaseService> serviceMap = (Map<String, BaseService>)SpringUtil.
					getApplicationContext().getBeansOfType(BaseService.class);
			return "本地服务列表" + COLON + serviceMap.values().toString() + LINE_SEPERATOR;
		} else if(REFRENCE_INVOKE_LIST_REQUEST.equals(request)) {//查看引用调用次数列表
			return "引用调用次数列表" + COLON + LoadBalanceUtil.getRefrenceInvokeMap().toString() + LINE_SEPERATOR;
		} else if(CACHE_COUNT_REQUEST.equals(request)) {//查看缓存大小
			return "缓存大小" + COLON + CacheUtil.getCacheSize() + LINE_SEPERATOR;
		} else if(CACHE_CLEAR_REQUEST.equals(request)) {//缓存清空
			/**
			 * 清空缓存
			 */
			CacheUtil.clearCache(0);
			return "缓存清空成功." + LINE_SEPERATOR;
		} else if(request.startsWith(CACHE_CLEAR_TO_REQUEST + COLON)) {//缓存清除到指定大小
			/**
			 * 获取大小
			 */
			String size = request.substring(request.indexOf(COLON) + 1);
			/**
			 * 清除缓存
			 */
			CacheUtil.clearCache(Integer.parseInt(size));
			return "缓存清除到指定大小成功." + LINE_SEPERATOR;
		} else if(SERVICE_DEGRADE_LIST_REQUEST.equals(request)) {//查看降级服务列表
			return "降级服务列表" + COLON + ServiceDegradeUtil.degradeServiceList.toString() + LINE_SEPERATOR;
		} else if(request.startsWith(SERVICE_DEGRADE_ADD_REQUEST + COLON)) {//降级服务新增
			/**
			 * 获取引用名称
			 */
			String refrence = request.substring(request.indexOf(COLON) + 1);
			/**
			 * 降级服务新增
			 */
			ServiceDegradeUtil.add(refrence);
			return "降级服务新增成功." + LINE_SEPERATOR;
		} else if(request.startsWith(SERVICE_DEGRADE_REMOVE_REQUEST + COLON)) {//降级服务删除
			/**
			 * 获取引用名称
			 */
			String refrence = request.substring(request.indexOf(COLON) + 1);
			/**
			 * 降级服务删除
			 */
			ServiceDegradeUtil.remove(refrence);
			return "降级服务删除成功." + LINE_SEPERATOR;
		} else {
			return WELCOME_RESPONSE;
		}
	}
}
