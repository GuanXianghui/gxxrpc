package com.gxx.rpc.service;

import java.math.BigDecimal;

import org.apache.log4j.Logger;

import com.gxx.rpc.core.RpcContext;

/**
 * 用户服务实现类
 * @author Gxx
 */
public class UserServiceImpl implements UserService {

	/**
	 * 日志记录器
	 */
	Logger logger = Logger.getLogger(UserServiceImpl.class);
	
	/**
	 * 加法-正常返回结果
	 * @param a
	 * @param b
	 * @return
	 */
	public BigDecimal normalPlus(BigDecimal a, BigDecimal b) {
		logger.info("加法-正常返回结果,a=[" + a + "],b=[" + b + "]");

		logger.info("客户端名称：" + RpcContext.clientApplicationName.get());
		logger.info("客户端ip：" + RpcContext.clientIp.get());
		logger.info("本地名称：" + RpcContext.localApplicationName);
		logger.info("本地ip：" + RpcContext.localIp);
		logger.info("服务端名称：" + RpcContext.serverApplicationName.get());
		logger.info("服务端ip：" + RpcContext.serverIp.get());
		logger.info("服务端端口：" + RpcContext.serverPort.get());
		
		logger.info("获取客户端送的隐式参数:" + RpcContext.shadowParameterMap.get());
		return a.add(b);
	}

	/**
	 * 减法-无返回结果
	 * @param a
	 * @param b
	 * @return
	 */
	public void noReturnMinus(BigDecimal a, BigDecimal b) {
		logger.info("减法-无返回结果,a=[" + a + "],b=[" + b + "]");
		a.subtract(b);
	}

	/**
	 * 除法-抛出异常
	 * @param a
	 * @return
	 */
	public BigDecimal exceptionDevideZero(BigDecimal a) {
		logger.info("除法-抛出异常,a=[" + a + "]");
		return a.divide(BigDecimal.ZERO);
	}

	/**
	 * 处理超时
	 * @return
	 */
	public void processTimeout() {
		logger.info("处理超时");
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			logger.error("处理超时异常！", e);
		}
	}

	/**
	 * 泛化调用 空请求 空响应
	 * @return
	 */
	public void gereralEmptyReqEmptyRes() {
		logger.info("泛化调用 空参数 空返回");
	}

	/**
	 * 泛化调用 空请求 简单响应
	 * @return
	 */
	public String gereralEmptyReqSimpleRes() {
		logger.info("泛化调用 空参数 空返回");
		return "hello from server!";
	}

	/**
	 * 泛化调用 空请求 复杂响应
	 * @return
	 */
	public UserDto gereralEmptyReqComplexRes() {
		logger.info("泛化调用 空请求 复杂响应");
		UserDto userDto = new UserDto();
		userDto.setUserName("关向辉");
		userDto.setAge(28);
		return userDto;
	}

	/**
	 * 泛化调用 一个简单请求 简单响应
	 * @return
	 */
	public String gereralOneSimpleReqSimpleRes(String name) {
		logger.info("泛化调用 一个简单请求 简单响应");
		return "hello [" + name + "] from server!";
	}

	/**
	 * 泛化调用 两个简单请求 简单响应
	 * @return
	 */
	public String gereralTwoSimpleReqSimpleRes(String name, int age) {
		logger.info("泛化调用 两个简单请求 简单响应");
		return "hello [" + name + ":" + age + "] from server!";
	}

	/**
	 * 泛化调用 一个复杂请求 复杂响应
	 * @return
	 */
	public UserDto gereralOneComplexReqComplexRes(UserDto userDto) {
		logger.info("泛化调用 一个复杂请求 复杂响应");
		userDto.setUserName("new Name");
		userDto.setAge(userDto.getAge() + 10);
		return userDto;
	}
}