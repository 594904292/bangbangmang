package com.bbxiaoqu.ui.sub;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.json.JSONArray;
import org.json.JSONObject;

import com.bbxiaoqu.R;
import com.bbxiaoqu.bean.InfoBase;
import com.bbxiaoqu.comm.tool.StreamTool;

public class MessageInfoBean implements Serializable
{
	private static final long serialVersionUID = -758459502806858414L;
	/**
	 * 精度
	 */
	private double latitude;
	/**
	 * 纬度
	 */
	private double longitude;
	/**
	 * 图片ID，真实项目中可能是图片路径
	 */
	private String imgId;
	/**
	 * 商家名称
	 */
	private String name;
	/**
	 * 距离
	 */
	private String distance;
	/**
	 * 赞数量
	 */
	private int zannum;
	
	
	/**
	 * 赞数量
	 */
	private int iszan;

	public int getIszan() {
		return iszan;
	}


	public void setIszan(int iszan) {
		this.iszan = iszan;
	}
	
	

	/*static
	{
		infos.add(new Info(34.242652, 108.971171, R.drawable.a01, "英伦贵族小区",
				"距离209米", 1456));
		infos.add(new Info(34.242952, 108.972171, R.drawable.a02, "沙井国际洗浴小区",
				"距离897米", 456));
		infos.add(new Info(34.242852, 108.973171, R.drawable.a03, "五环服装城小区",
				"距离249米", 1456));
		infos.add(new Info(34.242152, 108.971971, R.drawable.a04, "老米家泡馍小区",
				"距离679米", 1456));
	}*/
	
	
	public static double getDistance(double lat1, double lng1, double lat2, double lng2) 
	{ 
		double earthRadius = 6367000; //approximate radius of earth in meters 	 
		/* 
		Convert these degrees to radians 
		to work with the formula 
		*/
		 
		lat1 = (lat1 * Math.PI ) / 180; 
		lng1 = (lng1 * Math.PI) / 180; 
		 
		lat2 = (lat2 * Math.PI ) / 180; 
		lng2 = (lng2 * Math.PI ) / 180; 
		 
		/* 
		Using the 
		Haversine formula 	 
		http://en.wikipedia.org/wiki/Haversine_formula 	 
		calculate the distance 
		*/	 
		double calcLongitude = lng2 - lng1; 
		double calcLatitude = lat2 - lat1; 
		double stepOne = Math.pow(Math.sin(calcLatitude/2), 2) + Math.cos(lat1) * Math.cos(lat2) * Math.pow(Math.sin(calcLongitude / 2), 2); 
		
		double stepTwo = 2 * Math.asin(Math.min(1, Math.sqrt(stepOne))); 
		double calculatedDistance = earthRadius * stepTwo; 
		 
		return Math.round(calculatedDistance); //四舍五入
	}

	
	/**
	 * 赞数量
	 */
/*	private int iszan;

	public int getIszan() {
		return iszan;
	}


	public void setIszan(int iszan) {
		this.iszan = iszan;
	}*/
	

	private String id;
	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public MessageInfoBean(String id,double latitude, double longitude, String imgId, String name,
			String distance, int zannum,int iszan)
	{
		super();
		this.id = id;
		this.latitude = latitude;
		this.longitude = longitude;
		this.imgId = imgId;
		this.name = name;
		this.distance = distance;
		this.zannum = zannum;
		this.iszan=iszan;
	}

	public double getLatitude()
	{
		return latitude;
	}

	public void setLatitude(double latitude)
	{
		this.latitude = latitude;
	}

	public double getLongitude()
	{
		return longitude;
	}

	public void setLongitude(double longitude)
	{
		this.longitude = longitude;
	}

	public String getName()
	{
		return name;
	}

	public String getImgId()
	{
		return imgId;
	}

	public void setImgId(String imgId)
	{
		this.imgId = imgId;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getDistance()
	{
		return distance;
	}

	public void setDistance(String distance)
	{
		this.distance = distance;
	}

	public int getZanNum()
	{
		return zannum;
	}

	public void setZanNum(int zannum)
	{
		this.zannum = zannum;
	}

}
