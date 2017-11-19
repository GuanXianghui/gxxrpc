package com.gxx.rpc.utils;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.log4j.Logger;

import com.gxx.rpc.core.LoadBalanceStrategy;
import com.gxx.rpc.register.RegisterUtils;
import com.gxx.rpc.spring.BaseRefrence;
import com.gxx.rpc.spring.BaseService;
import com.gxx.rpc.spring.Client;
import com.gxx.rpc.spring.SpringUtil;

/** 
 * 负载均衡工具类
 * @author Gxx
 */
public class LoadBalanceUtil {

	/**
	 * 日志记录器
	 */
	private static Logger logger = Logger.getLogger(LoadBalanceUtil.class);
	
	/**
	 * 引用调用集合
	 * 格式：引用接口名:次数
	 */
	private static Map<String, Long> refrenceInvokeMap = new HashMap<>();
	
	public static Map<String, Long> getRefrenceInvokeMap() {
		return refrenceInvokeMap;
	}

	/**
	 * 经过负载均衡算法选择服务
	 * @param refrenceName 引用
	 * @return
	 */
	public static BaseService chooseService(String refrenceName) throws Exception {
		/**
		 * 判断直接指向服务，不从注册中心负载均衡路由
		 * 注意：直接指向服务，忽略版本
		 */
		BaseService directService = checkDirectService(refrenceName);
		if(null != directService) {
			return directService;
		}
		
		/**
		 * 查找引用信息
		 */
		BaseRefrence baseRefrence = RefrenceUtil.getBaseRefrence(refrenceName);
		
		/**
		 * 获取引用服务
		 */
		List<BaseService> serviceList = RegisterUtils.getServiceList(refrenceName, baseRefrence.getVersion());
		logger.info("服务方信息:" + serviceList);
		
		/**
		 * 判无服务
		 */
		if(null == serviceList || serviceList.size() == 0) {
			throw new RuntimeException("无可用服务！");
		}
		
		/**
		 * 引用调用次数加1
		 */
		long count = addRefrenceInvoke(refrenceName);
		logger.info("目前第[" + count + "]次调用[" + refrenceName + "]");
		
		/**
		 * 只有一个服务提供方，则直接返回
		 */
		if(serviceList.size() == 1) {
			return serviceList.get(0);
		}
		
		Client client = (Client)SpringUtil.getApplicationContext().getBean("client");
		logger.info("负载均衡算法：" + client.getLoadBalance());
		
		/**
		 * 负载均衡算法路由服务
		 */
		BaseService service;
		if(LoadBalanceStrategy.POLL.getName().equals(client.getLoadBalance())) {//轮询
			service = choosePoolService(count, serviceList);
		} else if(LoadBalanceStrategy.RANDOM.getName().equals(client.getLoadBalance())) {//随机
			service = chooseRandomService(serviceList);
		} else if(LoadBalanceStrategy.WEIGHT_POLL.getName().equals(client.getLoadBalance())) {//加权轮询
			service = chooseWeightPoolService(count, serviceList);
		} else if(LoadBalanceStrategy.WEIGHT_RANDOM.getName().equals(client.getLoadBalance())) {//加权随机
			service = chooseWeightRandomService(serviceList);
		} else if(LoadBalanceStrategy.SOURCE_HASH.getName().equals(client.getLoadBalance())) {//源地址哈希
			service = chooseSourceHashService(serviceList);
		} else if(LoadBalanceStrategy.SMALL_CONNECT.getName().equals(client.getLoadBalance())) {//最小连接数
			throw new RuntimeException("暂不支持负载均衡策略[" + client.getLoadBalance() + "-最小连接数]！");
		} else {
			throw new RuntimeException("不支持该负载均衡策略[" + client.getLoadBalance() + "]！");
		}
		return service;
	}
	
