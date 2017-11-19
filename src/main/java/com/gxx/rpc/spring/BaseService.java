package com.gxx.rpc.spring;

import java.io.Serializable;

/**
 * 服务信息
 * @author Gxx
 */
public class BaseService implements Serializable {

	private static final long serialVersionUID = 1L;

	private String id;

	private String name;

	private String impl;

	private String ref;
	
	private String version;

	private int weight;

	private String applicationName;

	private String ip;

	private int port;

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

	public String getImpl() {
		return impl;
	}

	public void setImpl(String impl) {
		this.impl = impl;
	}

	public String getRef() {
		return ref;
	}

	public void setRef(String ref) {
		this.ref = ref;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	public String getApplicationName() {
		return applicationName;
	}

	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	@Override
	public String toString() {
		return "BaseService [id=" + id + ", name=" + name + ", impl=" + impl + ", ref=" + ref + ", version=" + version
				+ ", weight=" + weight + ", applicationName=" + applicationName + ", ip=" + ip + ", port=" + port + "]";
	}
}
