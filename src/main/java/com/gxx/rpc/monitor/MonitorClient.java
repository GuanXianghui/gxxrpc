package com.gxx.rpc.monitor;

import org.apache.log4j.Logger;

import com.gxx.rpc.spring.Monitor;
import com.gxx.rpc.spring.SpringUtil;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

/**
 * 监控客户端
 * @author Gxx
 */
public class MonitorClient {

	Logger logger = Logger.getLogger(MonitorClient.class);

	private MonitorClientHandler handler = null;
	private EventLoopGroup workerGroup = null;
	private ChannelFuture channelFuture = null;
	
	/**
	 * 构造方法
	 */
	public MonitorClient() {}
	
	/**
	 * 初始化标识
	 */
	private boolean isInit = false;
	
	/**
	 * 初始化
	 * @throws Exception
	 */
	private synchronized void init() throws Exception {
		if(isInit) {
			logger.info("已初始化连接！");
			return;
		}
		
		/**
		 * 监控
		 */
		Monitor monitor = (Monitor)SpringUtil.getApplicationContext().getBean("monitor");
		
		/**
		 * 连接服务提供方
		 */
		connect(monitor.getIp(), monitor.getPort());
		
		/**
		 * 不关闭 workerGroup.shutdownGracefully();
		 * 后续通过jdk的ShutdownHook来优雅停机
		 */
		logger.info("初始化连接完成！");
		isInit = true;
	}
	
	/**
	 * 监控调用
	 * @param monitor
	 * @return
	 * @throws Exception
	 */
	public void runRpc(MonitorObject monitorObject) throws Exception {
		/**
		 * 初始化
		 */
		init();
		/**
		 * 发送监控请求
		 */
		channelFuture.channel().writeAndFlush(monitorObject).sync();
		channelFuture.channel().closeFuture().sync();
		/**
		 * 不接收监控响应，纯异步发送监控
		 */
		//handler.getResponse();

		/**
		 * 关闭资源
		 */
		if(null != workerGroup) {
			workerGroup.shutdownGracefully();
		}
	}

	/**
	 * 连接服务提供方
	 * @param host
	 * @param port
	 * @throws Exception
	 */
	private void connect(String host, int port) throws Exception {
		logger.info("rpc客户端连接[" + host + ":" + port + "]...");
		handler = new MonitorClientHandler();
		//客户端线程
		workerGroup = new NioEventLoopGroup(4);

		Bootstrap b = new Bootstrap();
		b.group(workerGroup);
		b.channel(NioSocketChannel.class);
		b.option(ChannelOption.SO_KEEPALIVE, true);
		b.handler(new ChannelInitializer<SocketChannel>() {
			@Override
			public void initChannel(SocketChannel ch) throws Exception {
				/**
				 * 注册handler ChannelInboundHandler按照注册的先后，[顺序]执行
				 * ChannelOutboundHandler按照注册的先后，[逆序]执行
				 * 根据以下注册顺序，最终程序执行顺序(顺序不能乱写)：[In][1]->[In][2]->[Out][2]->[Out][
				 * 1]
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
				 * Decoder-msgpack解码器 [有问题] Kyro Decoder-kyro解码器 [未完成]
				 */
				ch.pipeline().addLast("Object Decoder",
						new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(null)));
				// ch.pipeline().addLast("Msgpack Decoder", new
				// MsgpackDecoder());
				// ch.pipeline().addLast("Kyro Decoder", new KyroMsgDecoder());
				/**
				 * [Out][1] 编码 LengthFieldPrepender -
				 * 解决TCP拆包和粘包问题(配合LengthFieldBasedFrameDecoder一起用)
				 * 计算当前待发送消息的二进制字节长度，将该长度添加到ByteBuf的缓冲区头中
				 */
				ch.pipeline().addLast(new LengthFieldPrepender(2));
				/**
				 * [Out][2] 编码 Object Encoder-jdk内置序列化编码器 Msgpack
				 * Encoder-msgpack编码器 [有问题] Kyro Encoder-kyro编码器 [未完成]
				 */
				ch.pipeline().addLast("Object Encoder", new ObjectEncoder());
				// ch.pipeline().addLast("Msgpack Encoder", new
				// MsgpackEncoder());
				// ch.pipeline().addLast("Kyro Encoder", new KyroMsgEncoder());
				/**
				 * 处理器 解码+编码 收发信息
				 */
				ch.pipeline().addLast(handler);
			}
		});

		channelFuture = b.connect(host, port).sync();
	}
}