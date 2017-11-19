package com.gxx.rpc.core;

import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.gxx.rpc.monitor.MonitorClient;
import com.gxx.rpc.monitor.MonitorObject;
import com.gxx.rpc.spring.BaseRefrence;
import com.gxx.rpc.spring.BaseService;
import com.gxx.rpc.spring.Monitor;
import com.gxx.rpc.spring.SpringUtil;
import com.gxx.rpc.utils.AsynUtils;
import com.gxx.rpc.utils.LoadBalanceUtil;

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
 * rpc客户端
 * @author Gxx
 */
public class RpcClient {

	Logger logger = Logger.getLogger(RpcClient.class);
	
	/**
	 * 默认5秒超时
	 */
	public static final long DEFAULT_TIMEOUT = 5000l;
	
	/**
	 * 超时毫秒数
	 */
	private long timeout;

	/**
	 * netty关键类
	 */
	private RpcClientHandler handler = null;
	private EventLoopGroup workerGroup = null;
	private ChannelFuture channelFuture = null;
	
	/**
	 * 构造方法
	 */
	public RpcClient() {}
	
	/**
	 * 初始化标识
	 */
	private boolean isInit = false;
	
	/**
	 * 初始化
	 * @param baseService
	 * @param rpcRequest
	 * @throws Exception
	 */
	private synchronized void init(BaseService baseService, RpcRequest rpcRequest) throws Exception {
		if(isInit) {
			logger.info("已初始化连接！");
			return;
		}
		
		/**
		 * 设置上下文-服务端信息-清空
		 */
		RpcContext.serverApplicationName.set(null);
		RpcContext.serverIp.set(null);
		RpcContext.serverPort.set(0);
		
		if(null == baseService) {
			/**
			 * 经过负载均衡算法选择服务
			 */
			baseService = LoadBalanceUtil.chooseService(rpcRequest.getClassName());
		}

		/**
		 * 设置上下文-服务端信息
		 */
		RpcContext.serverApplicationName.set(baseService.getApplicationName());
		RpcContext.serverIp.set(baseService.getIp());
		RpcContext.serverPort.set(baseService.getPort());
		
		/**
		 * 连接服务提供方
		 */
		connect(baseService.getIp(), baseService.getPort());
		
		/**
		 * 不关闭 workerGroup.shutdownGracefully();
		 * 后续通过jdk的ShutdownHook来优雅停机
		 */
		logger.info("初始化连接完成！");
		isInit = true;
	}
	
	/**
	 * rpc调用
	 * @param baseRefrence
	 * @param rpcRequest
	 * @return
	 * @throws Exception
	 */
	public RpcResponse runRpc(BaseRefrence baseRefrence, RpcRequest rpcRequest) throws Exception {
		/**
		 * rpc调用指定服务
		 */
		return runRpc(baseRefrence, null, rpcRequest);
	}
	
	/**
	 * rpc调用指定服务
	 * @param baseRefrence
	 * @param baseService
	 * @param rpcRequest
	 * @return
	 * @throws Exception
	 */
	public RpcResponse runRpc(BaseRefrence baseRefrence, BaseService baseService, RpcRequest rpcRequest) throws Exception {
		/**
		 * 超时时间
		 */
		timeout = baseRefrence.getTimeout();
		if(timeout <= 0) {
			timeout = DEFAULT_TIMEOUT;
		}
		
		/**
		 * 开始时间
		 */
		Date beginTime = new Date();
		RpcResponse rpcResponse = null;
		try {
			/**
			 * 初始化
			 */
			init(baseService, rpcRequest);
			/**
			 * 发送rpc请求
			 */
			channelFuture.channel().writeAndFlush(rpcRequest).sync();
			channelFuture.channel().closeFuture().sync();
			/**
			 * 接收rpc响应
			 */
			rpcResponse = handler.getResponse();
			return rpcResponse;
		} catch (Throwable throwable) {
			/**
			 * 设置异常响应
			 */
			rpcResponse = new RpcResponse();
			rpcResponse.setSuccess(false);
			rpcResponse.setThrowable(throwable);
			/**
			 * 抛出异常
			 */
			throw throwable;
		} finally {
			/**
			 * 判断是否监控
			 */
			if(SpringUtil.getApplicationContext().containsBean("monitor")) {
				Monitor monitor = (Monitor)SpringUtil.getApplicationContext().getBean("monitor");
				if(null != monitor && null != monitor.getIp() && !"".equals(monitor.getIp())
						&& 0 < monitor.getPort()) {
					/**
					 * 结束时 和 耗时
					 */
					Date endTime = new Date();
					long usedTime = endTime.getTime() - beginTime.getTime();
					
					/**
					 * 获取不到服务，设置
					 */
					if(null == baseService) {
						baseService = new BaseService();
						baseService.setApplicationName(RpcContext.serverApplicationName.get());
						baseService.setIp(RpcContext.serverIp.get());
						baseService.setPort(RpcContext.serverPort.get());
					}
					
					/**
					 * 监控数据
					 */
					final MonitorObject monitorObject = new MonitorObject();
					monitorObject.setBaseRefrence(baseRefrence);
					monitorObject.setBaseService(baseService);
					monitorObject.setRpcRequest(rpcRequest);
					monitorObject.setRpcResponse(rpcResponse);
					monitorObject.setBeginTime(beginTime);
					monitorObject.setEndTime(endTime);
					monitorObject.setUsedTime(usedTime);
					
					AsynUtils.getInstance().submitTask(new Callable<Object>() {
						@Override
						public Object call() throws Exception {
							logger.info("[异步调用监控]:" + monitorObject);
							new MonitorClient().runRpc(monitorObject);
							return null;
						}
					});
				}
			}
			/**
			 * 关闭资源
			 */
			if(null != workerGroup) {
				workerGroup.shutdownGracefully();
			}
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
		handler = new RpcClientHandler();
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
				 * 根据以下注册顺序，最终程序执行顺序(顺序不能乱写)：[In][1]->[In][2]->[Out][2]->[Out][1]
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
				ch.pipeline().addLast("Object Decoder", new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(null)));
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
				 * 只配置读超时(服务器处理有时候便慢)，不配置写超时(写一般量不大)
				 */
				ch.pipeline().addLast(new RpcReadTimeoutHandler(handler, timeout, TimeUnit.MILLISECONDS));
				//ch.pipeline().addLast(new WriteTimeoutHandler(2000, TimeUnit.MILLISECONDS));
				//ch.pipeline().addLast(new IdleStateHandler(2000,2000,2000, TimeUnit.MILLISECONDS));
				/**
				 * 处理器 解码+编码 收发信息
				 */
				ch.pipeline().addLast(handler);
			}
		});

		channelFuture = b.connect(host, port).sync();
	}
}