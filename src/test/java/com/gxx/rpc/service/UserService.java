package com.gxx.rpc.service;

import java.math.BigDecimal;

/**
 * 用户服务
 * @author Gxx
 */
public interface UserService {

	/**
	 * 加法-正常返回结果
	 * @param a
	 * @param b
	 * @return
	 */
	BigDecimal normalPlus(BigDecimal a, BigDecimal b);

	/**
	 * 减法-无返回结果
	 * @param a
	 * @param b
	 * @return
	 */
	void noReturnMinus(BigDecimal a, BigDecimal b);

	/**
	 * 除法-抛出异常
	 * @param a
	 * @return
	 */
	BigDecimal exceptionDevideZero(BigDecimal a);

	/**
	 * 处理超时
	 * @return
	 */
	public void processTimeout();

	/**
	 * 泛化调用 空请求 空响应
	 * @return
	 */
	public void gereralEmptyReqEmptyRes();

	/**
	 * 泛化调用 空请求 简单响应
	 * @return
	 */
	public String gereralEmptyReqSimpleRes();

	/**
	 * 泛化调用 空请求 复杂响应
	 * @return
	 */
	public UserDto gereralEmptyReqComplexRes();

	/**
	 * 泛化调用 一个简单请求 简单响应
	 * @return
	 */
	public String gereralOneSimpleReqSimpleRes(String name);

	/**
	 * 泛化调用 两个简单请求 简单响应
	 * @return
	 */
	public String gereralTwoSimpleReqSimpleRes(String name, int age);

	/**
	 * 泛化调用 一个复杂请求 复杂响应
	 * @return
	 */
	public UserDto gereralOneComplexReqComplexRes(UserDto userDto);
}
