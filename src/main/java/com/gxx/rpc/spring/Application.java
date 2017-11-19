package com.gxx.rpc.spring;

import java.net.InetAddress;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.gxx.rpc.core.RpcContext;
import com.gxx.rpc.register.RegisterUtils;

/** 
 * 应用
 * @author Gxx
 */
public class Application implements InitializingBean, ApplicationContextAware {

	private String id;

	private String name;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "Application [id=" + id + ", name=" + name + "]";
	}
    
	/**
	 * spring上下文
	 */
	private ApplicationContext applicationContext;

	/**
	 * 设置spring上下文对象
	 * 注意：ApplicationContextAware.setApplicationContext 在 InitializingBean.afterPropertiesSet 前执行！
	 */
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
		SpringUtil.setApplicationContext(applicationContext);
	}

	/**
	 * 在spring实例化所有bean之后，执行该方法
	 * 前提：bean实现InitializingBean
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		Application application = (Application)applicationContext.getBean("application");
		/**
		 * 设置上下文-本地信息
		 */
		RpcContext.localApplicationName = application.getName();//本地应用名称
		RpcContext.localIp = InetAddress.getLocalHost().getHostAddress();//本地ip
		/**
		 * 优雅停机-下掉注册到注册中心上的所有服务
		 */
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			
			@Override
			public void run() {
				/**
				 * 配置了server服务端(端口)，才能注册到注册中心
				 * 注意：本地调用配置service，则无须下掉注册
				 */
				if(SpringUtil.getApplicationContext().containsBean("server")) {
					/**
					 * spring管理的所有服务
					 */
					Map<String, BaseService> serviceMap = (Map<String, BaseService>)SpringUtil.
							getApplicationContext().getBeansOfType(BaseService.class);
					
					for(String key : serviceMap.keySet()) {
						BaseService baseService = serviceMap.get(key);
						/**
						 * 服务从注册中心下掉
						 */
						RegisterUtils.unregistService(baseService);
					}
				}
				/**
				 * 配置了client客户端(端口)，才能发布到注册中心
				 */
				if(SpringUtil.getApplicationContext().containsBean("client")) {
					/**
					 * spring管理的所有服务
					 */
					Map<String, BaseRefrence> refrenceMap = (Map<String, BaseRefrence>)SpringUtil.
							getApplicationContext().getBeansOfType(BaseRefrence.class);
					
					for(String key : refrenceMap.keySet()) {
						BaseRefrence baseRefrence = refrenceMap.get(key);
						/**
						 * 引用从注册中心下掉
						 */
						RegisterUtils.unregistRefrence(baseRefrence);
					}
				}
			}
		}));
	}
}