package com.gxx.rpc.register;

import java.util.ArrayList;
import java.util.List;

import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.ZkClient;
import org.apache.log4j.Logger;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

import com.gxx.rpc.spring.Register;
import com.gxx.rpc.spring.SpringUtil;

/**
 * zookeeper客户端
 * 
 * @author Gxx
 */
public class ZookeeperClient {

	/**
	 * 日志记录器
	 */
	Logger logger = Logger.getLogger(ZookeeperClient.class);

	/**
	 * zookeeper客户端
	 */
	ZkClient zkClient;

	/**
	 * 单例
	 */
	private static ZookeeperClient instance = null;

	/**
	 * 获取单例
	 * @return
	 */
	public static ZookeeperClient getInstance() {
		if(null == instance) {
			instance = new ZookeeperClient(null, "");	
		}
		return instance;
	}

	/**
	 * 获取单例
	 * @param ip
	 * @param port
	 * @return
	 */
	public static ZookeeperClient getInstance(String ip, String port) {
		if(null == instance) {
			instance = new ZookeeperClient(ip, port);	
		}
		return instance;
	}

	/**
	 * 构造方法-传入ip，端口
	 */
	private ZookeeperClient(String ip, String port) {
		/**
		 * 初始化连接
		 */
		initConnect(ip, port);
	}

	/**
	 * 初始化连接
	 * @param ip
	 * @param port
	 */
	private void initConnect(String ip, String port) {
		/**
		 * 判断ip，端口非空
		 */
		if(null != ip && !"".equals(ip) && null != port && !"".equals(port)) {
			zkClient = new ZkClient(ip + ":" + port);
		} else {
			/**
			 * 获取注册中心
			 */
			Register register = (Register)SpringUtil.getApplicationContext().getBean("register");
			zkClient = new ZkClient(register.getIp() + ":" + register.getPort());
		}
	}

	/**
	 * 判断节点是否存在
	 * 
	 * @param path
	 */
	public boolean exists(String path) {
		return zkClient.exists(path);
	}

	/**
	 * 获取节点
	 * 
	 * @param path
	 * @return
	 */
	public Object getNode(String path) {
		if(!exists(path)) {
			return null;
		}
		return zkClient.readData(path, new Stat());
	}

	/**
	 * 获取子节点
	 * 
	 * @param path
	 * @return
	 */
	public List<String> getChildNodes(String path) {
		if(!exists(path)) {
			return new ArrayList<>();
		}
		return zkClient.getChildren(path);
	}
	
	/**
	 * 创建[永久保留，不删除]节点
	 * @param path
	 */
	public void createOrUpdateNodePersistent(String path, Object object) {
		if(exists(path)) {
			zkClient.writeData(path, object);
		} else {
			/**
			 * 比如:/A/B/C
			 */
			String[] paths = path.substring(1).split("/");
			String temp = "";
			for(String dir : paths) {
				temp += "/" + dir;
				if(!exists(temp)) {
					/**
					 * CreateMode.PERSISTENT:节点永久保留，不删除
					 * CreateMode.EPHEMERAL:当与zookeeper连接断开，自动删除(过20秒左右)
					 */
					zkClient.create(temp, null, CreateMode.PERSISTENT);
				}
			}
			zkClient.writeData(path, object);
		}
	}

	/**
	 * 创建临时节点
	 *
	 * @param path
	 * @param object
	 */
	public void createOrUpdateNode(String path, Object object) {
		if(exists(path)) {
			zkClient.writeData(path, object);
		} else {
			/**
			 * 比如:/A/B/C
			 */
			String[] paths = path.substring(1).split("/");
			String temp = "";
			for(String dir : paths) {
				temp += "/" + dir;
				if(!exists(temp)) {
					/**
					 * CreateMode.PERSISTENT:节点永久保留，不删除
					 * CreateMode.EPHEMERAL:当与zookeeper连接断开，自动删除(过20秒左右)
					 */
					zkClient.create(temp, null, CreateMode.EPHEMERAL);
				}
			}
			zkClient.writeData(path, object);
		}
	}

	/**
	 * 删除节点
	 * 
	 * @param path
	 */
	public boolean delete(String path) {
		if(!exists(path)) {
			return true;
		}
		return zkClient.delete(path);
	}
	
	/**
	 * 订阅子目录变化
	 * @param path
	 * @param listener
	 */
	public void subscribeChildChanges(String path, IZkChildListener listener) {
		zkClient.subscribeChildChanges(path, listener);
	}
}
