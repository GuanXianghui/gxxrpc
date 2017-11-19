package com.gxx.rpc.core;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.msgpack.annotation.Message;

/**
 * rpc传输请求
 * @author Gxx
 */
@Message
public class RpcRequest implements Serializable {

	private static final long serialVersionUID = 1L;

	private String uuid;//宇宙唯一键

	private String className;//服务名称

	private String methodName;//方法名称

	private String[] types;//参数类型

	private Object[] args;//参数
	
	private Map<String, Object> shadowParameterMap = new HashMap<>();//隐式参数
	
	private String clientApplicationName;//客户端应用名称
	
	private String clientIp;//客户端ip
	
	private boolean isGeneral;//是否泛化调用，服务端判断泛化，结果转json

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public String[] getTypes() {
		return types;
	}

	public void setTypes(String[] types) {
		this.types = types;
	}

	public Object[] getArgs() {
		return args;
	}

	public void setArgs(Object[] args) {
		this.args = args;
	}

	public Map<String, Object> getShadowParameterMap() {
		return shadowParameterMap;
	}

	public void setShadowParameterMap(Map<String, Object> shadowParameterMap) {
		this.shadowParameterMap = shadowParameterMap;
	}

	public String getClientApplicationName() {
		return clientApplicationName;
	}

	public void setClientApplicationName(String clientApplicationName) {
		this.clientApplicationName = clientApplicationName;
	}

	public String getClientIp() {
		return clientIp;
	}

	public void setClientIp(String clientIp) {
		this.clientIp = clientIp;
	}

	public boolean isGeneral() {
		return isGeneral;
	}

	public void setGeneral(boolean isGeneral) {
		this.isGeneral = isGeneral;
	}

	@Override
	public String toString() {
		return "RpcRequest [uuid=" + uuid + ", className=" + className + ", methodName=" + methodName + ", types="
				+ Arrays.toString(types) + ", args=" + Arrays.toString(args) + ", shadowParameterMap="
				+ shadowParameterMap + ", clientApplicationName=" + clientApplicationName + ", clientIp=" + clientIp
				+ ", isGeneral=" + isGeneral + "]";
	}
}
