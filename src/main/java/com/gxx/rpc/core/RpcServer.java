package com.gxx.rpc.core;

import org.apache.log4j.Logger;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

/**
 * rpc服务端
 * @author Gxx
 */
public class RpcServer extends Thread {

	Logger logger = Logger.getLogger(RpcServer.class);
	
	/**
	 * 端口
	 */
	private int port;

	/**
	 * 构造方法
	 * @param port
	 */
	public RpcServer(int port) {
		this.port = port;
	}

	public void run() {
		logger.info("rpc服务端启动开始，监听端口[" + port + "]！");
		// EventLoopGroup是用来处理IO操作的多线程事件循环器
		// bossGroup 用来接收进来的连接
		EventLoopGroup bossGroup = new NioEventLoopGroup(2);
		// workerGroup 用来处理已经被接收的连接
		EventLoopGroup workerGroup = new NioEventLoopGroup(4);
		try {
			// 启动 NIO 服务的辅助启动类
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup)
					// 配置 Channel
					.channel(NioServerSocketChannel.class).childHandler(new ChannelInitializer<SocketChannel>() {
						@Override
						public void initChannel(SocketChannel ch) throws Exception {
							/**
							 * 注册handler ChannelInboundHandler按照注册的先后，[顺序]执行
							 * ChannelOutboundHandler按照注册的先后，[逆序]执行
							 * 根据以下注册顺序，最终程序执行顺序(顺序不能乱写)：[In][1]->[In][2]->[Out]
							 * [2]->[Out][1]
							 */
							/**
							 * [In][1] 解码 LengthFieldBasedFrameDecoder -
							 * 解决TCP拆包和粘包问题(配合LengthFieldPrepender一起用)
							 * 定义了一个长度的字段来表示消息的长度，因此能够处理可变长度的消息。
							 * 将消息分为消息头和消息体，消息头固定位置增加一个表示长度的字段，通过长度字段来获取整包的信息。
							 */
							ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(65535, 0, 2, 0, 2));
							/**
							 * [In][2] 解码 Object Decoder-jdk内置序列化解码器 Msgpack
							 * Decoder-msgpack解码器 [有问题] Kyro Decoder-kyro解码器
							 * [未完成]
							 */
							ch.pipeline().addLast("Object Decoder", new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(null)));
							// ch.pipeline().addLast("Msgpack Decoder", new MsgpackDecoder());
							// ch.pipeline().addLast("Kyro Decoder", new KyroMsgDecoder());
							/**
							 * [Out][1] 编码 LengthFieldPrepender -
							 * 解决TCP拆包和粘包问题(配合LengthFieldBasedFrameDecoder一起用)
							 * 计算当前待发送消息的二进制字节长度，将该长度添加到ByteBuf的缓冲区头中
							 */
							ch.pipeline().addLast(new LengthFieldPrepender(2));
							/**
							 * [Out][2] 编码 Object Encoder-jdk内置序列化编码器 Msgpack
							 * Encoder-msgpack编码器 [有问题] Kyro Encoder-kyro编码器
							 * [未完成]
							 */
							ch.pipeline().addLast("Object Encoder", new ObjectEncoder());
							// ch.pipeline().addLast("Msgpack Encoder", new MsgpackEncoder());
							// ch.pipeline().addLast("Kyro Encoder", new KyroMsgEncoder());
							/**
							 * 处理器 解码+编码 收发信息
							 */
							ch.pipeline().addLast(new RpcServerHandler());
						}
					}).option(ChannelOption.SO_BACKLOG, 128).childOption(ChannelOption.SO_KEEPALIVE, true);

			// 绑定端口，开始接收进来的连接
			ChannelFuture f = b.bind(port).sync();
			logger.info("rpc服务端启动完成，监听端口[" + port + "]！");
			// 等待服务器 socket 关闭 。
			f.channel().closeFuture().sync();
		} catch (Exception e) {
			logger.error("rpc服务端启动异常，监听端口[" + port + "]！", e);
		} finally {
			workerGroup.shutdownGracefully();
			bossGroup.shutdownGracefully();
		}
	}
}