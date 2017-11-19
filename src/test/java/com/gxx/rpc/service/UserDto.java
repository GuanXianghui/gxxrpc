package com.gxx.rpc.service;

import java.io.Serializable;

/** 
 * 用户传输对象
 * @author Gxx
 */
public class UserDto implements Serializable {

	private static final long serialVersionUID = 1L;

	private String userName;
	
	private int age;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	@Override
	public String toString() {
		return "UserDto [userName=" + userName + ", age=" + age + "]";
	}
}
