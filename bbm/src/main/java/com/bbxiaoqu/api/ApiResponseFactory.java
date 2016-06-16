/*
 * Copyright (C) 2010 mAPPn.Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bbxiaoqu.api;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;

import com.bbxiaoqu.api.util.Utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;


/**
 * API 响应结果解析工厂类，所有的API响应结果解析需要在此完成。
 * 
 * @author dzyang
 * @date 2011-4-22
 * 
 */
@SuppressLint("NewApi")
public class ApiResponseFactory {

//    private static final String TAG = "ApiResponseFactory";

	
	
    public static byte[] read(InputStream inStream)
			throws Exception {
	
		try {
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int len = 0;
			while ((len = inStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, len);
			}
			inStream.close();
			// ProgressDialog1.dismiss();
			return outputStream.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
	
		}
		return null;
	}

	/**
     * 解析市场API响应结果
     * 
     * @param action
     *            请求API方法
     * @param response
     *            HTTP Response
     * @return 解析后的结果（如果解析错误会返回Null）
     */
	public static Object getResponse(Context context, int action,
			HttpResponse httpResponse) {
		InputStream in = null;
		if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {			
			try {
				in = httpResponse.getEntity().getContent();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}		
		String requestMethod = "";
        Object result = null;       
        switch (action) {		
		case MarketAPI.ACTION_REGISTER:
		    // 注册
		    requestMethod = "ACTION_REGISTER";
		    result = parseLoginOrRegisterResult(in);
		    break;
		case MarketAPI.ACTION_LOGIN:
		    // 登录
		    requestMethod = "ACTION_LOGIN";
		    result = parseLoginOrRegisterResult(in);
		    break;
		case MarketAPI.ACTION_GETDYNAMICS:
		    // 小区动态
		    requestMethod = "ACTION_GETDYNAMICS";
		    result = parseGetDaymicResult(in);
		    break;
		case MarketAPI.ACTION_GONGGAO:
		    // 公告
		    requestMethod = "ACTION_GONGGAO";
		    result = parseGongGaoResult(in);
		    break;
		case MarketAPI.ACTION_GETINFO:
		    // 单条信息
		    requestMethod = "ACTION_GETINFO";
		    result = parseInfoResult(in);
		    break;
		case MarketAPI.ACTION_GETITEMNUM:
		    // 单条信息的评论数
		    requestMethod = "ACTION_GETITEMNUM";
		    result = parseItemNumsResult(in);
		    break;
		case MarketAPI.ACTION_GETINFOS:
		    // 很多条消息
		    requestMethod = "ACTION_GETINFOS";
		    result = parseGetInfosResult(in);
		    break;    
		case MarketAPI.ACTION_GETFRIENDS:
		    // 很多条消息
		    requestMethod = "ACTION_GETINFOS";
		    result = parseGetFriendsResult(in);
		    break;
		case MarketAPI.ACTION_GETXIAOQUS:
				// 很多条消息
				requestMethod = "ACTION_GETXIAOQUS";
				result = parseGetXiaoqusResult(in);
				break;
		case MarketAPI.ACTION_GETFWINFOS:
				// 很多条消息
				requestMethod = "ACTION_GETFWINFOS";
				result = parseGetFwInfosResult(in);
				break;
			default:
		    break;
		}
        if (result != null) {
            Utils.D(requestMethod + "'s Response is : " + result.toString());
        } else {
            Utils.D(requestMethod + "'s Response is null");
        }
        return result;
    }

	
    private static HashMap<String, String> parseLoginOrRegisterResult(InputStream jsonStream) {
    	byte[] data = null;
		try {
			data = read(jsonStream);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		HashMap<String, String> result = new HashMap<String, String>();
		if(data!=null)
		{
			String JsonContext = new String(data);
			result.put("login", JsonContext);
		}else
		{
			result.put("login", "");
		}
        return result;
    }
    
    
    private static HashMap<String, String> parseGetDaymicResult(InputStream jsonStream) {
    	byte[] data = null;
		try {
			data = read(jsonStream);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		HashMap<String, String> result = new HashMap<String, String>(); 
		if(data!=null)
		{
			String JsonContext = new String(data);
			result.put("daymic", JsonContext);
		}else
		{
			result.put("daymic", "");			
		}
        return result;
    }
    
    
    private static HashMap<String, String> parseGongGaoResult(InputStream jsonStream) {
    	byte[] data = null;
		try {
			data = read(jsonStream);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		HashMap<String, String> result = new HashMap<String, String>();     
		String JsonContext = new String(data);
		result.put("gonggao", JsonContext);
              
        return result;
    }
    
    
    
    
    private static HashMap<String, String> parseInfoResult(InputStream jsonStream) {
    	byte[] data = null;
		try {
			data = read(jsonStream);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		HashMap<String, String> result = new HashMap<String, String>();
		if(data!=null&&data.length>0) {
			String JsonContext = new String(data);
			result.put("guidinfo", JsonContext);
		}else
		{
			result.put("guidinfo", "");
		}
        return result;
    }
    
    
    private static HashMap<String, String> parseItemNumsResult(InputStream jsonStream) {
    	byte[] data = null;
		try {
			data = read(jsonStream);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		HashMap<String, String> result = new HashMap<String, String>();     
		String JsonContext = new String(data);
		result.put("guidnums", JsonContext);
              
        return result;
    }
    
    private static HashMap<String, String> parseGetInfosResult(InputStream jsonStream) {
    	byte[] data = null;
		try {
			data = read(jsonStream);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}		
		HashMap<String, String> result = new HashMap<String, String>();     
		String JsonContext = new String(data);
		result.put("infos", JsonContext);
              
        return result;
    }
    
    
    
    private static HashMap<String, String> parseGetFriendsResult(InputStream jsonStream) {
    	byte[] data = null;
		try {
			data = read(jsonStream);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		HashMap<String, String> result = new HashMap<String, String>();     
		String JsonContext = new String(data);
		result.put("friends", JsonContext);
              
        return result;
    }


	private static HashMap<String, String> parseGetXiaoqusResult(InputStream jsonStream) {
		byte[] data = null;
		try {
			data = read(jsonStream);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		HashMap<String, String> result = new HashMap<String, String>();
		String JsonContext = new String(data);
		result.put("xiaoqus", JsonContext);

		return result;
	}

	private static HashMap<String, String> parseGetFwInfosResult(InputStream jsonStream) {
		byte[] data = null;
		try {
			data = read(jsonStream);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		HashMap<String, String> result = new HashMap<String, String>();
		String JsonContext = new String(data);
		result.put("infos", JsonContext);

		return result;
	}


	/*
     * 获取所有分类列表
     
    private static ArrayList<HashMap<String, Object>> parseAllCategory(XmlElement xmlDocument) {

        if (xmlDocument == null) {
            return null;
        }

        List<XmlElement> categorys = xmlDocument.getChildren(Constants.KEY_CATEGORY);
        ArrayList<HashMap<String, Object>> result = null;
        if (categorys != null) {
            result = new ArrayList<HashMap<String, Object>>();

            for (int i = 1; i < categorys.size(); i++) {
                XmlElement category = categorys.get(i);
                HashMap<String, Object> item = new HashMap<String, Object>();
                item.put(Constants.KEY_CATEGORY_NAME,
                        category.getAttribute(Constants.KEY_CATEGORY_NAME));
                item.put(Constants.KEY_APP_COUNT,
                        category.getAttribute(Constants.KEY_APP_COUNT));
                item.put(Constants.KEY_CATEGORY_ICON_URL,
                        category.getAttribute(Constants.KEY_CATEGORY_ICON_URL));
                
                String subCategoryText = category.getChild(Constants.KEY_SUB_CATEGORY, 0).getAttribute(
                        Constants.KEY_CATEGORY_NAME) + ", ";
                XmlElement category2 = category.getChild(Constants.KEY_SUB_CATEGORY, 1);
                if(category2 != null) {
                    subCategoryText +=  (category2.getAttribute(
                            Constants.KEY_CATEGORY_NAME) + ", ");
                }
                XmlElement category3 = category.getChild(Constants.KEY_SUB_CATEGORY, 2);
                if(category3 != null) {
                    subCategoryText += (category3.getAttribute(
                            Constants.KEY_CATEGORY_NAME) + ", ");
                }
                if (subCategoryText.length() > 0) {
                    subCategoryText = subCategoryText.substring(0, subCategoryText.length() - 2);
                }
                item.put(Constants.KEY_TOP_APP, subCategoryText);

                List<XmlElement> subCategorys = category.getChildren(Constants.KEY_SUB_CATEGORY);
                ArrayList<HashMap<String, Object>> subCategoryList = new ArrayList<HashMap<String, Object>>();
                for (XmlElement element : subCategorys) {
                    HashMap<String, Object> subCategory = new HashMap<String, Object>();
                    subCategory.put(Constants.KEY_CATEGORY_ID,
                            element.getAttribute(Constants.KEY_CATEGORY_ID));
                    subCategory.put(Constants.KEY_CATEGORY_NAME,
                            element.getAttribute(Constants.KEY_CATEGORY_NAME));
                    subCategory.put(Constants.KEY_APP_COUNT,
                            element.getAttribute(Constants.KEY_APP_COUNT));
                    subCategory.put(Constants.KEY_CATEGORY_ICON_URL,
                            element.getAttribute(Constants.KEY_CATEGORY_ICON_URL));
                    String app1 = element.getAttribute(Constants.KEY_APP_1);
                    String app2 = element.getAttribute(Constants.KEY_APP_2);
                    String app3 = element.getAttribute(Constants.KEY_APP_3);
                    String topApp = (TextUtils.isEmpty(app1) ? "" : app1 + ", ")
                            + (TextUtils.isEmpty(app2) ? "" : app2 + ", ")
                            + (TextUtils.isEmpty(app3) ? "" : app3 +  ", ");
                    if (topApp.length() > 0) {
                        topApp = topApp.substring(0, topApp.length() - 2);
                    }
                    subCategory.put(Constants.KEY_TOP_APP, topApp);
                    subCategoryList.add(subCategory);
                }
                item.put(Constants.KEY_SUB_CATEGORY, subCategoryList);
                result.add(item);
            }
            
            // 展开第一个一级列表
            XmlElement firstCategory = categorys.get(0);
            List<XmlElement> firstSubCategorys = firstCategory
                    .getChildren(Constants.KEY_SUB_CATEGORY);
            for (XmlElement element : firstSubCategorys) {
                HashMap<String, Object> item = new HashMap<String, Object>();
                item.put(Constants.KEY_CATEGORY_ID,
                            element.getAttribute(Constants.KEY_CATEGORY_ID));
                item.put(Constants.KEY_CATEGORY_NAME,
                        element.getAttribute(Constants.KEY_CATEGORY_NAME));
                item.put(Constants.KEY_APP_COUNT,
                        element.getAttribute(Constants.KEY_APP_COUNT));
                item.put(Constants.KEY_CATEGORY_ICON_URL,
                        element.getAttribute(Constants.KEY_CATEGORY_ICON_URL));
                String app1 = element.getAttribute(Constants.KEY_APP_1);
                String app2 = element.getAttribute(Constants.KEY_APP_2);
                String app3 = element.getAttribute(Constants.KEY_APP_3);
                String topApp = (TextUtils.isEmpty(app1) ? "" : app1 + ", ")
                        + (TextUtils.isEmpty(app2) ? "" : app2 + ", ")
                        + (TextUtils.isEmpty(app3) ? "" : app3 +  ", ");
                if (topApp.length() > 0) {
                    topApp = topApp.substring(0, topApp.length() - 2);
                }
                item.put(Constants.KEY_TOP_APP, topApp);
                result.add(item);
            }
        }
        return result;
    }
    
    
     * 获取产品详细信息 
     
    private static Object parseProductDetail(InputStream jsonStream) {

    	byte[] data = null;
		try {
			data = read(jsonStream);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String json = new String(data);
		JSONObject product;
		 ProductDetail result = null;
		try {
			product = new JSONObject(json);
		
		
       // XmlElement product = xmlDocument.getChild(Constants.KEY_PRODUCT, 0);
       

        if (product != null) {
            result = new ProductDetail();
            result.setPid(product.getString(Constants.KEY_PRODUCT_ID));
            result.setProductType(product.getString(Constants.KEY_PRODUCT_TYPE));
            result.setName(product.getString(Constants.KEY_PRODUCT_NAME));
            result.setPrice(Utils.getInt(product.getString(Constants.KEY_PRODUCT_PRICE)));
            result.setPayCategory(Utils.getInt(product.getString(Constants.KEY_PRODUCT_PAY_TYPE)));
            result.setRating(Utils.getInt(product.getString(Constants.KEY_PRODUCT_RATING)));
            result.setIconUrl(product.getString(Constants.KEY_PRODUCT_ICON_URL));
            result.setIconUrlLdpi(product.getString(Constants.KEY_PRODUCT_ICON_URL_LDPI));
            result.setShotDes(product.getString(Constants.KEY_PRODUCT_SHORT_DESCRIPTION));
            result.setAppSize(Utils.getInt(product.getString(Constants.KEY_PRODUCT_SIZE)));
            result.setSourceType(product.getString(Constants.KEY_PRODUCT_SOURCE_TYPE));
            result.setPackageName(product.getString(Constants.KEY_PRODUCT_PACKAGE_NAME));
            result.setVersionName(product.getString(Constants.KEY_PRODUCT_VERSION_NAME));
            result.setVersionCode(Utils.getInt(product
                    .getString(Constants.KEY_PRODUCT_VERSION_CODE)));
            result.setCommentsCount(Utils.getInt(product
                    .getString(Constants.KEY_PRODUCT_COMMENTS_COUNT)));
            result.setRatingCount(Utils.getInt(product
                    .getString(Constants.KEY_PRODUCT_RATING_COUNT)));
            result.setDownloadCount(Utils.getInt(product
                    .getString(Constants.KEY_PRODUCT_DOWNLOAD_COUNT)));
            result.setLongDescription(product.getString(Constants.KEY_PRODUCT_LONG_DESCRIPTION));
            result.setAuthorName(product.getString(Constants.KEY_PRODUCT_AUTHOR));
            result.setPublishTime(Utils.getInt(product
                    .getString(Constants.KEY_PRODUCT_PUBLISH_TIME)));
            final String[] screenShot = new String[5];
            screenShot[0] = product.getString(Constants.KEY_PRODUCT_SCREENSHOT_1);
            screenShot[1] = product.getString(Constants.KEY_PRODUCT_SCREENSHOT_2);
            screenShot[2] = product.getString(Constants.KEY_PRODUCT_SCREENSHOT_3);
            screenShot[3] = product.getString(Constants.KEY_PRODUCT_SCREENSHOT_4);
            screenShot[4] = product.getString(Constants.KEY_PRODUCT_SCREENSHOT_5);
            result.setScreenshot(screenShot);
            final String[] screenShotLdpi = new String[5];
            screenShotLdpi[0] = product.getString(Constants.KEY_PRODUCT_SCREENSHOT_LDPI_1);
            screenShotLdpi[1] = product.getString(Constants.KEY_PRODUCT_SCREENSHOT_LDPI_2);
            screenShotLdpi[2] = product.getString(Constants.KEY_PRODUCT_SCREENSHOT_LDPI_3);
            screenShotLdpi[3] = product.getString(Constants.KEY_PRODUCT_SCREENSHOT_LDPI_4);
            screenShotLdpi[4] = product.getString(Constants.KEY_PRODUCT_SCREENSHOT_LDPI_5);
            result.setScreenshotLdpi(screenShotLdpi);
            result.setUpReason(product.getString(Constants.KEY_PRODUCT_UP_REASON));
            result.setUpTime(Utils.getLong(product.getString(Constants.KEY_PRODUCT_UP_TIME)));
            result.setPermission(product.getString(Constants.KEY_PRODUCT_PERMISSIONS));
        }
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return result;
    }
    
    *//**
     * 解析同步应用
     *//*
    private static Object parseSyncApps(XmlElement xmlDocument) {

        if (xmlDocument == null) {
            return null;
        }
        UpdateInfo updateInfo = new UpdateInfo();

        updateInfo.setUpdageLevel(Integer.valueOf(xmlDocument.getChild(
                Constants.EXTRA_UPDATE_LEVEL, 0).getText()));
        updateInfo.setVersionCode(Integer.valueOf(xmlDocument.getChild(
                Constants.EXTRA_VERSION_CODE, 0).getText()));
        updateInfo.setVersionName(xmlDocument.getChild(
                Constants.EXTRA_VERSION_NAME, 0).getText());
        updateInfo.setDescription(xmlDocument.getChild(
                Constants.EXTRA_DESCRIPTION, 0).getText());
        updateInfo.setApkUrl(xmlDocument.getChild(Constants.EXTRA_URL, 0)
                .getText());

        return updateInfo;
    }

    
     * 解析我的评星结果
     
    private static Object parseMyRating(Context context,
    		InputStream jsonStream) {
     	byte[] data = null;
    		try {
    			data = read(jsonStream);
    		} catch (Exception e1) {
    			// TODO Auto-generated catch block
    			e1.printStackTrace();
    		}
    		String json = new String(data);
    		 try {
				JSONObject customJson = new JSONObject(json);
				return customJson.getString(Constants.KEY_VALUE);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			};
    		 
        XmlElement element = xmlDocument.getChild(Constants.KEY_PRODUCT_RATING, 0);
        if (element != null) {
            return element.getAttribute(Constants.KEY_VALUE);
        }
        return null;
    }

    
     * 解析评论列表
     
    private static Object parseComments(Context context,InputStream jsonStream) throws JSONException {
        
    	byte[] data = null;
		try {
			data = read(jsonStream);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String json = new String(data);
    	
    	HashMap<String, Object> result = null;
        //XmlElement comments = xmlDocument.getChild(Constants.KEY_COMMENTS, 0);
    	 JSONObject jsonobj = new JSONObject(json);
        if (jsonobj != null) {
            result = new HashMap<String, Object>();
            
            int totalSize = Utils.getInt(jsonobj.getString(Constants.KEY_TOTAL_SIZE));
            result.put(Constants.KEY_TOTAL_SIZE, totalSize);
            
            if (totalSize > 0) {
                ArrayList<HashMap<String, Object>> commentList = new ArrayList<HashMap<String, Object>>();
                JSONArray jsonarray = new JSONArray(jsonobj.getJSONArray(Constants.KEY_COMMENTS));
                //List<XmlElement> children = comments.getChildren(Constants.KEY_COMMENT);
                for (int i = 0; i < jsonarray.length(); i++) 
		    	{
                    HashMap<String, Object> commentEntity = new HashMap<String, Object>();
                    JSONObject jsoncommentEntity = jsonarray.getJSONObject(i);
                    
                    commentEntity.put(Constants.KEY_COMMENT_ID,
                    		jsoncommentEntity.getString(Constants.KEY_COMMENT_ID));
                    commentEntity.put(Constants.KEY_COMMENT_AUTHOR,
                    		jsoncommentEntity.getString(Constants.KEY_COMMENT_AUTHOR));
                    commentEntity.put(Constants.KEY_COMMENT_BODY,
                    		jsoncommentEntity.getString(Constants.KEY_COMMENT_BODY));
                    long millis = Utils.getLong(jsoncommentEntity.getString(Constants.KEY_COMMENT_DATE));
                   
                    commentEntity.put(Constants.KEY_COMMENT_DATE, Utils.formatTime(millis));
                    commentList.add(commentEntity);
                }
                result.put(Constants.KEY_COMMENT_LIST, commentList);
            }
        }
        return result;
    }

    
     * 解析注册或者登录结果
     
    private static HashMap<String, String> parseLoginOrRegisterResult(InputStream jsonStream) {
    	byte[] data = null;
		try {
			data = read(jsonStream);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		HashMap<String, String> result = new HashMap<String, String>();     
		String JsonContext = new String(data);
		JSONObject jsonobj;
		try {
			jsonobj = new JSONObject(JsonContext);			
	        result.put(Constants.KEY_USER_UID, jsonobj.getString(Constants.KEY_USER_UID));
	        result.put(Constants.KEY_USER_NAME, jsonobj.getString(Constants.KEY_USER_NAME));                
	        result.put(Constants.KEY_USER_EMAIL, jsonobj.getString(Constants.KEY_USER_EMAIL));  
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}//转换为JSONObject  
		
              
        return result;
    }
    
    
    public static String convertStreamToString(InputStream is) {   
		   BufferedReader reader = new BufferedReader(new InputStreamReader(is));   
		        StringBuilder sb = new StringBuilder();   
		        String line = null;   
		        try {   
		            while ((line = reader.readLine()) != null) {   
		                sb.append(line + "/n");   
		            }   
		        } catch (IOException e) {   
		            e.printStackTrace();   
		        } finally {   

		            try {   

		                is.close();   

		            } catch (IOException e) {   

		                e.printStackTrace();   

		            }   

		        }   

		    

		        return sb.toString();   

		    }   

    private static HashMap<String, Object> parseProductList(Context context,
    		InputStream jsonStream) {
    	////[{ldpi_icon_url=http://cdn2.image.apk.gfan.com/asdf/PImages/2013/1/18/ldpi_340317_716ca33c-0bc5-45f4-a7a6-de7b6d05e1dc_icon.png, pay_category=1, packagename=com.mikrosonic.RD3X, icon_url=http://cdn2.image.apk.gfan.com/asdf/PImages/2013/1/18/340317_716ca33c-0bc5-45f4-a7a6-de7b6d05e1dc_icon.png, is_star=false, price=免费, short_description=这是一款专业音效合成软件, app_size=4.12M, name=音效合成器, author_name=机锋网友, rating=0, product_download=0, p_id=340317, sub_category=应用 > 实用工具}, {ldpi_icon_url=http://cdn2.image.apk.gfan.com/asdf/PImages/2013/4/18/ldpi_376899_22a873780-cda7-4414-a4ae-0c4a6b13b7cb.png, pay_category=1, packagename=com.entel.app.cmcc, icon_url=http://cdn2.image.apk.gfan.com/asdf/PImages/2013/4/18/376899_22a873780-cda7-4414-a4ae-0c4a6b13b7cb.png, is_star=false, price=免费, short_description=中国移动推出的无线上网服务, app_size=6.98M, name=新鲜网事, author_name=北京宇和永泰网络科技有限公司, rating=50, product_download=0, p_id=376899, sub_category=应用 > 实用工具}, {ldpi_icon_url=http://cdn6.image.apk.gfan.com/asdf/PImages/2013/12/10/ldpi_381825_a4298dfa-ca25-46a8-bc8c-04a5eafad39d_icon.png, pay_category=1, packagename=com.runtastic.android.pro2, icon_url=http://cdn6.image.apk.gfan.com/asdf/PImages/2013/12/10/381825_a4298dfa-ca25-46a8-bc8c-04a5eafad39d_icon.png, is_star=false, price=免费, short_description=一款可使用GPS定位的跑步健身应用软件。, app_size=12.99M, name=跑步记录器, author_name=机锋网友, rating=0, product_download=0, p_id=381825, sub_category=应用 > 实用工具}, {ldpi_icon_url=http://cdn2.image.apk.gfan.com/asdf/PImages/2013/3/28/ldpi_146472_7fa27378-5ff1-44d8-ac05-f795d406ff46_icon.png, pay_category=1, packagename=uk.co.olilan.touchcalendar, icon_url=http://cdn2.image.apk.gfan.com/asdf/PImages/2013/3/28/146472_7fa27378-5ff1-44d8-ac05-f795d406ff46_icon.png, is_star=false, price=免费, short_description=它可以让您轻松地浏览您的日历。, app_size=442K, name=触摸日历 汉化版, author_name=机锋网友, rating=10, product_download=0, p_id=146472, sub_category=应用 > 事务管理}, {ldpi_icon_url=http://cdn6.image.apk.gfan.com/asdf/PImages/2013/1/18/ldpi_146943_0b690771-10a5-4211-9d75-c15ba4558264_icon.png, pay_category=1, packagename=com.triapodi.apprec, icon_url=http://cdn6.image.apk.gfan.com/asdf/PImages/2013/1/18/146943_0b690771-10a5-4211-9d75-c15ba4558264_icon.png, is_star=false, price=免费, short_description=一款应用程序推荐软件。, app_size=1.78M, name=应用程序搜索器, author_name=机锋网友, rating=0, product_download=0, p_id=146943, sub_category=应用 > 聊天社区}, {ldpi_icon_url=http://cdn2.image.apk.gfan.com/asdf/PImages/2012/2/13/ldpi_151004_21a3e98b0-6506-410b-96d4-649c36b5a550.jpg, pay_category=1, packagename=com.netqin.schedule.sc, icon_url=http://cdn2.image.apk.gfan.com/asdf/PImages/2012/2/13/151004_21a3e98b0-6506-410b-96d4-649c36b5a550.jpg, is_star=false, price=免费, short_description=帮您打点情人节的一切，专家级情人过节指南, app_size=4.44M, name=niceday, author_name=网秦天下, rating=47, product_download=0, p_id=151004, sub_category=应用 > 事务管理}, {ldpi_icon_url=http://cdn6.image.apk.gfan.com/asdf/PImages/2012/5/1/ldpi_259941_ca072563-f89b-46ea-9a4f-69af9e7782f8_icon.png, pay_category=1, packagename=com.gaogao.dzjp, icon_url=http://cdn6.image.apk.gfan.com/asdf/PImages/2012/5/1/259941_ca072563-f89b-46ea-9a4f-69af9e7782f8_icon.png, is_star=false, price=免费, short_description=街机麻将经典之作，宅男复仇精品！, app_size=4M, name=麻将-电子基盘(必胡版), author_name=, rating=25, product_download=0, p_id=259941, sub_category=游戏 > 模拟器}, {ldpi_icon_url=http://cdn6.image.apk.gfan.com/asdf/PImages/2013/3/1/ldpi_161335_71d57fc0-57c0-46fa-b05d-08d9e84e5f07_icon.png, pay_category=1, packagename=com.keros.android.kerosplanner, icon_url=http://cdn6.image.apk.gfan.com/asdf/PImages/2013/3/1/161335_71d57fc0-57c0-46fa-b05d-08d9e84e5f07_icon.png, is_star=false, price=免费, short_description=Keros Planner是事物计划表。, app_size=4.29M, name=Keros事物计划, author_name=机锋网友, rating=40, product_download=0, p_id=161335, sub_category=应用 > 事务管理}, {ldpi_icon_url=http://cdn6.image.apk.gfan.com/asdf/PImages/2011/10/12/ldpi_180521_76cd9f80-84fb-4f3b-9764-564f551b2236_icon.png, pay_category=1, packagename=com.longxk.screentranslator, icon_url=http://cdn6.image.apk.gfan.com/asdf/PImages/2011/10/12/180521_76cd9f80-84fb-4f3b-9764-564f551b2236_icon.png, is_star=false, price=免费, short_description=一款查找屏幕上英文单词释义的应用, app_size=1.71M, name=屏幕词典, author_name=Loong, rating=40, product_download=0, p_id=180521, sub_category=应用 > 词典翻译}, {ldpi_icon_url=http://cdn6.image.apk.gfan.com/asdf/PImages/2011/5/ldpi_12926e08014b1-f52b-4410-b68f-6185ab6cb9f8_icon.png, pay_category=1, packagename=com.byread.reader, icon_url=http://cdn6.image.apk.gfan.com/asdf/PImages/2011/5/12926e08014b1-f52b-4410-b68f-6185ab6cb9f8_icon.png, is_star=false, price=免费, short_description=最具人气手机阅读互动社区, app_size=6.05M, name=百阅, author_name=南京掌门科技有限公司, rating=40, product_download=0, p_id=12926, sub_category=应用 > 教育阅读}, {ldpi_icon_url=http://cdn6.image.apk.gfan.com/asdf/PImages/2012/5/14/ldpi_265047_77833d90-04bc-4964-a561-5c142e205bc7_icon.png, pay_category=1, packagename=com.text, icon_url=http://cdn6.image.apk.gfan.com/asdf/PImages/2012/5/14/265047_77833d90-04bc-4964-a561-5c142e205bc7_icon.png, is_star=false, price=免费, short_description=计步器 最准 健康 益步, app_size=1.97M, name=云端漫步, author_name=万象天龙科技发展（北京）有限公司, rating=46, product_download=0, p_id=265047, sub_category=应用 > 健康医疗}, {ldpi_icon_url=http://cdn2.image.apk.gfan.com/asdf/PImages/2012/11/6/ldpi_2820_c754de73-f776-44eb-bc2f-37a5db162d10_icon.png, pay_category=1, packagename=com.applauses, icon_url=http://cdn2.image.apk.gfan.com/asdf/PImages/2012/11/6/2820_c754de73-f776-44eb-bc2f-37a5db162d10_icon.png, is_star=false, price=免费, short_description=包含了8种不一样的掌声的一个小应用。, app_size=862K, name=鼓掌艺术, author_name=机锋网友, rating=25, product_download=0, p_id=2820, sub_category=应用 > 生活娱乐}, {ldpi_icon_url=http://cdn2.image.apk.gfan.com/asdf/PImages/2012/11/6/ldpi_4758_82f8a4ef-e569-46e6-a467-0de7cabd81c9_icon.png, pay_category=1, packagename=net.qtwn.android.camera, icon_url=http://cdn2.image.apk.gfan.com/asdf/PImages/2012/11/6/4758_82f8a4ef-e569-46e6-a467-0de7cabd81c9_icon.png, is_star=false, price=免费, short_description=让你在安静中方便。, app_size=64K, name=无声摄像机, author_name=机锋网友, rating=23, product_download=0, p_id=4758, sub_category=应用 > 拍照摄影}, {ldpi_icon_url=http://cdn6.image.apk.gfan.com/asdf/PImages/2012/10/24/ldpi_118340_5b4418b8-e15e-4ef4-8a90-6c44056f78da_icon.png, pay_category=1, packagename=com.baoyitj, icon_url=http://cdn6.image.apk.gfan.com/asdf/PImages/2012/10/24/118340_5b4418b8-e15e-4ef4-8a90-6c44056f78da_icon.png, is_star=false, price=免费, short_description=学习太极的一款包含视频，文字讲解的小软件。, app_size=783K, name=内家拳之太极, author_name=机锋网友, rating=40, product_download=0, p_id=118340, sub_category=应用 > 生活娱乐}, {ldpi_icon_url=http://cdn2.image.apk.gfan.com/asdf/PImages/2012/10/23/ldpi_149708_047910d5-7171-47ed-8bef-cab60e0b55bf_icon.png, pay_category=1, packagename=jp.co.haibis.android.angelcamera, icon_url=http://cdn2.image.apk.gfan.com/asdf/PImages/2012/10/23/149708_047910d5-7171-47ed-8bef-cab60e0b55bf_icon.png, is_star=false, price=免费, short_description=Angel Camera有简单，酷的拍照界面。, app_size=3.43M, name=天使相机, author_name=机锋网友, rating=0, product_download=0, p_id=149708, sub_category=应用 > 拍照摄影}, {ldpi_icon_url=http://cdn2.image.apk.gfan.com/asdf/PImages/2012/10/31/ldpi_153158_1ca74e04-a250-4edc-a866-d78cff2a60c5_icon.png, pay_category=1, packagename=be.hcpl.android.phototools, icon_url=http://cdn2.image.apk.gfan.com/asdf/PImages/2012/10/31/153158_1ca74e04-a250-4edc-a866-d78cff2a60c5_icon.png, is_star=false, price=免费, short_description=这是一个免费的摄影工具收藏软件。, app_size=855K, name=摄影工具, author_name=机锋网友, rating=40, product_download=0, p_id=153158, sub_category=应用 > 拍照摄影}, {ldpi_icon_url=http://cdn2.image.apk.gfan.com/asdf/PImages/2013/1/24/ldpi_292420_1598fc82-71e6-48c1-9e24-5bcada0f14b3_icon.png, pay_category=1, packagename=com.appbyme.app915, icon_url=http://cdn2.image.apk.gfan.com/asdf/PImages/2013/1/24/292420_1598fc82-71e6-48c1-9e24-5bcada0f14b3_icon.png, is_star=false, price=免费, short_description=一款言言语语图库类应用, app_size=2.77M, name=精典语录珍藏版, author_name=百团网, rating=0, product_download=0, p_id=292420, sub_category=应用 > 教育阅读}, {ldpi_icon_url=http://cdn2.image.apk.gfan.com/asdf/PImages/2011/12/16/ldpi_186402_2ddd44256-62da-4d58-878f-ced93d437024.png, pay_category=1, packagename=com.tuangoudaren, icon_url=http://cdn2.image.apk.gfan.com/asdf/PImages/2011/12/16/186402_2ddd44256-62da-4d58-878f-ced93d437024.png, is_star=false, price=免费, short_description=精品团购大全 团购导航, app_size=4.55M, name=团购达人(手机买团购）, author_name=北京趣游指尖科技有限公司, rating=47, product_download=0, p_id=186402, sub_category=应用 > 网络购物}, {ldpi_icon_url=http://cdn6.image.apk.gfan.com/asdf/PImages/2013/11/19/ldpi_330139_2efe2fbcc-7ce4-42cc-860f-934117e6d264.png, pay_category=1, packagename=com.ethersoft.ebook1, icon_url=http://cdn6.image.apk.gfan.com/asdf/PImages/2013/11/19/330139_2efe2fbcc-7ce4-42cc-860f-934117e6d264.png, is_star=false, price=免费, short_description=在经典故事里点触汉字来识字，帮助儿童学习和书写汉字，改善阅读, app_size=47.47M, name=点字书／一句话小故事, author_name=以太 软件, rating=0, product_download=0, p_id=330139, sub_category=应用 > 教育阅读}, {ldpi_icon_url=http://cdn2.image.apk.gfan.com/asdf/PImages/2011/9/23/ldpi_170712_23534c1b6-0513-43ca-a8d4-e803d1df93bf.png, pay_category=1, packagename=com.sinosoft.jddxylk, icon_url=http://cdn2.image.apk.gfan.com/asdf/PImages/2011/9/23/170712_23534c1b6-0513-43ca-a8d4-e803d1df93bf.png, is_star=false, price=免费, short_description=短信一箩筐是一款经典的短信软件！收集了大量经典短信，你浏览的时候可以分享到微博，也可直接发送。, app_size=1.08M, name=短信一箩筐, author_name=, rating=45, product_download=0, p_id=170712, sub_category=应用 > 生活娱乐}, {ldpi_icon_url=http://cdn2.image.apk.gfan.com/asdf/PImages/2012/11/6/ldpi_3394_c4460d49-a322-4939-a1d3-ab40d97246f6_icon.png, pay_category=1, packagename=com.redirectin.android.ime, icon_url=http://cdn2.image.apk.gfan.com/asdf/PImages/2012/11/6/3394_c4460d49-a322-4939-a1d3-ab40d97246f6_icon.png, is_star=false, price=免费, short_description=最新版本的五笔输入法，用的更顺手。, app_size=992K, name=五笔输入法, author_name=机锋网友, rating=39, product_download=0, p_id=3394, sub_category=应用 > 输入法}, {ldpi_icon_url=http://cdn6.image.apk.gfan.com/asdf/PImages/2011/11/21/ldpi_194341_442...
    
    	
    	byte[] data = null;
		try {
			data = read(jsonStream);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String json = new String(data);
			HashMap<String, Object> result = null;
	    	result = new HashMap<String, Object>();
	    	ArrayList<HashMap<String, Object>> productArray = null;
	    	productArray = new ArrayList<HashMap<String, Object>>();

			if(json.length()>0)
			{
				JSONArray jsonarray = null;
				try {
					jsonarray = new JSONArray(json);
					for (int i = 0; i < jsonarray.length(); i++) 
			    	{
			    		 HashMap<String, Object> item = new HashMap<String, Object>();
			    		 JSONObject customJson = jsonarray.getJSONObject(i);
			    		 item.put(Constants.KEY_PRODUCT_ID, "340317");	                
			             item.put(Constants.KEY_PRODUCT_PACKAGE_NAME, "com.mikrosonic.RD3X");
			             item.put(Constants.KEY_PRODUCT_PRICE, "免费");
			             item.put(Constants.KEY_PRODUCT_IS_STAR, false);
			             item.put(Constants.KEY_PRODUCT_DOWNLOAD, Constants.STATUS_INSTALLED);
			             item.put(Constants.KEY_PRODUCT_NAME,customJson.getString("name"));
			             item.put(Constants.KEY_PRODUCT_AUTHOR,"机锋网友");
			             item.put(Constants.KEY_PRODUCT_SUB_CATEGORY,"应用  >  实用工具");	               
			             item.put(Constants.KEY_PRODUCT_PAY_TYPE,1);
			             item.put(Constants.KEY_PRODUCT_RATING,0);
			             item.put(Constants.KEY_PRODUCT_SIZE,StringUtils.formatSize(4012000));
			             item.put(Constants.KEY_PRODUCT_ICON_URL,"http://cdn2.image.apk.gfan.com/asdf/PImages/2013/1/18/340317_716ca33c-0bc5-45f4-a7a6-de7b6d05e1dc_icon.png");
			             item.put(Constants.KEY_PRODUCT_ICON_URL_LDPI,"http://cdn2.image.apk.gfan.com/asdf/PImages/2013/1/18/ldpi_340317_716ca33c-0bc5-45f4-a7a6-de7b6d05e1dc_icon.png");
		                 item.put(Constants.KEY_PRODUCT_SHORT_DESCRIPTION,"这是一款专业音效合成软件");
		                 String source = element.getAttribute(Constants.KEY_PRODUCT_SOURCE_TYPE);
		                 if (Constants.SOURCE_TYPE_GOOGLE.equals(source)) {
		                     item.put(Constants.KEY_PRODUCT_SOURCE_TYPE,
		                             context.getString(R.string.leble_google));
		                 }
			             productArray.add(item);
			    	}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    	
		    	result.put(Constants.KEY_PRODUCT_LIST, productArray);
			}
	    	return result;    	
    	
		
    }
    
  
    
     * 检查可更新产品列表
     
    private static String parseUpgrade(Context context, XmlElement xmlDocument) {

        if (xmlDocument == null) {
            return "";
        }

        XmlElement products = xmlDocument.getChild(Constants.KEY_PRODUCTS, 0);
        String count = "";
        if (products != null) {
            List<XmlElement> productList = products.getChildren(Constants.KEY_PRODUCT);
            if (productList == null) {
                // 没有可更新的应用
                return count;
            }
            ArrayList<UpgradeInfo> list = new ArrayList<UpgradeInfo>();
            for (XmlElement element : productList) {
                UpgradeInfo info = new UpgradeInfo();
                info.pid = element.getAttribute(Constants.KEY_PRODUCT_ID);
                info.pkgName = element.getAttribute(Constants.KEY_PRODUCT_PACKAGE_NAME);
                info.versionName = element.getAttribute(Constants.KEY_PRODUCT_VERSION_NAME);
                info.versionCode = Utils.getInt(element
                        .getAttribute(Constants.KEY_PRODUCT_VERSION_CODE));
                info.update = 0;
                list.add(info);
            }
            count = String.valueOf(DBUtils.addUpdateProduct(context, list));
        }
        return count;
    }
    
    
     * 解析专题列表
     
    private static ArrayList<HashMap<String, Object>> parseTopicList(Context context,
            XmlElement xmlDocument) {

        if (xmlDocument == null) {
            return null;
        }

        XmlElement topics = xmlDocument.getChild(Constants.KEY_TOPICS, 0);
        ArrayList<HashMap<String, Object>> topicArray = null;
        if (topics != null) {
            final String MUST_HAVE_ID = "5";
            List<XmlElement> topicList = topics.getChildren(Constants.KEY_TOPIC);
            topicArray = new ArrayList<HashMap<String, Object>>();
            for (XmlElement element : topicList) {
                HashMap<String, Object> item = new HashMap<String, Object>();
                    
                String id = element.getAttribute(Constants.KEY_ID);
                if (MUST_HAVE_ID.equals(id)) {
                    // 装机必备不需要在这个列表中展示
                    continue;
                }
                item.put(Constants.KEY_ID, id);
                item.put(Constants.KEY_CATEGORY_NAME,
                        element.getAttribute(Constants.KEY_TOPIC_NAME));
                item.put(Constants.KEY_CATEGORY_ICON_URL,
                        element.getAttribute(Constants.KEY_TOPIC_ICON_LDPI));

                String app1 = element.getAttribute(Constants.KEY_APP_1);
                String app2 = element.getAttribute(Constants.KEY_APP_2);
                String app3 = element.getAttribute(Constants.KEY_APP_3);
                String description = app1 + ", ";
                if (!TextUtils.isEmpty(app2)) {
                    description += (app2 + ", ");
                }
                if (!TextUtils.isEmpty(app3)) {
                    description += (app3 + ", ");
                }
                if (description.length() > 1) {
                    description = description.substring(0, description.lastIndexOf(",") - 2);
                }
                item.put(Constants.KEY_TOP_APP, description);
                item.put(Constants.KEY_APP_COUNT, element.getAttribute(Constants.KEY_APP_COUNT));
                topicArray.add(item);
            }
        }
        return topicArray;
    }

    
     * 解析首页顶部推荐项列表
     
    private static ArrayList<HashMap<String, Object>> parseTopRecommend(XmlElement xmlDocument) {

        if (xmlDocument == null) {
            return null;
        }

        List<XmlElement> recommends = xmlDocument.getAllChildren();
        ArrayList<HashMap<String, Object>> recommendList = null;
        if (recommends != null) {
            recommendList = new ArrayList<HashMap<String, Object>>();
            for (XmlElement element : recommends) {
                HashMap<String, Object> item = new HashMap<String, Object>();
                if (Constants.KEY_CATEGORY.equals(element.getName())) {
                    item.put(Constants.KEY_RECOMMEND_TYPE, Constants.KEY_CATEGORY);
                } else if (Constants.KEY_TOPIC.equals(element.getName())) {
                    item.put(Constants.KEY_RECOMMEND_TYPE, Constants.KEY_TOPIC);
                } else if (Constants.KEY_PRODUCT.equals(element.getName())) {
                    item.put(Constants.KEY_RECOMMEND_TYPE, Constants.KEY_PRODUCT);
                } else {
                    item.put(Constants.KEY_RECOMMEND_TYPE, -1);
                }
                item.put(Constants.KEY_ID,
                        element.getAttribute(Constants.KEY_ID));
                item.put(Constants.KEY_RECOMMEND_ICON,
                        element.getAttribute(Constants.KEY_RECOMMEND_ICON));
                item.put(Constants.KEY_RECOMMEND_TITLE,
                        element.getAttribute(Constants.KEY_RECOMMEND_TITLE));
                recommendList.add(item);
            }
        }
        return recommendList;
    }
    
    
    *//**
     * 检查是否有新版本
     *//*
    private static Object parseCheckNewVersion(InputStream jsonStream) {
       
    	byte[] data = null;
		try {
			data = read(jsonStream);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String json = new String(data);
		
        int level = Utils.getInt(xmlDocument.getChild(Constants.EXTRA_UPDATE_LEVEL, 0).getText());

        if (level == 0) {
            File root = new File(Environment.getExternalStorageDirectory(),
                    Constants.IMAGE_CACHE_DIR);
            root.mkdirs();
            File output = new File(root, "aMarket.apk");
            output.delete();
            return null;
        }
        
        UpdateInfo updateInfo = new UpdateInfo();
        //updateInfo.setUpdageLevel(level);
        try {
        	JSONObject customJson = new JSONObject(json);
			updateInfo.setVersionCode(Utils.getInt(customJson.getString(Constants.EXTRA_VERSION_CODE)));
			updateInfo.setVersionName(customJson.getString(Constants.EXTRA_VERSION_NAME));
	        updateInfo.setDescription(customJson.getString(Constants.EXTRA_DESCRIPTION));
	        updateInfo.setApkUrl(customJson.getString(Constants.EXTRA_URL));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        return updateInfo;
    }
    
    
     * 获取产品下载信息 
     
    private static DownloadItem parseDownloadInfo(
    		InputStream jsonStream) {

     		byte[] data = null;
    		try {
    			data = read(jsonStream);
    		} catch (Exception e1) {
    			// TODO Auto-generated catch block
    			e1.printStackTrace();
    		}
    		String json = new String(data);
    		DownloadItem item = null;
    		try {
				JSONObject customJson = new JSONObject(json);
				JSONObject downloadInfo = new JSONObject(Constants.KEY_DOWNLOAD_INFO); 
	    		 
	    		 
		        
		      
		            item = new DownloadItem();
		            item.pId = downloadInfo.getString(Constants.KEY_PRODUCT_ID);
		            item.packageName = downloadInfo.getString(Constants.KEY_PRODUCT_PACKAGE_NAME);
		            item.url = downloadInfo.getString(Constants.KEY_PRODUCT_DOWNLOAD_URI);
		            item.fileMD5 = downloadInfo.getString(Constants.KEY_PRODUCT_MD5);
		        
		       
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		 return item;
    }

    
     * 解析搜索热词列表
     
    private static ArrayList<String> parseSearchKeywords(XmlElement xmlDocument) {

        if (xmlDocument == null) {
            return null;
        }

        XmlElement keyList = xmlDocument.getChild(Constants.KEY_KEYLIST, 0);
        ArrayList<String> keywords = null;
        if (keyList != null) {
            keywords = new ArrayList<String>();
            List<XmlElement> keys = keyList.getAllChildren();
            for (XmlElement key : keys) {
                keywords.add(key.getAttribute(Constants.KEY_TEXT));
            }
        }
        return keywords;
    }
    
  
    
    
     * 获取装机必备列表
     
    private static Object parseGetRequired(Context context, XmlElement xmlDocument) {

        if (xmlDocument == null) {
            return null;
        }

        ArrayList<HashMap<String, Object>> result = null;
        List<XmlElement> productGroup = 
                xmlDocument.getChildren(Constants.KEY_REQUIRED_CATEGORY);
        if (productGroup != null && productGroup.size() > 0) {

            result = new ArrayList<HashMap<String, Object>>();
            
            // 获取已经安装的应用列表
            Session session = Session.get(context);
            ArrayList<String> installedApps = session.getInstalledApps();
            
            for (XmlElement group : productGroup) {

                // 分组信息
                HashMap<String, Object> groupItem = new HashMap<String, Object>();
                groupItem.put(Constants.INSTALL_PLACE_HOLDER, true);
                groupItem.put(Constants.INSTALL_APP_TITLE,
                        group.getAttribute(Constants.KEY_PRODUCT_NAME));
                List<XmlElement> productList = group.getChildren(Constants.KEY_PRODUCT);
                result.add(groupItem);

                if (productList == null || productList.size() == 0) {
                    continue;
                }

                // 分组下的产品列表信息
                for (XmlElement product : productList) {
                    HashMap<String, Object> productItem = new HashMap<String, Object>();
                    productItem.put(Constants.INSTALL_PLACE_HOLDER, false);
                    productItem.put(Constants.KEY_PRODUCT_ID,
                            product.getAttribute(Constants.KEY_PRODUCT_ID));
                    productItem.put(Constants.INSTALL_APP_LOGO,
                            product.getAttribute(Constants.KEY_PRODUCT_ICON_URL));
                    productItem.put(Constants.INSTALL_APP_TITLE,
                            product.getAttribute(Constants.KEY_PRODUCT_NAME));
                    productItem.put(Constants.INSTALL_APP_DESCRIPTION,
                            product.getAttribute(Constants.KEY_PRODUCT_SHORT_DESCRIPTION));
                    String packageName = product.getAttribute(Constants.KEY_PRODUCT_PACKAGE_NAME);
                    if (installedApps.contains(packageName)) {
                        productItem.put(Constants.KEY_PRODUCT_IS_INSTALLED, true);
                    } else {
                        productItem.put(Constants.INSTALL_APP_IS_CHECKED, false);    
                    }
                    result.add(productItem);
                }
            }
        }
        return result;
    }
    */
   
    
   
   
    
}