package com.gxx.rpc.core;

import org.apache.log4j.Logger;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * rpc客户端处理类
 * @author Gxx
 */
public class RpcClientHandler extends ChannelInboundHandlerAdapter {

	Logger logger = Logger.getLogger(RpcClientHandler.class);

	RpcResponse response;

	public RpcResponse getResponse() {
		return response;
	}

	public void setResponse(RpcResponse response) {
		this.response = response;
	}

	// 连接成功后，向server发送消息
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		//logger.info("channelActive");
		// String msg = "hello Server!";
		// //msg += System.lineSeparator();
		// logger.info("向服务端写内容:" + msg);
		// ByteBuf encoded = ctx.alloc().buffer(4 * msg.length());
		// encoded.writeBytes(msg.getBytes());
		// ctx.write(encoded);

		// RpcObject rpcObject = new RpcObject();
		// rpcObject.setUserName("关向辉你好！");
		// rpcObject.setPassword("123qwe");
		// logger.info(rpcObject.toString());
		// ctx.write(rpcObject);
		// ctx.flush();
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		//logger.info("channelRead");
		// ByteBuf result = (ByteBuf) msg;
		// byte[] result1 = new byte[result.readableBytes()];
		// result.readBytes(result1);
		// logger.info("客户端接受到内容:" + new String(result1));
		// result.release();
		// response = new String(result1);

		response = (RpcResponse) msg;
		//logger.info("返回:" + response);
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		//logger.info("channelReadComplete");
		ctx.flush();
		ctx.close();//复用，读完一次不用关闭
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		//logger.info("exceptionCaught");
		// 当出现异常就关闭连接
		cause.printStackTrace();
		ctx.close();
	}

	@Override
	public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
		//logger.info("channelRegistered");
	}

	@Override
	public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
		//logger.info("channelUnregistered");
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		//logger.info("channelInactive");
	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		//logger.info("userEventTriggered,evt:" + evt);
	}

	@Override
	public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
		//logger.info("channelWritabilityChanged");
	}
}