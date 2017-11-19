package com.gxx.rpc.spring;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/** 
 * 支持spring的schema命名空间解析
 * 注意：继承spring抽象类
 * @author Gxx
 */
public class SchemaNamespaceHandler extends NamespaceHandlerSupport {

	/**
	 * 日志记录器
	 */
	Logger logger = Logger.getLogger(SchemaNamespaceHandler.class);
	
	@Override
	public void init() {
		logger.info("spring触发解析rpcxml开始！");
		/**
		 * 这里的elementName，无视schema
		 */
		registerBeanDefinitionParser("application", new RpcBeanDefinitionParser(Application.class));
		registerBeanDefinitionParser("server", new RpcBeanDefinitionParser(Server.class));
		registerBeanDefinitionParser("telnet_manage", new RpcBeanDefinitionParser(TelnetManage.class));
		registerBeanDefinitionParser("client", new RpcBeanDefinitionParser(Client.class));
		registerBeanDefinitionParser("monitor", new RpcBeanDefinitionParser(Monitor.class));
		registerBeanDefinitionParser("register", new RpcBeanDefinitionParser(Register.class));
		registerBeanDefinitionParser("service", new RpcBeanDefinitionParser(Service.class));
		registerBeanDefinitionParser("refrence", new RpcBeanDefinitionParser(Refrence.class));
		logger.info("spring触发解析rpcxml结束！");
	}

}
