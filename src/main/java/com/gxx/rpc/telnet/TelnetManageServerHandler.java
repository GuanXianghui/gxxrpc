package com.gxx.rpc.telnet;

import java.util.Date;

import org.apache.log4j.Logger;

import com.gxx.rpc.core.RpcContext;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * telnet治理服务端处理类
 * @author Gxx
 */
public class TelnetManageServerHandler extends SimpleChannelInboundHandler<String> {

	/**
	 * 日志处理器
	 */
	Logger logger = Logger.getLogger(TelnetManageServerHandler.class);
	
	/**
	 * 客户端创建连接
	 */
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		ctx.write("欢迎使用telnet治理gxxrpc服务[" + RpcContext.localApplicationName + "-" + RpcContext.localIp + "]!\r\n");
		ctx.write("当前时间:[" + new Date() + "].\r\n");
		ctx.write(TelnetManageUtil.WELCOME_RESPONSE);
		ctx.flush();
	}

	/**
	 * 异常捕获关闭连接
	 */
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		logger.error("异常发生!", cause);
		ctx.close();
	}

	/**
	 * 读取请求
	 */
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, String request) throws Exception {
		/**
		 * 读取并解析请求
		 */
		String response;
		boolean close = false;
		/**
		 * 退出请求
		 */
		if (TelnetManageUtil.EXIT_REQUEST.equalsIgnoreCase(request) ||
				TelnetManageUtil.QUIT_REQUEST.equalsIgnoreCase(request)) {
			response = TelnetManageUtil.EXIT_RESPONSE;
			close = true;
		} else {
			/**
			 * 治理请求
			 */
			response = TelnetManageUtil.manageRequest(request);
		}

		/**
		 * 写出应答
		 */
		ChannelFuture future = ctx.write(response);
		ctx.flush();
		
		/**
		 * 关闭连接
		 */
		if (close) {
			future.addListener(ChannelFutureListener.CLOSE);
		}
	}
}