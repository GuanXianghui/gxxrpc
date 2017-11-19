package com.gxx.rpc.proxy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

import org.apache.log4j.Logger;

import com.gxx.rpc.cache.CacheUtil;
import com.gxx.rpc.core.ClusterFaultStrategy;
import com.gxx.rpc.core.Interceptor;
import com.gxx.rpc.core.RpcClient;
import com.gxx.rpc.core.RpcContext;
import com.gxx.rpc.core.RpcRequest;
import com.gxx.rpc.core.RpcResponse;
import com.gxx.rpc.register.RegisterUtils;
import com.gxx.rpc.spring.BaseRefrence;
import com.gxx.rpc.spring.BaseService;
import com.gxx.rpc.spring.SpringUtil;
import com.gxx.rpc.utils.AsynUtils;
import com.gxx.rpc.utils.InterceptorUtil;
import com.gxx.rpc.utils.RefrenceUtil;
import com.gxx.rpc.utils.ServiceDegradeUtil;
import com.gxx.rpc.utils.ServiceUtil;
import com.gxx.rpc.utils.TypeParseUtil;

/** 
 * 接口调用
 * @author Gxx
 */
public class InterfaceInvoke {

	/**
	 * 日志记录器
	 */
	Logger logger = Logger.getLogger(InterfaceInvoke.class);

	/**
	 * 构造方法
	 */
	public InterfaceInvoke(){}

	/**
	 * 代理调用方法
	 * @param className 服务名称
	 * @param methodName 方法名称
	 * @param types 参数类型数组
	 * @param args 参数数组
	 * @param isGeneral 是否泛化调用
	 * @param returnType 返回值类型
	 * @return
	 * @throws Throwable
	 */
	public Object invoke(String className, String methodName, String[] types, 
			Object[] args, boolean isGeneral, final Class<?> returnType) throws Throwable {
		final RpcRequest rpcRequest = new RpcRequest();
		/**
		 * 如果使用method.getDeclaringClass()，而且toString方法，就会进来java.lang.Object而不是定义的接口类
		 */
		rpcRequest.setUuid(RpcContext.uuid.get());//设置[uuid]-线程独享
		rpcRequest.setClassName(className);//服务名称
		rpcRequest.setMethodName(methodName);//方法名称
		rpcRequest.setTypes(types);//参数类型数组
		rpcRequest.setArgs(args);//参数数组
		rpcRequest.setGeneral(isGeneral);//是否泛化调用
		/**
		 * 设置隐式参数，请求后马上清空
		 */
		rpcRequest.setShadowParameterMap(new HashMap<>(RpcContext.shadowParameterMap.get()));
		RpcContext.shadowParameterMap.get().clear();
		/**
		 * 设置客户端信息
		 */
		rpcRequest.setClientApplicationName(RpcContext.localApplicationName);
		rpcRequest.setClientIp(RpcContext.localIp);
		logger.info("rpc请求信息:" + rpcRequest);

		/**
		 * 查找引用信息
		 */
		final BaseRefrence baseRefrence = RefrenceUtil.getBaseRefrence(className);
		logger.info("引用信息:" + baseRefrence);
		
		/**
		 * 判断是否拦截
		 */
		if(baseRefrence.isIntercept()) {
			Interceptor interceptor;
			if (!InterceptorUtil.interceptorMap.containsKey(baseRefrence.getInterceptor())) {
				interceptor = (Interceptor)Class.forName(baseRefrence.getInterceptor()).newInstance();
				logger.info("[拦截器]:" + interceptor);
				InterceptorUtil.interceptorMap.put(baseRefrence.getInterceptor(), interceptor);
			} else {
				interceptor = InterceptorUtil.interceptorMap.get(baseRefrence.getInterceptor());
				logger.info("[拦截器]:" + interceptor);
			}
			/**
			 * rpc调用前执行
			 */
			logger.info("[拦截器]-rpc调用前执行");
			interceptor.preHandle(baseRefrence, rpcRequest);
		}
		
		/**
		 * 判断异步调用
		 */
		if(baseRefrence.isAsync()) {
			logger.info("本次为异步调用");
			FutureTask<Object> futureTask = AsynUtils.getInstance().submitTask(new Callable<Object>() {
				@Override
				public Object call() throws Exception {
					try {
						/**
						 * 判断是否拦截，方法调用
						 */
						if(baseRefrence.isIntercept()) {
							return interceptInvoke(baseRefrence, rpcRequest, returnType);
						} else {
							return invoke(baseRefrence, rpcRequest, returnType);
						}
					} catch (Throwable e) {
						logger.error("异步调用异常发生！", e);
						throw new RuntimeException(e.getMessage());
					}
				}
			});
			
			/**
			 * 异步放入上下文中，提交调用方法后，可以从上下文中获取结果
			 */
			RpcContext.futureTask.set(futureTask);
			
			/**
			 * 判断基础类型，返回默认值，否则自动转换会报空指针
			 */
			if(TypeParseUtil.isBasicType(returnType)) {
				return TypeParseUtil.getBasicTypeDefaultValue(returnType);
			}
			
			/**
			 * 异步调用，实时返回null
			 */
			return null;
		}
		
		/**
		 * 判断是否拦截，方法调用
		 */
		if(baseRefrence.isIntercept()) {
			return interceptInvoke(baseRefrence, rpcRequest, returnType);
		} else {
			return invoke(baseRefrence, rpcRequest, returnType);
		}
	}
	
