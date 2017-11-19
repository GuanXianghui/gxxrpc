package com.gxx.rpc.telnet;

import org.apache.log4j.Logger;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

/**
 * telnet治理服务端
 * @author Gxx
 */
public class TelnetManageServer extends Thread {

	Logger logger = Logger.getLogger(TelnetManageServer.class);
	
	/**
	 * 端口
	 */
	private int port;

	/**
	 * 构造方法
	 * @param port
	 */
	public TelnetManageServer(int port) {
		this.port = port;
	}

	public void run() {
		logger.info("telnet治理服务端启动开始，监听端口[" + port + "]！");
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
							 * 根据以下注册顺序，最终程序执行顺序(顺序不能乱写)：[In][1]->[In][2]->[Out][1]
							 */
							/**
							 * [In][1] 解码 DelimiterBasedFrameDecoder - 换行符分隔
							 * 解决TCP拆包和粘包问题
							 */
							ch.pipeline().addLast(new DelimiterBasedFrameDecoder(65535, Delimiters.lineDelimiter()));
							/**
							 * [In][2] 解码 String Decoder-netty内置解码器
							 */
							ch.pipeline().addLast("String Decoder", new StringDecoder());
							/**
							 * [Out][1] 编码 String Encoder-netty内置编码器
							 */
							ch.pipeline().addLast("String Encoder", new StringEncoder());
							/**
							 * 处理器 解码+编码 收发信息
							 */
							ch.pipeline().addLast(new TelnetManageServerHandler());
						}
					}).option(ChannelOption.SO_BACKLOG, 128).childOption(ChannelOption.SO_KEEPALIVE, true);

			// 绑定端口，开始接收进来的连接
			ChannelFuture f = b.bind(port).sync();
			logger.info("telnet治理服务端启动完成，监听端口[" + port + "]！");
			// 等待服务器 socket 关闭 。
			f.channel().closeFuture().sync();
		} catch (Exception e) {
			logger.error("telnet治理服务端启动异常，监听端口[" + port + "]！", e);
		} finally {
			workerGroup.shutdownGracefully();
			bossGroup.shutdownGracefully();
		}
	}
}