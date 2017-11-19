package com.gxx.rpc;

import java.math.BigDecimal;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.gxx.rpc.core.GeneralService;
import com.gxx.rpc.core.RpcContext;
import com.gxx.rpc.service.UserDto;
import com.gxx.rpc.service.UserService;

import junit.framework.TestCase;

/** 
 * gxxrpc测试单元
 * @author Gxx
 */
public class GxxRpcTest extends TestCase {

	/**
	 * 日志记录器
	 */
	Logger logger = Logger.getLogger(GxxRpcTest.class);

	/**
	 * 测试服务端-base版本
	 * @throws Exception
	 */
	@SuppressWarnings("resource")
	@Test
	public void testServer_base() throws Exception {
		logger.info("测试服务端-base版本");
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("gxxrpc-provider-base.xml");
		context.start();
		System.in.read();
	}

	/**
	 * 测试客户端-base版本
	 * @throws Exception
	 */
	@SuppressWarnings("resource")
	@Test
	public void testClient_base() throws Exception {
		logger.info("测试客户端-base版本");
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("gxxrpc-consumer-base.xml");
		context.start();
		/**
		 * 服务调用
		 */
		UserService userService = (UserService)context.getBean("userService");
		BigDecimal a = new BigDecimal("123");
		BigDecimal b = new BigDecimal("321");
		logger.info("a+b:" + userService.normalPlus(a, b));
		System.in.read();
	}

	/**
	 * 测试服务端-非base版本-version属性
	 * @throws Exception
	 */
	@SuppressWarnings("resource")
	@Test
	public void testServer_version() throws Exception {
		logger.info("测试服务端-非base版本-version属性");
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("gxxrpc-provider-version.xml");
		context.start();
		System.in.read();
	}

	/**
	 * 测试客户端-非base版本-version属性
	 * @throws Exception
	 */
	@SuppressWarnings("resource")
	@Test
	public void testClient_version() throws Exception {
		logger.info("测试客户端-非base版本-version属性");
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("gxxrpc-consumer-version.xml");
		context.start();
		/**
		 * 服务调用
		 */
		UserService userService = (UserService)context.getBean("userService");
		BigDecimal a = new BigDecimal("123");
		BigDecimal b = new BigDecimal("321");
		logger.info("a+b:" + userService.normalPlus(a, b));
		System.in.read();
	}

	/**
	 * 测试客户端-非base版本-timeout属性
	 * @throws Exception
	 */
	@SuppressWarnings("resource")
	@Test
	public void testClient_timeout() throws Exception {
		logger.info("测试客户端-非base版本-timeout属性");
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("gxxrpc-consumer-timeout.xml");
		context.start();
		/**
		 * 服务调用
		 */
		UserService userService = (UserService)context.getBean("userService");
		userService.processTimeout();
		System.in.read();
	}

	/**
	 * 测试服务端-非base版本-weight属性
	 * 注意：可以启动两个provider，配置不同权重，多调几次查看结果
	 * @throws Exception
	 */
	@SuppressWarnings("resource")
	@Test
	public void testServer_weight() throws Exception {
		logger.info("测试服务端-非base版本-weight属性");
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("gxxrpc-provider-weight.xml");
		context.start();
		System.in.read();
	}

	/**
	 * 测试客户端-非base版本-direct相关属性
	 * 注意：直接指向服务提供方，可以不配置注册中心
	 * @throws Exception
	 */
	@SuppressWarnings("resource")
	@Test
	public void testClient_direct() throws Exception {
		logger.info("测试客户端-非base版本-direct相关属性");
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("gxxrpc-consumer-direct.xml");
		context.start();
		/**
		 * 服务调用
		 */
		UserService userService = (UserService)context.getBean("userService");
		BigDecimal a = new BigDecimal("123");
		BigDecimal b = new BigDecimal("321");
		logger.info("a+b:" + userService.normalPlus(a, b));
		System.in.read();
	}

	/**
	 * 测试客户端-非base版本-cluster-fault属性
	 * @throws Exception
	 */
	@SuppressWarnings("resource")
	@Test
	public void testClient_cluster_fault() throws Exception {
		logger.info("测试客户端-非base版本-cluster-fault属性");
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("gxxrpc-consumer-cluster-fault.xml");
		context.start();
		/**
		 * 服务调用
		 */
		UserService userService = (UserService)context.getBean("userService");
		BigDecimal a = new BigDecimal("123");
		logger.info("a/0:" + userService.exceptionDevideZero(a));
		System.in.read();
	}

