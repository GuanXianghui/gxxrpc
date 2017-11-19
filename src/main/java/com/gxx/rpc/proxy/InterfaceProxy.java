package com.gxx.rpc.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 接口动态代理
 * @author Gxx
 */
public class InterfaceProxy implements InvocationHandler {

	/**
	 * 动态代理接口类
	 */
	Class<?> interfaceProxyClass;
	
	/**
	 * 创建代理类
	 * 
	 * @param classLoader
	 * @param classes
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> T newProxy(Class<T> object) {
		/**
		 * 动态代理接口类
		 */
		this.interfaceProxyClass = object;
		/**
		 * 返回代理类
		 */
		return (T) Proxy.newProxyInstance(object.getClassLoader(), new Class[] { object }, this);
	}

	/**
	 * 代理调用方法
	 */
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		/**
		 * 参数类型集合 转成 字符串集合
		 */
		String[] types = new String[method.getParameterTypes().length];
		for(int i=0;i<method.getParameterTypes().length;i++) {
			types[i] = method.getParameterTypes()[i].getName();
		}
		
		/**
		 * 返回类型
		 */
		Class<?> returnType = method.getReturnType();
		
		return new InterfaceInvoke().invoke(interfaceProxyClass.getName(), method.getName(), types, args, false, returnType);
	}
}
