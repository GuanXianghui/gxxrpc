package com.gxx.rpc.cache;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.gxx.rpc.core.RpcRequest;
import com.gxx.rpc.core.RpcResponse;

/** 
 * 缓存工具类
 * @author Gxx
 */
public class CacheUtil {
	
	/**
	 * 日志记录器
	 */
	private static Logger logger = Logger.getLogger(CacheUtil.class);
	/**
	 * 缓存大小，超过则使用FIFO缓存失效策略
	 */
	private static final int CACHE_SIZE = 1000;
	/**
	 * 缓存集合
	 */
	private static Map<String, CacheObject> cacheMap = new HashMap<>();
	/**
	 * 键值集合，用于FIFO缓存失效策略
	 */
	private static List<String> cacheKeyList = new ArrayList<>();
	
	/**
	 * 获取缓存
	 * @param rpcRequest
	 * @return
	 */
	public synchronized static CacheObject getCache(RpcRequest rpcRequest) {
		CacheObject cacheObject = cacheMap.get(rpcRequest.toString());
		if(null == cacheObject) {
			logger.info("缓存中不存在:[" + rpcRequest.toString() + "]！");
			return null;
		}
		long nowTime = new Date().getTime();
		if(nowTime > cacheObject.getEndTime()) {
			/**
			 * 过期，清除
			 */
			logger.info("缓存失效:[" + rpcRequest.toString() + "]！");
			cacheKeyList.remove(rpcRequest.toString());
			cacheMap.remove(rpcRequest.toString());
			return null;
		}
		logger.info("获取到缓存:[" + rpcRequest.toString() + "]:[" + cacheObject + "]！");
		return cacheObject;
	}
	
	/**
	 * 设置缓存
	 * @param rpcRequest 请求
	 * @param rpcResponse 响应
	 * @param cacheTime 缓存时间
	 */
	public synchronized static void setCache(RpcRequest rpcRequest, RpcResponse rpcResponse, long cacheTime) {
		long startTime = new Date().getTime();
		long endTime = startTime + cacheTime*1000;//需要乘以1000转成毫秒数
		CacheObject cacheObject = new CacheObject(rpcRequest, rpcResponse, startTime, endTime, cacheTime);
		cacheKeyList.add(rpcRequest.toString());
		cacheMap.put(rpcRequest.toString(), cacheObject);
		logger.info("设置缓存:[" + rpcRequest.toString() + "]:[" + cacheObject + "]！");
		/**
		 * 清除缓存到指定大小
		 */
		clearCache(CACHE_SIZE);
	}
	
	/**
	 * 获取缓存大小
	 * @return
	 */
	public static long getCacheSize() {
		return cacheKeyList.size();
	}
	
	/**
	 * 清除缓存到指定大小
	 * @param size
	 */
	public synchronized static void clearCache(int size) {
		if(size <= 0) {
			logger.info("清空缓存.");
			cacheKeyList.clear();
			cacheMap.clear();
		} else {
			while(cacheKeyList.size() > size) {
				String key = cacheKeyList.remove(0);
				logger.info("FIFO清除先进的缓存:[" + key + "]！");
				cacheMap.remove(key);
			}
		}
	}
}