	/**
	 * 测试客户端-非base版本-cache相关属性
	 * @throws Exception
	 */
	@SuppressWarnings("resource")
	@Test
	public void testClient_cache() throws Exception {
		logger.info("测试客户端-非base版本-cache相关属性");
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("gxxrpc-consumer-cache.xml");
		context.start();
		/**
		 * 服务调用
		 */
		UserService userService = (UserService)context.getBean("userService");
		BigDecimal a = new BigDecimal("123");
		BigDecimal b = new BigDecimal("321");
		logger.info("a+b:" + userService.normalPlus(a, b));
		logger.info("a+b:" + userService.normalPlus(a, b));
		System.in.read();
	}

	/**
	 * 测试客户端-非base版本-async属性
	 * @throws Exception
	 */
	@SuppressWarnings("resource")
	@Test
	public void testClient_async() throws Exception {
		logger.info("测试客户端-非base版本-async属性");
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("gxxrpc-consumer-async.xml");
		context.start();
		/**
		 * 服务调用
		 */
		UserService userService = (UserService)context.getBean("userService");
		BigDecimal a = new BigDecimal("123");
		BigDecimal b = new BigDecimal("321");
		logger.info("a+b，同步结果:" + userService.normalPlus(a, b));
		logger.info("a+b，异步结果:" + RpcContext.futureTask.get().get());
		System.in.read();
	}

	/**
	 * 测试客户端-非base版本-search-local属性
	 * 注意：本地调用，可以不配置注册中心
	 * @throws Exception
	 */
	@SuppressWarnings("resource")
	@Test
	public void testClient_search_local() throws Exception {
		logger.info("测试客户端-非base版本-search-local属性");
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("gxxrpc-consumer-search-local.xml");
		context.start();
		/**
		 * 服务调用
		 */
		UserService userService = (UserService)context.getBean("userService");
		BigDecimal a = new BigDecimal("123");
		BigDecimal b = new BigDecimal("321");
		logger.info("a+b:" + userService.normalPlus(a, b));
		System.in.read();
	}

	/**
	 * 测试客户端-非base版本-intercept相关属性
	 * @throws Exception
	 */
	@SuppressWarnings("resource")
	@Test
	public void testClient_intercept() throws Exception {
		logger.info("测试客户端-非base版本-intercept相关属性");
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("gxxrpc-consumer-intercept.xml");
		context.start();
		/**
		 * 服务调用
		 */
		UserService userService = (UserService)context.getBean("userService");
		BigDecimal a = new BigDecimal("123");
		BigDecimal b = new BigDecimal("321");
		logger.info("a+b:" + userService.normalPlus(a, b));
		System.in.read();
	}

	/**
	 * 测试客户端-非base版本-load-balance属性
	 * 注意：可以启动两个provider，多调几次查看结果
	 * @throws Exception
	 */
	@SuppressWarnings("resource")
	@Test
	public void testClient_load_balance() throws Exception {
		logger.info("测试客户端-非base版本-load-balance属性");
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("gxxrpc-consumer-load-balance.xml");
		context.start();
		/**
		 * 服务调用
		 */
		UserService userService = (UserService)context.getBean("userService");
		BigDecimal a = new BigDecimal("123");
		BigDecimal b = new BigDecimal("321");
		logger.info("a+b:" + userService.normalPlus(a, b));
		logger.info("a+b:" + userService.normalPlus(a, b));
		logger.info("a+b:" + userService.normalPlus(a, b));
		System.in.read();
	}

	/**
	 * 测试服务端-非base版本-telnet-manage标签
	 * @throws Exception
	 */
	@SuppressWarnings("resource")
	@Test
	public void testServer_telnet_manage() throws Exception {
		logger.info("测试服务端-非base版本-telnet-manage标签");
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("gxxrpc-provider-telnet-manage.xml");
		context.start();
		System.in.read();
	}

