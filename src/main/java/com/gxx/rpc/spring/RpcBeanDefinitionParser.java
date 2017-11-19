package com.gxx.rpc.spring;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

/** 
 * spring的bean解析器
 * @author Gxx
 */
public class RpcBeanDefinitionParser extends AbstractSingleBeanDefinitionParser {

	/**
	 * pojo类
	 */
	Class<?> pojoClass;
	
	/**
	 * 构造函数
	 * @param pojoClass
	 */
	public RpcBeanDefinitionParser(Class<?> pojoClass) {
		this.pojoClass = pojoClass;
	}

	/**
	 * 分派pojo对象
	 */
	@Override
	protected Class<?> getBeanClass(Element element) {
		return pojoClass;
	}

	/**
	 * 解析对象
	 */
	@Override
	protected void doParse(Element element, BeanDefinitionBuilder bean) {
		/**
		 * 解析应用
		 */
		if(Application.class.equals(pojoClass)) {
			String id = element.getAttribute("id");
			String name = element.getAttribute("name");
	        if(StringUtils.hasText(id)) {
	        	bean.addPropertyValue("id", id);
	        }
	        if(StringUtils.hasText(name)) {
	        	bean.addPropertyValue("name", name);
	        }
		}
		
		/**
		 * 解析服务端
		 */
		if(Server.class.equals(pojoClass)) {
			String id = element.getAttribute("id");
			String port = element.getAttribute("port");
	        if(StringUtils.hasText(id)) {
	        	bean.addPropertyValue("id", id);
	        }
	        if(StringUtils.hasText(port)) {
	        	bean.addPropertyValue("port", Integer.parseInt(port));
	        }
		}
		
		/**
		 * 解析telnet治理服务端
		 */
		if(TelnetManage.class.equals(pojoClass)) {
			String id = element.getAttribute("id");
			String port = element.getAttribute("port");
	        if(StringUtils.hasText(id)) {
	        	bean.addPropertyValue("id", id);
	        }
	        if(StringUtils.hasText(port)) {
	        	bean.addPropertyValue("port", Integer.parseInt(port));
	        }
		}
		
		/**
		 * 解析客户端
		 */
		if(Client.class.equals(pojoClass)) {
			String id = element.getAttribute("id");
			String loadBalance = element.getAttribute("load_balance");
	        if(StringUtils.hasText(id)) {
	        	bean.addPropertyValue("id", id);
	        }
	        if(StringUtils.hasText(loadBalance)) {
	        	bean.addPropertyValue("loadBalance", loadBalance);
	        }
		}
		
		/**
		 * 监控客户端
		 */
		if(Monitor.class.equals(pojoClass)) {
			String id = element.getAttribute("id");
			String ip = element.getAttribute("ip");
			String port = element.getAttribute("port");
	        if(StringUtils.hasText(id)) {
	        	bean.addPropertyValue("id", id);
	        }
	        if(StringUtils.hasText(ip)) {
	        	bean.addPropertyValue("ip", ip);
	        }
	        if(StringUtils.hasText(port)) {
	        	bean.addPropertyValue("port", Integer.parseInt(port));
	        }
		}
		
		/**
		 * 解析注册中心
		 */
		if(Register.class.equals(pojoClass)) {
			String id = element.getAttribute("id");
			String type = element.getAttribute("type");
			String ip = element.getAttribute("ip");
			String port = element.getAttribute("port");
			String username = element.getAttribute("username");
			String password = element.getAttribute("password");
	        if(StringUtils.hasText(id)) {
	        	bean.addPropertyValue("id", id);
	        }
	        if(StringUtils.hasText(type)) {
	        	bean.addPropertyValue("type", type);
	        }
	        if(StringUtils.hasText(ip)) {
	        	bean.addPropertyValue("ip", ip);
	        }
	        if(StringUtils.hasText(port)) {
	        	bean.addPropertyValue("port", port);
	        }
	        if(StringUtils.hasText(username)) {
	        	bean.addPropertyValue("username", username);
	        }
	        if(StringUtils.hasText(password)) {
	        	bean.addPropertyValue("password", password);
	        }
		}
		
		/**
		 * 解析服务
		 */
		if(Service.class.equals(pojoClass)) {
			String id = element.getAttribute("id");
			String name = element.getAttribute("name");
	        String impl = element.getAttribute("impl");
	        String ref = element.getAttribute("ref");
	        String version = element.getAttribute("version");
	        String weight = element.getAttribute("weight");
	        if(StringUtils.hasText(id)) {
	        	bean.addPropertyValue("id", id);
	        }
	        if(StringUtils.hasText(name)) {
	        	bean.addPropertyValue("name", name);
	        }
	        if(StringUtils.hasText(impl)) {
	        	bean.addPropertyValue("impl", impl);
	        }
	        if(StringUtils.hasText(ref)) {
	        	bean.addPropertyValue("ref", ref);
	        }
	        if(StringUtils.hasText(version)) {
	        	bean.addPropertyValue("version", version);
	        }
	        if(StringUtils.hasText(weight)) {
	        	bean.addPropertyValue("weight", Integer.parseInt(weight));
	        }
		}
		
		/**
		 * 解析引用
		 */
		if(Refrence.class.equals(pojoClass)) {
			String id = element.getAttribute("id");
			String name = element.getAttribute("name");
	        String version = element.getAttribute("version");
	        String timeout = element.getAttribute("timeout");
	        String directServerIp = element.getAttribute("direct_server_ip");
	        String directServerPort = element.getAttribute("direct_server_port");
	        String clusterFault = element.getAttribute("cluster_fault");
	        String useCache = element.getAttribute("use_cache");
	        String cacheTime = element.getAttribute("cache_time");
	        String async = element.getAttribute("async");
	        String searchLocal = element.getAttribute("search_local");
	        String intercept = element.getAttribute("intercept");
	        String interceptor = element.getAttribute("interceptor");
	        if(StringUtils.hasText(id)) {
	        	bean.addPropertyValue("id", id);
	        }
	        if(StringUtils.hasText(name)) {
	        	bean.addPropertyValue("name", name);
	        }
	        if(StringUtils.hasText(version)) {
	        	bean.addPropertyValue("version", version);
	        }
	        if(StringUtils.hasText(timeout)) {
	        	bean.addPropertyValue("timeout", Long.parseLong(timeout));
	        }
	        if(StringUtils.hasText(directServerIp)) {
	        	bean.addPropertyValue("directServerIp", directServerIp);
	        }
	        if(StringUtils.hasText(directServerPort)) {
	        	bean.addPropertyValue("directServerPort", directServerPort);
	        }
	        if(StringUtils.hasText(clusterFault)) {
	        	bean.addPropertyValue("clusterFault", clusterFault);
	        }
	        if(StringUtils.hasText(useCache)) {
	        	bean.addPropertyValue("useCache", Boolean.parseBoolean(useCache));
	        }
	        if(StringUtils.hasText(cacheTime)) {
	        	bean.addPropertyValue("cacheTime", Long.parseLong(cacheTime));
	        }
	        if(StringUtils.hasText(async)) {
	        	bean.addPropertyValue("async", Boolean.parseBoolean(async));
	        }
	        if(StringUtils.hasText(searchLocal)) {
	        	bean.addPropertyValue("searchLocal", Boolean.parseBoolean(searchLocal));
	        }
	        if(StringUtils.hasText(intercept)) {
	        	bean.addPropertyValue("intercept", Boolean.parseBoolean(intercept));
	        }
	        if(StringUtils.hasText(interceptor)) {
	        	bean.addPropertyValue("interceptor", interceptor);
	        }
		}
	}
}