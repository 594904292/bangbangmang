package com.bbxiaoqu.comm.service;

import java.io.Serializable;

public class User implements Serializable{
	private int id;
	private String nickname="";
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	private String userid="";
	private String password="";
	private String  telphone="";
	private String  headface="";

	public String getHeadface() {
		return headface;
	}
	public void setHeadface(String headface) {
		this.headface = headface;
	}
	public String getTelphone() {
		return telphone;
	}
	public void setTelphone(String telphone) {
		this.telphone = telphone;
	}
	public User() {
		super();
		// TODO Auto-generated constructor stub
	}
	public User(String nickname,String userid, String password, String tel, String headface) {
		super();
		this.nickname = nickname;
		this.userid = userid;
		this.password = password;
		this.telphone = tel;
		this.headface = headface;
		
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getUsername() {
		return userid;
	}
	public void setUsername(String username) {
		this.userid = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	@Override
	public String toString() {
		return "User [id=" + id + ", nickname=" + nickname + ", username=" + userid + ", password="
				+ password + ", telphone=" + telphone + "]";
	}
	
}