	/**
	 * 测试客户端-非base版本-telnet-manage标签
	 * 注意：客户端/服务端，都可以开启telnet治理
	 * @throws Exception
	 */
	@SuppressWarnings("resource")
	@Test
	public void testClient_telnet_manage() throws Exception {
		logger.info("测试客户端-非base版本-telnet-manage标签");
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("gxxrpc-consumer-telnet-manage.xml");
		context.start();
		System.in.read();
	}

	/**
	 * 测试客户端-非base版本-monitor标签
	 * 注意：需要启动gxxrpc治理中心，接受监控数据
	 * @throws Exception
	 */
	@SuppressWarnings("resource")
	@Test
	public void testClient_monitor() throws Exception {
		logger.info("测试客户端-非base版本-monitor标签");
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("gxxrpc-consumer-monitor.xml");
		context.start();
		/**
		 * 服务调用
		 */
		UserService userService = (UserService)context.getBean("userService");
		BigDecimal a = new BigDecimal("123");
		BigDecimal b = new BigDecimal("321");
		try {
			logger.info("a+b:" + userService.normalPlus(a, b));
		} catch (Throwable throwable) {
			/**
			 * 捕获异常，否则，test方法异常退出，异步监控就不会发送
			 */
			logger.error("调用异常", throwable);
		}
		
		System.in.read();
	}

	/**
	 * 测试客户端-base版本-上下文
	 * @throws Exception
	 */
	@SuppressWarnings("resource")
	@Test
	public void testClient_base_rpc_context() throws Exception {
		logger.info("测试客户端-base版本-上下文");
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("gxxrpc-consumer-base.xml");
		context.start();
		
		logger.info("客户端名称：" + RpcContext.clientApplicationName.get());
		logger.info("客户端ip：" + RpcContext.clientIp.get());
		logger.info("本地名称：" + RpcContext.localApplicationName);
		logger.info("本地ip：" + RpcContext.localIp);
		logger.info("服务端名称：" + RpcContext.serverApplicationName.get());
		logger.info("服务端ip：" + RpcContext.serverIp.get());
		logger.info("服务端端口：" + RpcContext.serverPort.get());
		
		/**
		 * 设置隐式参数(线程独享)，rpc调用一次后，马上清空；下次调用参数已经不存在，需要重新设置
		 */
		RpcContext.shadowParameterMap.get().put("name", "gxx");
		RpcContext.shadowParameterMap.get().put("hello", true);
		
		/**
		 * 服务调用
		 */
		UserService userService = (UserService)context.getBean("userService");
		BigDecimal a = new BigDecimal("123");
		BigDecimal b = new BigDecimal("321");
		logger.info("a+b:" + userService.normalPlus(a, b));
		
		logger.info("客户端名称：" + RpcContext.clientApplicationName.get());
		logger.info("客户端ip：" + RpcContext.clientIp.get());
		logger.info("本地名称：" + RpcContext.localApplicationName);
		logger.info("本地ip：" + RpcContext.localIp);
		logger.info("服务端名称：" + RpcContext.serverApplicationName.get());
		logger.info("服务端ip：" + RpcContext.serverIp.get());
		logger.info("服务端端口：" + RpcContext.serverPort.get());

		logger.info("隐式参数：" + RpcContext.shadowParameterMap.get());
		
		System.in.read();
	}

	/**
	 * 测试服务端+客户端-base版本
	 * @throws Exception
	 */
	@SuppressWarnings("resource")
	@Test
	public void testServerAndClient_base() throws Exception {
		logger.info("测试服务端+客户端-base版本");
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("gxxrpc-provider-and-consumer.xml");
		context.start();
		/**
		 * 延迟一秒，防止没有监听到服务变化，就开始调用，报服务不存在
		 */
		Thread.sleep(1000);
		/**
		 * 服务调用
		 */
		UserService userService = (UserService)context.getBean("userService");
		BigDecimal a = new BigDecimal("123");
		BigDecimal b = new BigDecimal("321");
		logger.info("a+b:" + userService.normalPlus(a, b));
		System.in.read();
	}
	
	/**
	 * 测试服务端-full版本
	 * @throws Exception
	 */
	@SuppressWarnings("resource")
	@Test
	public void testServer_full() throws Exception {
		logger.info("测试服务端-full版本");
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("gxxrpc-provider-full.xml");
		context.start();
		System.in.read();
	}

