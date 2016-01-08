package com.bbxiaoqu.bean;

public class PayBase {

	private Integer id;
	
	
	private String user;
	private String score;
	private String money;
	private String tool;
	private String date;
	private String status;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getScore() {
		return score;
	}
	public void setScore(String score) {
		this.score = score;
	}
	public String getMoney() {
		return money;
	}
	public void setMoney(String money) {
		this.money = money;
	}
	public String getTool() {
		return tool;
	}
	public void setTool(String tool) {
		this.tool = tool;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public PayBase(int id, String user, String score, String money,
			String tool, String date, String status) {
		super();
		this.id = id;
		this.user = user;
		this.score = score;
		this.money = money;
		this.tool = tool;
		this.date = date;
		this.status = status;
	}
	
	
	
}