	/**
	 * 拦截方法调用
	 * @param baseRefrence
	 * @param rpcRequest
	 * @param returnType
	 * @return
	 * @throws Throwable
	 */
	private Object interceptInvoke(BaseRefrence baseRefrence, RpcRequest rpcRequest, Class<?> returnType) throws Throwable {
		/**
		 * 获取拦截器
		 */
		Interceptor interceptor;
		if (!InterceptorUtil.interceptorMap.containsKey(baseRefrence.getInterceptor())) {
			interceptor = (Interceptor)Class.forName(baseRefrence.getInterceptor()).newInstance();
			logger.info("[拦截器]:" + interceptor);
			InterceptorUtil.interceptorMap.put(baseRefrence.getInterceptor(), interceptor);
		} else {
			interceptor = InterceptorUtil.interceptorMap.get(baseRefrence.getInterceptor());
			logger.info("[拦截器]:" + interceptor);
		}
		
		try {
			/**
			 * 方法调用
			 */
			Object object = invoke(baseRefrence, rpcRequest, returnType);
			
			/**
			 * rpc调用后执行
			 */
			logger.info("[拦截器]-rpc调用后执行");
			interceptor.postHandle(baseRefrence, rpcRequest, object);
			
			return object;
		} catch (Throwable throwable) {
			/**
			 * rpc调用异常后执行
			 */
			logger.info("[拦截器]-rpc调用异常后执行");
			interceptor.exceptionHandle(baseRefrence, rpcRequest, throwable);
			throw throwable;
		}
	}
	
