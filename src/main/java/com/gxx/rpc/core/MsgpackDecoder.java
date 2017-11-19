package com.gxx.rpc.core;

import java.util.List;

import org.msgpack.MessagePack;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

/**
 * MessagePack解码器
 * @author Gxx
 */
public class MsgpackDecoder extends MessageToMessageDecoder<ByteBuf> {

	/**
	 * 解码
	 * 
	 * @param ctx
	 * @param msg
	 * @param out
	 */
	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
		final byte[] byteArray;
		final int length = msg.readableBytes();
		byteArray = new byte[length];
		msg.getBytes(msg.readerIndex(), byteArray, 0, length);
		MessagePack messagePack = new MessagePack();
		out.add(messagePack.read(byteArray, RpcRequest.class));// 指定[编码对象]的类型 -
																// 这个很重要
	}

}
