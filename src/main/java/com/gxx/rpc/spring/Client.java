package com.gxx.rpc.spring;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;

import com.gxx.rpc.utils.ServiceDegradeUtil;

/** 
 * 客户端
 * @author Gxx
 */
public class Client implements InitializingBean {

	/**
	 * 日志记录器
	 */
	Logger logger = Logger.getLogger(Client.class);

	private String id;

	private String loadBalance;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLoadBalance() {
		return loadBalance;
	}

	public void setLoadBalance(String loadBalance) {
		this.loadBalance = loadBalance;
	}

	@Override
	public String toString() {
		return "Server [id=" + id + ", loadBalance=" + loadBalance + "]";
	}
    
	/**
	 * 在spring实例化所有bean之后，执行该方法
	 * 前提：bean实现InitializingBean
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		/**
		 * 同步最新降级服务
		 */
		ServiceDegradeUtil.synDegradeService();
		/**
		 * 订阅降级服务变化
		 */
		ServiceDegradeUtil.subscribeDegradeServiceChanges();;
	}
}
