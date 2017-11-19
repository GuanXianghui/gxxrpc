package com.gxx.rpc.spring;

/** 
 * 注册中心
 * @author Gxx
 */
public class Register {

	private String id;

	private String type;

	private String ip;

	private String port;

	private String username;

	private String password;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String toString() {
		return "Register [id=" + id + ", type=" + type + ", ip=" + ip + ", port=" + port
				+ ", username=" + username + ", password=" + password + "]";
	}
}