	/**
	 * 判断直接指向服务，不从注册中心负载均衡路由
	 * @param refrenceName
	 * @return
	 */
	private static BaseService checkDirectService(String refrenceName) {
		/**
		 * 查找引用信息
		 */
		BaseRefrence baseRefrence = RefrenceUtil.getBaseRefrence(refrenceName);
		if(null != baseRefrence && !"".equals(baseRefrence.getDirectServerIp()) 
				&& 0 < baseRefrence.getDirectServerPort()) {
			BaseService baseService = new BaseService();
			baseService.setIp(baseRefrence.getDirectServerIp());
			baseService.setPort(baseRefrence.getDirectServerPort());
			logger.info("直接指向:" + baseRefrence.getDirectServerIp() + ":" + baseRefrence.getDirectServerPort());
			return baseService;
		}
		
		return null;
	}

	/**
	 * 引用调用次数加1
	 * @param refrenceName
	 * @return
	 */
	private static synchronized long addRefrenceInvoke(String refrenceName) {
		Long count = refrenceInvokeMap.get(refrenceName);
		if(null == count) {
			count = 0l;
		}
		count ++;
		refrenceInvokeMap.put(refrenceName, count);
		return count;
	}

	/**
	 * 轮询选择服务
	 * @param serviceList
	 * @return
	 */
	private static synchronized BaseService choosePoolService(Long count, List<BaseService> serviceList) {
		/**
		 * 拷贝新的服务集合，防止订阅服务并发操作集合错乱
		 */
		List<BaseService> copyList = new ArrayList<>(serviceList);
		return copyList.get((int)(count%copyList.size()));
	}

	/**
	 * 随机选择服务
	 * @param serviceList
	 * @return
	 */
	private static BaseService chooseRandomService(List<BaseService> serviceList) {
		/**
		 * 拷贝新的服务集合，防止订阅服务并发操作集合错乱
		 */
		List<BaseService> copyList = new ArrayList<>(serviceList);
		int random = new Random().nextInt(copyList.size());
		return copyList.get(random);
	}

	/**
	 * 加权轮询选择服务
	 * @param serviceList
	 * @return
	 */
	private static BaseService chooseWeightPoolService(Long count, List<BaseService> serviceList) {
		/**
		 * 拷贝新的服务集合，防止订阅服务并发操作集合错乱
		 */
		List<BaseService> copyList = new ArrayList<>(serviceList);
		List<BaseService> newServiceList = new ArrayList<>();
		for(BaseService baseService : copyList) {
			for(int i=0;i<baseService.getWeight();i++) {
				newServiceList.add(baseService);
			}
		}
		/**
		 * 权重过滤后，有可能服务集合为空
		 */
		if(newServiceList.size() == 0) {
			throw new RuntimeException("无可用服务！");
		}
		return newServiceList.get((int)(count%newServiceList.size()));
	}

	/**
	 * 加权随机选择服务
	 * @param serviceList
	 * @return
	 */
	private static BaseService chooseWeightRandomService(List<BaseService> serviceList) {
		/**
		 * 拷贝新的服务集合，防止订阅服务并发操作集合错乱
		 */
		List<BaseService> copyList = new ArrayList<>(serviceList);
		List<BaseService> newServiceList = new ArrayList<>();
		for(BaseService baseService : copyList) {
			for(int i=0;i<baseService.getWeight();i++) {
				newServiceList.add(baseService);
			}
		}
		/**
		 * 权重过滤后，有可能服务集合为空
		 */
		if(newServiceList.size() == 0) {
			throw new RuntimeException("无可用服务！");
		}
		int random = new Random().nextInt(newServiceList.size());
		return newServiceList.get(random);
	}
		
	/**
	 * 源地址哈希选择服务
	 * @param serviceList
	 * @return
	 */
	private static BaseService chooseSourceHashService(List<BaseService> serviceList) throws Exception {
		List<BaseService> copyList = new ArrayList<>(serviceList);
		String localIp = InetAddress.getLocalHost().getHostAddress();
		return copyList.get((int)(localIp.hashCode()%copyList.size()));
	}
}