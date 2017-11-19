package com.gxx.rpc.core;

import java.util.Map;
import java.util.UUID;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.gxx.rpc.spring.BaseService;
import com.gxx.rpc.spring.SpringUtil;
import com.gxx.rpc.utils.ServiceUtil;
import com.gxx.rpc.utils.TypeParseUtil;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * rpc服务端处理类
 * @author Gxx
 */
public class RpcServerHandler extends ChannelInboundHandlerAdapter {

	Logger logger = Logger.getLogger(RpcServerHandler.class);

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		logger.info("channelRead");

		RpcResponse rpcResponse = new RpcResponse();
		
		try {
			RpcRequest rpcRequest = (RpcRequest) msg;
			logger.info("服务端接受到内容:" + rpcRequest);
			
			/**
			 * 刷新[uuid]-线程独享
			 */
			RpcContext.uuid.set(UUID.randomUUID().toString());

			Object serviceImpl;//服务实现类

			if (!ServiceUtil.serviceImplMap.containsKey(rpcRequest.getClassName())) {
				
				/**
				 * spring管理的所有服务
				 */
				Map<String, BaseService> serviceMap = (Map<String, BaseService>)SpringUtil.
						getApplicationContext().getBeansOfType(BaseService.class);
				logger.info("spring管理的所有服务:" + serviceMap);
				
				BaseService baseService = null;//服务
				
				for(String key : serviceMap.keySet()) {
					if(serviceMap.get(key).getName().equals(rpcRequest.getClassName())) {
						baseService = serviceMap.get(key);
						logger.info("根据[" + rpcRequest.getClassName() + "]查找到服务信息:" + baseService);
					}
				}
				
				if(null == baseService) {
					throw new RuntimeException("无可用服务");
				}
				
				/**
				 * 优先判断ref
				 */
				if(null != baseService.getRef() && !"".equals(baseService.getRef())) {
					if(SpringUtil.getApplicationContext().containsBean(baseService.getRef())) {
						serviceImpl = SpringUtil.getApplicationContext().getBean(baseService.getRef());
					} else {
						throw new RuntimeException("无可用服务");
					}
				} else {
					/**
					 * 再判断impl
					 */
					serviceImpl = Class.forName(baseService.getImpl()).newInstance();
				}
				
				logger.info("服务端serviceImpl:" + serviceImpl);
				ServiceUtil.serviceImplMap.put(rpcRequest.getClassName(), serviceImpl);
			} else {
				serviceImpl = ServiceUtil.serviceImplMap.get(rpcRequest.getClassName());
			}
			
			/**
			 * 设置上下文-客户端信息
			 */
			RpcContext.clientApplicationName.set(rpcRequest.getClientApplicationName());
			RpcContext.clientIp.set(rpcRequest.getClientIp());
			
			/**
			 * 获取客户端送的隐式参数
			 */
			RpcContext.shadowParameterMap.set(rpcRequest.getShadowParameterMap());
			
			/**
			 * 转类型字符串到类型对象
			 */
			Map<String, Object> map = TypeParseUtil.parseTypeString2Class(rpcRequest.getTypes(), rpcRequest.getArgs());
			Class<?>[] classTypes = (Class<?>[])map.get("classTypes");
			Object[] args = (Object[])map.get("args");
			
			/**
			 * 反射调用
			 */
			Object result = serviceImpl.getClass().getMethod(rpcRequest.getMethodName(), classTypes)
					.invoke(serviceImpl, args);
			logger.info("服务端result:" + result);
			
			/**
			 * 判断泛化调用
			 */
			if(rpcRequest.isGeneral()) {
				result = JSON.toJSONString(result);
			}
			
			rpcResponse.setSuccess(true);
			rpcResponse.setResult(result);
		} catch (Throwable throwable) {
			logger.error("异常发生！", throwable);
			rpcResponse.setSuccess(false);
			rpcResponse.setThrowable(throwable);
		}

		logger.info("向客户端写内容:" + rpcResponse);
		ctx.write(rpcResponse);
		ctx.flush();
		// ctx.close();
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		logger.info("channelReadComplete");
		ctx.flush();
		// ctx.close();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		logger.info("exceptionCaught");
		// 当出现异常就关闭连接
		cause.printStackTrace();
		ctx.close();
	}

	@Override
	public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
		logger.info("channelRegistered");
	}

	@Override
	public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
		logger.info("channelUnregistered");
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		logger.info("channelActive");
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		logger.info("channelInactive");
	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		logger.info("userEventTriggered");
	}

	@Override
	public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
		logger.info("channelWritabilityChanged");
	}
}