package com.gxx.rpc.core;

import java.io.Serializable;

import org.msgpack.annotation.Message;

/**
 * rpc传输响应
 * @author Gxx
 */
@Message
public class RpcResponse implements Serializable {

	private static final long serialVersionUID = 1L;

	private boolean isSuccess;//成功/异常

	private Object result;//结果

	private Throwable throwable;//异常信息

	public boolean isSuccess() {
		return isSuccess;
	}

	public void setSuccess(boolean isSuccess) {
		this.isSuccess = isSuccess;
	}

	public Throwable getThrowable() {
		return throwable;
	}

	public void setThrowable(Throwable throwable) {
		this.throwable = throwable;
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}

	@Override
	public String toString() {
		return "RpcResponse [isSuccess=" + isSuccess + ", result=" + result + ", throwable=" + throwable + "]";
	}
}