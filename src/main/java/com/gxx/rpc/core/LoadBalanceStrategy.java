package com.gxx.rpc.core;

/**
 * 负载均衡策略
 * 
 * @author Gxx
 */
public enum LoadBalanceStrategy {
	
	POLL("poll", "轮询"), 
	RANDOM("random", "随机"), 
	WEIGHT_POLL("weight_poll", "加权轮询"), 
	WEIGHT_RANDOM("weight_random", "加权随机"), 
	SOURCE_HASH("source_hash", "源地址哈希"), 
	SMALL_CONNECT("small_connect", "最小连接数");

	public String name;
	public String desc;

	/**
	 * 构造方法
	 * 
	 * @param name
	 * @param desc
	 */
	private LoadBalanceStrategy(String name, String desc) {
		this.name = name;
		this.desc = desc;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}
}