	/**
	 * 测试客户端-full版本
	 * @throws Exception
	 */
	@SuppressWarnings("resource")
	@Test
	public void testClient_full() throws Exception {
		logger.info("测试客户端-full版本");
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("gxxrpc-consumer-full.xml");
		context.start();
		/**
		 * 服务调用
		 */
		UserService userService = (UserService)context.getBean("userService");
		BigDecimal a = new BigDecimal("123");
		BigDecimal b = new BigDecimal("321");
		logger.info("a+b:" + userService.normalPlus(a, b));
		System.in.read();
	}

	/**
	 * 测试客户端-base版本-泛化调用
	 * @throws Exception
	 */
	@SuppressWarnings("resource")
	@Test
	public void testClient_general() throws Throwable {
		logger.info("测试客户端-base版本-泛化调用");
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("gxxrpc-consumer-base.xml");
		context.start();
		
		/**
		 * 泛化调用 空请求 空响应
		 */
		String className = "com.gxx.rpc.service.UserService";
		String methodName = "gereralEmptyReqEmptyRes";
		String[] types = {};
		Object[] args = {};
		
		GeneralService generalService = new GeneralService();
		Object result = generalService.invoke(className, methodName, types, args);
		logger.info("[泛化调用 空请求 空响应]:" + result);
		
		/**
		 * 泛化调用 空请求 简单响应
		 */
		className = "com.gxx.rpc.service.UserService";
		methodName = "gereralEmptyReqSimpleRes";
		types = new String[]{};
		args = new Object[]{};
		
		result = generalService.invoke(className, methodName, types, args);
		logger.info("[泛化调用 空请求 简单响应]:" + result);
		
		/**
		 * 泛化调用 空请求 复杂响应
		 */
		className = "com.gxx.rpc.service.UserService";
		methodName = "gereralEmptyReqComplexRes";
		types = new String[]{};
		args = new Object[]{};
		
		result = generalService.invoke(className, methodName, types, args);
		logger.info("[泛化调用 空请求 复杂响应]:" + result);
		
		/**
		 * 泛化调用 一个简单请求 简单响应
		 */
		className = "com.gxx.rpc.service.UserService";
		methodName = "gereralOneSimpleReqSimpleRes";
		types = new String[]{"java.lang.String"};
		args = new Object[]{"gxx"};
		
		result = generalService.invoke(className, methodName, types, args);
		logger.info("[泛化调用 一个简单请求 简单响应]:" + result);
		
		/**
		 * 泛化调用 两个简单请求 简单响应
		 */
		className = "com.gxx.rpc.service.UserService";
		methodName = "gereralTwoSimpleReqSimpleRes";
		types = new String[]{"java.lang.String", "int"};
		args = new Object[]{"gxx", 28};
		
		result = generalService.invoke(className, methodName, types, args);
		logger.info("[泛化调用 两个简单请求 简单响应]:" + result);
		
		/**
		 * 泛化调用 两个简单请求 简单响应 - 非String
		 */
		BigDecimal a = new BigDecimal("991");
		BigDecimal b = new BigDecimal("779");
		
		className = "com.gxx.rpc.service.UserService";
		methodName = "normalPlus";
		types = new String[]{a.getClass().getName(), b.getClass().getName()};
		args = new Object[]{a, b};
		
		result = generalService.invoke(className, methodName, types, args);
		logger.info("[泛化调用 两个简单请求 简单响应 - 非String]:" + result);
		
		/**
		 * 泛化调用 一个复杂请求 复杂响应
		 */
		UserDto userDto = new UserDto();
		userDto.setUserName("gxx");
		userDto.setAge(18);
		
		className = "com.gxx.rpc.service.UserService";
		methodName = "gereralOneComplexReqComplexRes";
		types = new String[]{userDto.getClass().getName()};
		args = new Object[]{userDto};
		
		result = generalService.invoke(className, methodName, types, args);
		logger.info("[泛化调用 一个复杂请求 复杂响应]:" + result);
		
		/**
		 * 泛化调用 类api不存在的复杂参数类型请求(不能直接赋值属性) 暂不支持 后续版本考虑
		 */
		
		System.in.read();
	}
}