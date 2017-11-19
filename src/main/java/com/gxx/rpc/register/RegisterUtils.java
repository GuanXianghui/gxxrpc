package com.gxx.rpc.register;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.gxx.rpc.spring.BaseRefrence;
import com.gxx.rpc.spring.BaseService;
import com.gxx.rpc.spring.Register;
import com.gxx.rpc.spring.SpringUtil;

/** 
 * 注册工具类
 * @author Gxx
 */
public class RegisterUtils {

	/**
	 * 日志记录器
	 */
	private static Logger logger = Logger.getLogger(RegisterUtils.class);
	
	/**
	 * 引用服务集合
	 */
	private static Map<String, List<BaseService>> refrenceServiceMap = new HashMap<>();

	/**
	 * 获取引用服务集合
	 * @return
	 */
	public static Map<String, List<BaseService>> getRefrenceServiceMap() {
		return refrenceServiceMap;
	}
	
	/**
	 * 发布服务到注册中心
	 * @param service
	 */
	public static void registService(BaseService baseService) {
		/**
		 * 判断配置注册中心
		 */
		if(!SpringUtil.getApplicationContext().containsBean("register")) {
			return;
		}
		String basePath = "/gxxrpc/" + baseService.getName() + "/provider";
		String path = basePath + "/" + baseService.getIp() + "_" + baseService.getPort();
		logger.info("注册服务:[" + path + "]," + baseService);
		/**
		 * 获取注册中心
		 */
		Register register = (Register)SpringUtil.getApplicationContext().getBean("register");
		if("zookeeper".equals(register.getType())) {
			logger.info("zookeeper注册服务");
			ZookeeperClient.getInstance().createOrUpdateNodePersistent(basePath, null);//创建[永久保留，不删除]节点
			ZookeeperClient.getInstance().createOrUpdateNode(path, baseService);//创建临时节点
		}
	}
	
	/**
	 * 发布引用到注册中心
	 * @param baseRefrence
	 */
	public static void registRefrence(BaseRefrence baseRefrence) {
		/**
		 * 判断配置注册中心
		 */
		if(!SpringUtil.getApplicationContext().containsBean("register")) {
			return;
		}
		String basePath = "/gxxrpc/" + baseRefrence.getName() + "/consumer";
		String path = basePath + "/" + baseRefrence.getIp();
		logger.info("注册引用:[" + path + "]," + baseRefrence);
		/**
		 * 获取注册中心
		 */
		Register register = (Register)SpringUtil.getApplicationContext().getBean("register");
		if("zookeeper".equals(register.getType())) {
			logger.info("zookeeper注册引用");
			ZookeeperClient.getInstance().createOrUpdateNodePersistent(basePath, null);//创建[永久保留，不删除]节点
			ZookeeperClient.getInstance().createOrUpdateNode(path, baseRefrence);//创建临时节点
		}
	}
	
	/**
	 * 同步最新引用服务
	 * @param refrenceName
	 */
	public synchronized static void synRefrenceService(String refrenceName) {
		/**
		 * 判断配置注册中心
		 */
		if(!SpringUtil.getApplicationContext().containsBean("register")) {
			return;
		}
		String path = "/gxxrpc/" + refrenceName + "/provider";
		logger.info("同步最新引用服务:[" + path + "]");
		/**
		 * 获取注册中心
		 */
		Register register = (Register)SpringUtil.getApplicationContext().getBean("register");
		if("zookeeper".equals(register.getType())) {
			List<String> nodes = ZookeeperClient.getInstance().getChildNodes(path);
			List<BaseService> baseServices = new ArrayList<>();
			for(String node : nodes) {
				String tempPath = path + "/" + node;
				baseServices.add((BaseService)ZookeeperClient.getInstance().getNode(tempPath));
			}
			logger.info("所有服务:" + baseServices);
			refrenceServiceMap.put(refrenceName, baseServices);
		}
	}
	
	/**
	 * 订阅服务变化
	 * @param refrenceName
	 */
	public synchronized static void subscribeServiceChanges(String refrenceName) {
		/**
		 * 判断配置注册中心
		 */
		if(!SpringUtil.getApplicationContext().containsBean("register")) {
			return;
		}
		String path = "/gxxrpc/" + refrenceName + "/provider";
		logger.info("订阅服务变化:[" + path + "]");
		/**
		 * 订阅子目录变化
		 */
		ZookeeperClient.getInstance().subscribeChildChanges(path, 
				new ServiceChangeListener(refrenceName));
	}
	
	/**
	 * 获取引用服务
	 * @param refrenceName
	 * @param version 版本若非空，则需要匹配
	 * @return
	 */
	public synchronized static List<BaseService> getServiceList(String refrenceName, String version) {
		List<BaseService> tempList = new ArrayList<>();
		if(refrenceServiceMap.containsKey(refrenceName)) {
			tempList = new ArrayList<>(refrenceServiceMap.get(refrenceName));
		}
		/**
		 * 版本为空，则忽略
		 */
		if(null == version || "".equals(version)) {
			return tempList;
		}
		List<BaseService> serviceList = new ArrayList<>();
		for(BaseService baseService : tempList) {
			if(version.equals(baseService.getVersion())) {
				serviceList.add(baseService);
			}
		}
		return serviceList;
	}

	/**
	 * 服务从注册中心下掉
	 * @param baseService
	 */
	public static void unregistService(BaseService baseService) {
		/**
		 * 判断配置注册中心
		 */
		if(!SpringUtil.getApplicationContext().containsBean("register")) {
			return;
		}
		String path = "/gxxrpc/" + baseService.getName() + "/provider/" + baseService.getIp() + "_" + baseService.getPort();
		logger.info("服务从注册中心下掉:" + path);
		/**
		 * 获取注册中心
		 */
		Register register = (Register)SpringUtil.getApplicationContext().getBean("register");
		if("zookeeper".equals(register.getType())) {
			ZookeeperClient.getInstance().delete(path);
		}
	}

	/**
	 * 引用从注册中心下掉
	 * @param baseRefrence
	 */
	public static void unregistRefrence(BaseRefrence baseRefrence) {
		/**
		 * 判断配置注册中心
		 */
		if(!SpringUtil.getApplicationContext().containsBean("register")) {
			return;
		}
		String path = "/gxxrpc/" + baseRefrence.getName() + "/consumer/" + baseRefrence.getIp();
		logger.info("引用从注册中心下掉:" + path);
		/**
		 * 获取注册中心
		 */
		Register register = (Register)SpringUtil.getApplicationContext().getBean("register");
		if("zookeeper".equals(register.getType())) {
			ZookeeperClient.getInstance().delete(path);
		}
	}
}
