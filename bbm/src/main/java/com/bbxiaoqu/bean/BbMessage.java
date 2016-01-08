package com.bbxiaoqu.bean;

import java.text.SimpleDateFormat;
import java.util.Date;

public class BbMessage
{
	
	
	
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


	public String getCommunity() {
		return community;
	}


	public void setCommunity(String community) {
		this.community = community;
	}


	public String getAddress() {
		return address;
	}


	public void setAddress(String address) {
		this.address = address;
	}


	public double getLng() {
		return lng;
	}


	public void setLng(double lng) {
		this.lng = lng;
	}


	public double getLat() {
		return lat;
	}


	public void setLat(double lat) {
		this.lat = lat;
	}


	public String getGuid() {
		return guid;
	}


	public void setGuid(String guid) {
		this.guid = guid;
	}


	public String getInfocatagroy() {
		return infocatagroy;
	}


	public void setInfocatagroy(String infocatagroy) {
		this.infocatagroy = infocatagroy;
	}


	public String getMessage() {
		return message;
	}


	public void setMessage(String message) {
		this.message = message;
	}


	public String getIcon() {
		return icon;
	}


	public void setIcon(String icon) {
		this.icon = icon;
	}


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


	public Date getDate() {
		return date;
	}
	
	public void setDate(Date date)
	{
		this.date = date;
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		this.dateStr = df.format(date);
	}

	
	public void setDateStr(String dateStr)
	{
		this.dateStr = dateStr;
	}

	public String getDateStr() {
		return dateStr;
	}
	


	private String senduserId;
	private String sendnickname;
	
	private String community;
	private String address;
	private double lng;
	private double lat;
	
	private String guid;	
	private String infocatagroy;		
	private String message;	
	private String icon;
	private Date date;	
	private String dateStr;
	
	


	private boolean isComing;
	private boolean readed;
	public BbMessage()
	{
		
	}
	public BbMessage(String senduserId, String sendnickname,
			String community, String address, double lng, double lat,
			String guid, String infocatagroy, String message, String icon,
			Date date, String dateStr, boolean isComing, boolean readed) {
		super();
		this.senduserId = senduserId;
		this.sendnickname = sendnickname;
		this.community = community;
		this.address = address;
		this.lng = lng;
		this.lat = lat;
		this.guid = guid;
		this.infocatagroy = infocatagroy;
		this.message = message;
		this.icon = icon;
		this.date = date;
		this.dateStr = dateStr;
		this.isComing = isComing;
		this.readed = readed;
	}

	

}
