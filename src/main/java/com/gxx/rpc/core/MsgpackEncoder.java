package com.gxx.rpc.core;

import org.msgpack.MessagePack;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * MessagePack编码器
 * @author Gxx
 */
public class MsgpackEncoder extends MessageToByteEncoder<RpcRequest> {

	/**
	 * 编码
	 * 
	 * @param ctx
	 * @param msg 指定[编码对象]的类型 - 这个很重要
	 * @param out
	 */
	@Override
	protected void encode(ChannelHandlerContext ctx, RpcRequest msg, ByteBuf out) throws Exception {
		MessagePack messagePack = new MessagePack();
		byte[] byteArray = messagePack.write(msg);
		out.writeBytes(byteArray);
	}
}
