package com.gxx.rpc.cache;

import com.gxx.rpc.core.RpcRequest;
import com.gxx.rpc.core.RpcResponse;

/** 
 * 缓存对象
 * @author Gxx
 */
public class CacheObject {
	/**
	 * 请求
	 */
	private RpcRequest rpcRequest;

	/**
	 * 结果
	 */
	private RpcResponse rpcResponse;
	
	/**
	 * 开始时间
	 */
	private long startTime;
	
	/**
	 * 结束时间
	 */
	private long endTime;
	
	/**
	 * 缓存时间
	 */
	private long cacheTime;

	/**
	 * 构造函数
	 * @param rpcRequest
	 * @param rpcResponse
	 * @param startTime
	 * @param endTime
	 * @param cacheTime
	 */
	public CacheObject(RpcRequest rpcRequest, RpcResponse rpcResponse, long startTime, long endTime, long cacheTime) {
		this.rpcRequest = rpcRequest;
		this.rpcResponse = rpcResponse;
		this.startTime = startTime;
		this.endTime = endTime;
		this.cacheTime = cacheTime;
	}

	public RpcRequest getRpcRequest() {
		return rpcRequest;
	}

	public void setRpcRequest(RpcRequest rpcRequest) {
		this.rpcRequest = rpcRequest;
	}

	public RpcResponse getRpcResponse() {
		return rpcResponse;
	}

	public void setRpcResponse(RpcResponse rpcResponse) {
		this.rpcResponse = rpcResponse;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	public long getCacheTime() {
		return cacheTime;
	}

	public void setCacheTime(long cacheTime) {
		this.cacheTime = cacheTime;
	}

	@Override
	public String toString() {
		return "CacheObject [rpcRequest=" + rpcRequest + ", rpcResponse=" + rpcResponse + ", startTime=" + startTime
				+ ", endTime=" + endTime + ", cacheTime=" + cacheTime + "]";
	}
}
