package com.gxx.rpc.core;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoException;
import com.esotericsoftware.kryo.io.Input;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

/**
 * Kyro解码器
 * @author Gxx
 */
public class KyroMsgDecoder extends ByteToMessageDecoder {

	public static final int HEAD_LENGTH = 4;

	private Kryo kryo = new Kryo();

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

		if (in.readableBytes() < HEAD_LENGTH) { // 这个HEAD_LENGTH是我们用于表示头长度的字节数。
												// 由于Encoder中我们传的是一个int类型的值，所以这里HEAD_LENGTH的值为4.
			return;
		}
		in.markReaderIndex(); // 我们标记一下当前的readIndex的位置
		int dataLength = in.readInt(); // 读取传送过来的消息的长度。ByteBuf
										// 的readInt()方法会让他的readIndex增加4
		if (dataLength < 0) { // 我们读到的消息体长度为0，这是不应该出现的情况，这里出现这情况，关闭连接。
			ctx.close();
		}

		if (in.readableBytes() < dataLength) { // 读到的消息体长度如果小于我们传送过来的消息长度，则resetReaderIndex.
												// 这个配合markReaderIndex使用的。把readIndex重置到mark的地方
			in.resetReaderIndex();
			return;
		}

		byte[] body = new byte[dataLength]; // 传输正常
		in.readBytes(body);
		Object o = convertToObject(body); // 将byte数据转化为我们需要的对象
		out.add(o);
	}

	private Object convertToObject(byte[] body) {

		Input input = null;
		ByteArrayInputStream bais = null;
		try {
			bais = new ByteArrayInputStream(body);
			input = new Input(bais);

			return kryo.readObject(input, RpcRequest.class);
		} catch (KryoException e) {
			e.printStackTrace();
		} finally {
			if (null != input) {
				input.close();
			}
			if (null != bais) {
				try {
					bais.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

}