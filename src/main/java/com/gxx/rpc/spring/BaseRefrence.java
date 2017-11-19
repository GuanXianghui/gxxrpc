package com.gxx.rpc.spring;

import java.io.Serializable;

/**
 * 引用信息
 * @author Gxx
 */
public class BaseRefrence implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String id;

	private String name;
	
	private String version;
	
	private long timeout;

	private String directServerIp;

	private int directServerPort;
	
	private String clusterFault;
	
	private boolean useCache;
	
	private long cacheTime;
	
	private boolean async;
	
	private boolean searchLocal;
	
	private boolean intercept;
	
	private String interceptor;

	private String applicationName;

	private String ip;
	
	private boolean telnetManage;
	
	private int telnetManagePort;

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

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public long getTimeout() {
		return timeout;
	}

	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}

	public String getDirectServerIp() {
		return directServerIp;
	}

	public void setDirectServerIp(String directServerIp) {
		this.directServerIp = directServerIp;
	}

	public int getDirectServerPort() {
		return directServerPort;
	}

	public void setDirectServerPort(int directServerPort) {
		this.directServerPort = directServerPort;
	}

	public String getClusterFault() {
		return clusterFault;
	}

	public void setClusterFault(String clusterFault) {
		this.clusterFault = clusterFault;
	}

	public boolean isUseCache() {
		return useCache;
	}

	public void setUseCache(boolean useCache) {
		this.useCache = useCache;
	}

	public long getCacheTime() {
		return cacheTime;
	}

	public void setCacheTime(long cacheTime) {
		this.cacheTime = cacheTime;
	}

	public boolean isAsync() {
		return async;
	}

	public void setAsync(boolean async) {
		this.async = async;
	}

	public boolean isSearchLocal() {
		return searchLocal;
	}

	public void setSearchLocal(boolean searchLocal) {
		this.searchLocal = searchLocal;
	}

	public boolean isIntercept() {
		return intercept;
	}

	public void setIntercept(boolean intercept) {
		this.intercept = intercept;
	}

	public String getInterceptor() {
		return interceptor;
	}

	public void setInterceptor(String interceptor) {
		this.interceptor = interceptor;
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

	public boolean isTelnetManage() {
		return telnetManage;
	}

	public void setTelnetManage(boolean telnetManage) {
		this.telnetManage = telnetManage;
	}

	public int getTelnetManagePort() {
		return telnetManagePort;
	}

	public void setTelnetManagePort(int telnetManagePort) {
		this.telnetManagePort = telnetManagePort;
	}

	@Override
	public String toString() {
		return "BaseRefrence [id=" + id + ", name=" + name + ", version=" + version + ", timeout=" + timeout + ", directServerIp="
				+ directServerIp + ", directServerPort=" + directServerPort + ", clusterFault=" + clusterFault
				+ ", useCache=" + useCache + ", cacheTime=" + cacheTime + ", async=" + async + ", searchLocal="
				+ searchLocal + ", intercept=" + intercept + ", interceptor=" + interceptor + ", applicationName="
				+ applicationName + ", ip=" + ip + ", telnetManage=" + telnetManage + ", telnetManagePort=" + telnetManagePort + "]";
	}
}
