package com.bbxiaoqu.bean;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ChatMessage
{
	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public Date getDate() {
		return date;
	}
	
	public void setDate(Date date)
	{
		this.date = date;	
		this.dateStr = df.format(date);
	}

	
	public void setDateStr(String dateStr)
	{
		this.dateStr = dateStr;
	}

	public String getDateStr() {
		return dateStr;
	}
	
	public String getSenduserId() {
		return senduserId;
	}

	public void setSenduserId(String senduserId) {
		this.senduserId = senduserId;
	}

	public String getSendnickname() {
		return sendnickname;
	}

	public void setSendnickname(String sendnickname) {
		this.sendnickname = sendnickname;
	}

	public String getSenduserIcon() {
		return senduserIcon;
	}

	public void setSenduserIcon(String senduserIcon) {
		this.senduserIcon = senduserIcon;
	}

	public String getTouserId() {
		return touserId;
	}

	public void setTouserId(String touserId) {
		this.touserId = touserId;
	}

	public String getTonickname() {
		return tonickname;
	}

	public void setTonickname(String tonickname) {
		this.tonickname = tonickname;
	}

	public String getTouserIcon() {
		return touserIcon;
	}

	public void setTouserIcon(String touserIcon) {
		this.touserIcon = touserIcon;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	private String senduserId;
	private String sendnickname;
	private String senduserIcon;	
	private String touserId;
	private String tonickname;
	private String touserIcon;
	private String message;	
	private Date date;	
	private String dateStr;
	private String guid;	
	public boolean isComing() {
		return isComing;
	}

	public void setComing(boolean isComing) {
		this.isComing = isComing;
	}

	public boolean isReaded() {
		return readed;
	}

	public void setReaded(boolean readed) {
		this.readed = readed;
	}

	private boolean isComing;
	private boolean readed;
	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}

	public ChatMessage()
	{
		
	}
	
	public ChatMessage(String guid,String senduserId,String touserId,String message, String dateStr
		)
	{
		super();
		this.guid = guid;
		this.senduserId = senduserId;
		this.sendnickname = sendnickname;
		this.senduserIcon = senduserIcon;		
		this.touserId = senduserId;
		this.tonickname = sendnickname;
		this.touserIcon = senduserIcon;		
		this.message = message;
		this.date = date;
		this.dateStr = dateStr;
	}


}
