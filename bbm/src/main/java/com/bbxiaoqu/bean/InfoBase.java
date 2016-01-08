package com.bbxiaoqu.bean;

public class InfoBase {

	private Integer id;
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getLat() {
		return lat;
	}

	public void setLat(String lat) {
		this.lat = lat;
	}

	public String getLng() {
		return lng;
	}

	public void setLng(String lng) {
		this.lng = lng;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}

	public String getVoice() {
		return voice;
	}

	public void setVoice(String voice) {
		this.voice = voice;
	}

	public String getSendtime() {
		return sendtime;
	}

	public void setSendtime(String sendtime) {
		this.sendtime = sendtime;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
	private String senduser;
	public String getSenduser() {
		return senduser;
	}

	public void setSenduser(String senduser) {
		this.senduser = senduser;
	}
	
	private String lat;
	private String lng;
	private String title;
	private String content;
	private String photo;
	private String voice;
	private String sendtime;
	private String address;
	private String headface;
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
	private String guid;
	private String infocatagroy;

	public String getHeadface() {
		return headface;
	}

	public void setHeadface(String headface) {
		this.headface = headface;
	}

	public InfoBase(Integer id, String senduser, String lat, String lng,
			String title, String content, String photo, String voice,
			String sendtime, String address, String headface, String guid,
			String infocatagroy) {
		super();
		this.id = id;
		this.senduser = senduser;
		this.lat = lat;
		this.lng = lng;
		this.title = title;
		this.content = content;
		this.photo = photo;
		this.voice = voice;
		this.sendtime = sendtime;
		this.address = address;
		this.headface = headface;
		this.guid = guid;
		this.infocatagroy = infocatagroy;
	}

	
}
