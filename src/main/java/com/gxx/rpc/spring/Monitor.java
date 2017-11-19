package com.gxx.rpc.spring;

/** 
 * 监控
 * @author Gxx
 */
public class Monitor {

	private String id;

	private String ip;

	private int port;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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
		return "Monitor [id=" + id + ", ip=" + ip + ", port=" + port + "]";
	}
}
