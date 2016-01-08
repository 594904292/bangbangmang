package com.bbxiaoqu.ui.community;

public class Community {
	private String id;  
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	private String name;  
    private String address;  
      
    public String getName() {  
        return name;  
    }  
    public void setName(String name) {  
        this.name = name;  
    }  
    public String getAddress() {  
        return address;  
    }  
    public void setAddress(String address) {  
        this.address = address;  
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
	public String getPic() {
		return pic;
	}
	public void setPic(String pic) {
		this.pic = pic;
	}
	public String getBusiness() {
		return business;
	}
	public void setBusiness(String business) {
		this.business = business;
	}
	public String getDevelop() {
		return develop;
	}
	public void setDevelop(String develop) {
		this.develop = develop;
	}
	public String getPropertymanagement() {
		return propertymanagement;
	}
	public void setPropertymanagement(String propertymanagement) {
		this.propertymanagement = propertymanagement;
	}
	public String getPropertytype() {
		return propertytype;
	}
	public void setPropertytype(String propertytype) {
		this.propertytype = propertytype;
	}
	public String getHomenumber() {
		return homenumber;
	}
	public void setHomenumber(String homenumber) {
		this.homenumber = homenumber;
	}
	public String getBuildyear() {
		return buildyear;
	}
	public void setBuildyear(String buildyear) {
		this.buildyear = buildyear;
	}
	private String lat;
    private String lng;
    private String pic;
    private String business;
    private String develop;
    private String propertymanagement;
    private String propertytype;
    private String homenumber;
    private String buildyear; 
    private String distance;
	public String getDistance() {
		return distance;
	}
	public void setDistance(String distance) {
		this.distance = distance;
	} 
	 private int isgz;
	public int getIsgz() {
		return isgz;
	}
	public void setIsgz(int isgz) {
		this.isgz = isgz;
	}
}