	/**
	 * 方法调用
	 * @param baseRefrence
	 * @param rpcRequest
	 * @param returnType
	 * @return
	 * @throws Throwable
	 */
	private Object invoke(BaseRefrence baseRefrence, RpcRequest rpcRequest, Class<?> returnType) throws Throwable {
		/**
		 * 判断服务降级
		 * 无视：广播策略，缓存，本地调用，更无视rpc请求
		 */
		if(ServiceDegradeUtil.degradeServiceList.contains(baseRefrence.getName())) {
			logger.info("[服务降级]:" + baseRefrence.getName() + ",直接返回nul");
			return null;
		}
		
		/**
		 * 监控计算时间，无视拦截器操作，服务降级，无视缓存，无视本地调用
		 * 监控位置：RpcClient.runRpc();
		 */
		
		/**
		 * 广播失败/广播安全
		 * 注意：广播不判缓存，广播不判本地调用
		 */
		if(ClusterFaultStrategy.BROADCAST_FAIL.getName().equals(baseRefrence.getClusterFault()) ||
				ClusterFaultStrategy.BROADCAST_SAFE.getName().equals(baseRefrence.getClusterFault())) {
			broadcastInvoke(baseRefrence, rpcRequest);
			
			/**
			 * 判断基础类型，返回默认值，否则自动转换会报空指针
			 */
			if(TypeParseUtil.isBasicType(returnType)) {
				return TypeParseUtil.getBasicTypeDefaultValue(returnType);
			}
			
			/**
			 * 广播调用不返回响应，如果方法返回基础类型，获取结果，会报空指针
			 */
			return null;
		}
		
		try {
			/**
			 * 判断使用缓存
			 */
			if(baseRefrence.isUseCache() && null != CacheUtil.getCache(rpcRequest)) {
				logger.info("[使用缓存]从缓存中返回结果！");
				return CacheUtil.getCache(rpcRequest).getRpcResponse().getResult();
			}
			
			/**
			 * rpc调用结果
			 */
			RpcResponse rpcResponse = null;
			
			/**
			 * 判断本地调用
			 */
			if(baseRefrence.isSearchLocal()) {
				/**
				 * 本地调用
				 */
				rpcResponse = localInvoke(baseRefrence, rpcRequest);
			} else {
				/**
				 * 每次都创建一个ChannelFuture
				 * 服务端-不同线程并发处理
				 */
				rpcResponse = new RpcClient().runRpc(baseRefrence, rpcRequest);
				logger.info("rpc返回信息：" + rpcResponse);
			}
			
			/**
			 * 调用成功
			 */
			if(rpcResponse.isSuccess()) {
				/**
				 * 判断使用缓存，而且成功调用
				 */
				if(baseRefrence.isUseCache()) {
					logger.info("[成功调用写入缓存]！");
					CacheUtil.setCache(rpcRequest, rpcResponse, baseRefrence.getCacheTime());
				}
				/**
				 * 返回结果
				 */
				return rpcResponse.getResult();
			} else {
				/**
				 * 调用异常
				 */
				throw rpcResponse.getThrowable();
			}
		} catch (Throwable throwable) {
			/**
			 * 返回空响应导致空指针 或 捕获到调用异常 : 判断集群容错
			 */
			if(ClusterFaultStrategy.FAIL_FAST.getName().equals(baseRefrence.getClusterFault())) {//快速失败
				logger.info("集群容错-快速失败，直接抛出异常！");
				throw throwable;
			} else if(ClusterFaultStrategy.FAIL_RETRY.getName().equals(baseRefrence.getClusterFault())) {//失败重试
				/**
				 * 判断本地调用
				 */
				if(baseRefrence.isSearchLocal()) {
					logger.info("集群容错-失败重试，本地调用不重试，类似快速失败！");
					throw throwable;
				}
				logger.info("集群容错-失败重试，只重试1次！");
				/**
				 * 每次都创建一个ChannelFuture
				 * 服务端-不同线程并发处理
				 */
				RpcResponse rpcResponse = new RpcClient().runRpc(baseRefrence, rpcRequest);
				logger.info("rpc返回信息：" + rpcResponse);
				
				/**
				 * 调用成功
				 */
				if(rpcResponse.isSuccess()) {
					/**
					 * 判断使用缓存，而且成功调用
					 */
					if(baseRefrence.isUseCache()) {
						logger.info("[成功调用写入缓存]！");
						CacheUtil.setCache(rpcRequest, rpcResponse, baseRefrence.getCacheTime());
					}
					/**
					 * 返回结果
					 */
					return rpcResponse.getResult();
				} else {
					/**
					 * 调用异常
					 */
					throw rpcResponse.getThrowable();
				}
			} else if(ClusterFaultStrategy.FAIL_SAFE.getName().equals(baseRefrence.getClusterFault())) {//失败安全
				logger.info("集群容错-失败安全，不抛异常，返回null！");
				/**
				 * 判断基础类型，返回默认值，否则自动转换会报空指针
				 */
				if(TypeParseUtil.isBasicType(returnType)) {
					return TypeParseUtil.getBasicTypeDefaultValue(returnType);
				}
				/**
				 * 如果方法返回基础类型，获取结果，会报空指针
				 */
				return null;
			}
			logger.info("不支持的集群容错策略[" + baseRefrence.getClusterFault() + "]，直接抛出异常！");
			throw throwable;
		}
	}

