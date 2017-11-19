package com.gxx.rpc.spring;

import java.net.InetAddress;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.gxx.rpc.register.RegisterUtils;

/** 
 * 服务
 * @author Gxx
 */
public class Service extends BaseService implements InitializingBean, ApplicationContextAware {

	private static final long serialVersionUID = 1L;

	/**
	 * 日志记录器
	 */
	Logger logger = Logger.getLogger(Service.class);
    
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
		 * 配置了server服务端(端口)，才注册到注册中心
		 * 注意：本地调用配置service，则无须注册
		 */
		if(applicationContext.containsBean("server")) {
			Application application = (Application)applicationContext.getBean("application");
			Server server = (Server)applicationContext.getBean("server");
			super.setApplicationName(application.getName());
			super.setIp(InetAddress.getLocalHost().getHostAddress());
			super.setPort(server.getPort());
			/**
			 * 发布服务到注册中心
			 */
			RegisterUtils.registService(this.toBaseService());
		}
	}
	
	/**
	 * 用户序列化传输
	 * 目的：过滤掉ApplicationContext属性，不然序列化报错
	 * @return
	 */
	public BaseService toBaseService() {
		BaseService baseService = new BaseService();
		baseService.setId(this.getId());
		baseService.setImpl(this.getImpl());
		baseService.setApplicationName(this.getApplicationName());
		baseService.setIp(this.getIp());
		baseService.setName(this.getName());
		baseService.setPort(this.getPort());
		baseService.setRef(this.getRef());
		baseService.setWeight(this.getWeight());
		baseService.setVersion(this.getVersion());
		return baseService;
	}
}
