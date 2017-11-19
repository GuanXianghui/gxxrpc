package com.gxx.rpc.core;

import java.util.concurrent.TimeUnit;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.ReadTimeoutHandler;

/** 
 * rpc读超时处理类
 * @author Gxx
 */
public class RpcReadTimeoutHandler extends ReadTimeoutHandler {

	/**
	 * rpc客户端处理类
	 */
	RpcClientHandler handler = null;
	
	/**
	 * 超时时间
	 */
	private long timeout;
	
	/**
	 * 构造方法
	 * @param timeout
	 * @param unit
	 */
	public RpcReadTimeoutHandler(RpcClientHandler handler, long timeout, TimeUnit unit) {
		super(timeout, unit);
		this.handler = handler;
		this.timeout = timeout;
	}

	/**
	 * 超时
	 * @param ctx
	 * @throws Exception
	 */
	@Override
	protected void readTimedOut(ChannelHandlerContext ctx) throws Exception {
		/**
		 * 先设置返回对象
		 */
		RpcResponse rpcResponse = new RpcResponse();
		rpcResponse.setSuccess(false);
		rpcResponse.setThrowable(new RuntimeException("访问服务端[" + timeout + "毫秒]超时！"));
		handler.response = rpcResponse;
		/**
		 * 再执行父类方法
		 */
		super.readTimedOut(ctx);
	}

	/**
	 * 状态变化
	 * @param state
	 * @param first
	 * @return
	 */
	@Override
	protected IdleStateEvent newIdleStateEvent(IdleState state, boolean first) {
		return super.newIdleStateEvent(state, first);
	}
}