	/**
	 * 广播失败/广播安全 调用
	 * @param baseRefrence
	 * @param rpcRequest
	 * @return
	 */
	private void broadcastInvoke(BaseRefrence baseRefrence, RpcRequest rpcRequest) {
		logger.info("广播调用开始，策略：[" + baseRefrence.getClusterFault() + "]");
		/**
		 * 获取引用服务
		 */
		List<BaseService> serviceList = RegisterUtils.getServiceList(baseRefrence.getName(), baseRefrence.getVersion());
		logger.info("服务方信息:" + serviceList);
		
		/**
		 * 判无服务
		 */
		if(null == serviceList || serviceList.size() == 0) {
			throw new RuntimeException("无可用服务！");
		}
		
		boolean hasThrowable = false;//是否有异常
		
		for(BaseService baseService : serviceList) {
			try {
				/**
				 * 每次都创建一个ChannelFuture
				 * 服务端-不同线程并发处理
				 */
				RpcResponse rpcResponse = new RpcClient().runRpc(baseRefrence, baseService, rpcRequest);
				logger.info("rpc返回信息：" + rpcResponse);
				
				/**
				 * 调用成功
				 */
				if(rpcResponse.isSuccess()) {
					logger.info("广播调用[" + baseService.getIp() + ":" + baseService.getPort() + "]正常结束！");
				} else {
					/**
					 * 调用异常
					 */
					throw rpcResponse.getThrowable();
				}
			} catch (Throwable throwable) {
				logger.error("广播调用[" + baseService.getIp() + ":" + baseService.getPort() + "]异常发生！", throwable);
				hasThrowable = true;
			}
		}
		
		/**
		 * 判断有异常
		 */
		if(hasThrowable) {
			if(ClusterFaultStrategy.BROADCAST_FAIL.getName().equals(baseRefrence.getClusterFault())) {//快速失败
				logger.info("集群容错-广播失败，全部服务调用一遍，不获取单独某个结果，返回null，只要有一个异常，就抛出！");
				throw new RuntimeException("[集群容错-广播失败]，有异常发生！");
			} else if(ClusterFaultStrategy.BROADCAST_SAFE.getName().equals(baseRefrence.getClusterFault())) {//快速失败
				logger.info("集群容错-广播安全，全部服务调用一遍，不获取单独某个结果，返回null，不抛异常！");
			}
		}

		logger.info("广播调用结束，策略：[" + baseRefrence.getClusterFault() + "]");
	}

	/**
	 * 本地调用
	 * @param baseRefrence
	 * @param rpcRequest
	 * @return
	 * @throws Throwable
	 */
	private RpcResponse localInvoke(BaseRefrence baseRefrence, RpcRequest rpcRequest) throws Throwable {
		Object serviceImpl;//服务实现类

		if (!ServiceUtil.serviceImplMap.containsKey(rpcRequest.getClassName())) {
			
			/**
			 * spring管理的所有服务
			 */
			Map<String, BaseService> serviceMap = (Map<String, BaseService>)SpringUtil.
					getApplicationContext().getBeansOfType(BaseService.class);
			logger.info("[本地调用]spring管理的所有服务:" + serviceMap);
			
			BaseService baseService = null;//服务
			
			/**
			 * 本地调用，只读一个服务
			 */
			for(String key : serviceMap.keySet()) {
				if(serviceMap.get(key).getName().equals(rpcRequest.getClassName())) {
					baseService = serviceMap.get(key);
					logger.info("[本地调用]根据[" + rpcRequest.getClassName() + "]查找到服务信息:" + baseService);
				}
			}
			
			/**
			 * 没有服务 或者 有服务 但是 版本非空，而且 版本匹配不一致
			 */
			if(null == baseService || (null != baseRefrence.getVersion() && !"".equals(baseRefrence.getVersion()) && 
					!baseRefrence.getVersion().equals(baseService.getVersion()))) {
				throw new RuntimeException("[本地调用]无可用服务");
			}
			
			serviceImpl = Class.forName(baseService.getImpl()).newInstance();
			logger.info("[本地调用]服务端serviceImpl:" + serviceImpl);
			ServiceUtil.serviceImplMap.put(rpcRequest.getClassName(), serviceImpl);
		} else {
			serviceImpl = ServiceUtil.serviceImplMap.get(rpcRequest.getClassName());
			logger.info("[本地调用]服务端serviceImpl:" + serviceImpl);
		}
		
		/**
		 * 设置上下文-客户端信息 [本地调用不设置]
		 */
		//RpcContext.clientApplicationName.set(rpcRequest.getClientApplicationName());
		//RpcContext.clientIp.set(rpcRequest.getClientIp());
		
		/**
		 * 获取客户端送的隐式参数 [本地调用不[重复]设置]
		 */
		//RpcContext.shadowParameterMap.set(rpcRequest.getShadowParameterMap());

		RpcResponse rpcResponse = new RpcResponse();
		
		try {
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
			logger.info("[本地调用]result:" + result);
			rpcResponse.setSuccess(true);
			rpcResponse.setResult(result);
		} catch (Throwable throwable) {
			logger.error("[本地调用]异常发生！", throwable);
			rpcResponse.setSuccess(false);
			rpcResponse.setThrowable(throwable);
		}
		return rpcResponse;
	}
}
