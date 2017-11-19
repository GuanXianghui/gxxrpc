package com.gxx.rpc.spring;

import java.net.InetAddress;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.gxx.rpc.proxy.InterfaceProxy;
import com.gxx.rpc.register.RegisterUtils;

/** 
 * 引用
 * @author Gxx
 */
public class Refrence<T> extends BaseRefrence implements InitializingBean, ApplicationContextAware, FactoryBean<T> {

	private static final long serialVersionUID = 1L;

	/**
	 * 日志记录器
	 */
	Logger logger = Logger.getLogger(Refrence.class);
    
	/**
	 * spring上下文
	 */
	private ApplicationContext applicationContext;

	/**
	 * 获取spring上下文对象
	 * 前提：bean实现ApplicationContextAware
	 */
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
    
	/**
	 * 在spring实例化所有bean之后，执行该方法
	 * 前提：bean实现InitializingBean
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		/**
		 * 判断是否配置应用
		 */
		if(applicationContext.containsBean("application")) {
			Application application = (Application)applicationContext.getBean("application");
			super.setApplicationName(application.getName());//应用名称
			super.setIp(InetAddress.getLocalHost().getHostAddress());//ip
			
			/**
			 * 发布引用到注册中心
			 */
			RegisterUtils.registRefrence(this.toBaseRefrence());
			/**
			 * 同步最新引用服务
			 */
			RegisterUtils.synRefrenceService(this.getName());
			/**
			 * 订阅服务变化
			 */
			RegisterUtils.subscribeServiceChanges(this.getName());
			
			/**
			 * 判断是否启用telnet治理
			 */
			if(SpringUtil.getApplicationContext().containsBean("telnet_manage")) {
				TelnetManage telnetManage = (TelnetManage)SpringUtil.getApplicationContext().getBean("telnet_manage");
				if(null != telnetManage && 0 < telnetManage.getPort()) {
					super.setTelnetManage(true);//启用telnet治理
					super.setTelnetManagePort(telnetManage.getPort());//telnet治理端口
				}
			}
		}
	}

	/**
	 * spring获取到的对象
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@Override
	public T getObject() throws Exception {
		return (T) new InterfaceProxy().newProxy(getObjectType());
	}

	/**
	 * spring获取到对象的类型
	 * @return
	 */
	@Override
	public Class<?> getObjectType() {
		try {
			return Class.forName(this.getName());
		} catch (ClassNotFoundException e) {
			logger.error("服务类找不到", e);
			return null;
		}
	}

	/**
	 * 是否单例
	 * @return
	 */
	@Override
	public boolean isSingleton() {
		return true;
	}
	
	/**
	 * 用户序列化传输
	 * 目的：过滤掉Logger属性，不然序列化报错
	 * @return
	 */
	public BaseRefrence toBaseRefrence() {
		BaseRefrence baseRefrence = new BaseRefrence();
		baseRefrence.setId(getId());
		baseRefrence.setName(getName());
		baseRefrence.setVersion(getVersion());
		baseRefrence.setTimeout(getTimeout());
		baseRefrence.setDirectServerIp(getDirectServerIp());
		baseRefrence.setDirectServerPort(getDirectServerPort());
		baseRefrence.setClusterFault(getClusterFault());
		baseRefrence.setUseCache(isUseCache());
		baseRefrence.setCacheTime(getCacheTime());
		baseRefrence.setAsync(isAsync());
		baseRefrence.setSearchLocal(isSearchLocal());
		baseRefrence.setIntercept(isIntercept());
		baseRefrence.setInterceptor(getInterceptor());
		baseRefrence.setApplicationName(getApplicationName());
		baseRefrence.setIp(getIp());
		baseRefrence.setTelnetManage(isTelnetManage());
		baseRefrence.setTelnetManagePort(getTelnetManagePort());
		return baseRefrence;
	}
}