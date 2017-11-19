package com.gxx.rpc.core;

/**
 * 集群容错策略
 * 
 * @author Gxx
 */
public enum ClusterFaultStrategy {
	
	FAIL_FAST("fail_fast", "快速失败"), 
	FAIL_RETRY("fail_retry", "失败重试"), 
	FAIL_SAFE("fail_safe", "失败安全"), 
	BROADCAST_FAIL("broadcast_fail", "广播失败"), 
	BROADCAST_SAFE("broadcast_safe", "广播安全");

	public String name;
	public String desc;

	/**
	 * 构造方法
	 * 
	 * @param name
	 * @param desc
	 */
	private ClusterFaultStrategy(String name, String desc) {
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