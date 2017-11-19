package com.gxx.rpc.service;

import org.apache.log4j.Logger;

import com.gxx.rpc.core.Interceptor;
import com.gxx.rpc.core.RpcRequest;
import com.gxx.rpc.spring.BaseRefrence;

/** 
 * 用户服务拦截器
 * @author Gxx
 */
public class UserServiceInterceptor implements Interceptor {

	/**
	 * 日志记录器
	 */
	Logger logger = Logger.getLogger(UserServiceInterceptor.class);
	
	/**
	 * rpc调用前执行
	 * @param baseRefrence
	 * @param rpcRequest
	 */
	public void preHandle(BaseRefrence baseRefrence, RpcRequest rpcRequest) {
		logger.info("[用户服务拦截器]-rpc调用前执行:" + baseRefrence + "," + rpcRequest);
	}

	/**
	 * rpc调用后执行
	 * @param baseRefrence
	 * @param rpcRequest
	 * @param result
	 */
	public void postHandle(BaseRefrence baseRefrence, RpcRequest rpcRequest, Object result) {
		logger.info("[用户服务拦截器]-rpc调用后执行:" + baseRefrence + "," + rpcRequest + "," + result);
	}

	/**
	 * rpc调用异常后执行
	 * @param baseRefrence
	 * @param rpcRequest
	 * @param throwable
	 */
	public void exceptionHandle(BaseRefrence baseRefrence, RpcRequest rpcRequest, Throwable throwable) {
		logger.error("[用户服务拦截器]-rpc调用异常后执行:" + baseRefrence + "," + rpcRequest, throwable);
	}

}
