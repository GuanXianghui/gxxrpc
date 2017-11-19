package com.gxx.rpc.spring;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;

import com.gxx.rpc.telnet.TelnetManageServer;

/** 
 * telnet治理服务
 * @author Gxx
 */
public class TelnetManage implements InitializingBean {

	/**
	 * 日志记录器
	 */
	Logger logger = Logger.getLogger(TelnetManage.class);

	private String id;

	private int port;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	@Override
	public String toString() {
		return "Server [id=" + id + ", port=" + port + "]";
	}

	/**
	 * 在spring实例化所有bean之后，执行该方法
	 * 前提：bean实现InitializingBean
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		/**
		 * 启动telnet治理服务端口监听
		 */
		new TelnetManageServer(this.port).start();
	}
}
