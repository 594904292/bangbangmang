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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.bbxiaoqu.api.ApiAsyncTask.ApiRequestListener;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;



/**
 * GfanMobile aMarket API utility class
 * 
 * @author dzyang
 * @date 2010-10-29
 * @since Version 0.4.0
 */
public class MarketAPI {

    /** 机锋市场API host地址 */
    public static final String API_BASE_URL = 
        // real host
      "http://api.bbxiaoqu.com/";
        // test host

    
    // User Center URL HOST
    //public static final String API_HOST_CLOUD = "http://passport.gfan.com/gfan_center/";
    //public static final String API_HOST_CLOUD = "http://api.bbxiaoqu.com/mark/";
    
     // 机锋市场 API URLS
    static final String[] API_URLS = {
            // ACTION_LOGIN
            API_BASE_URL + "login.php",
            // ACTION_REGISTER
            API_BASE_URL + "register",
            // ACTION_GETDYNAMICS
            API_BASE_URL + "getdynamics.php",
            // ACTION_GONGGAO
            API_BASE_URL + "gonggao.php",
            // ACTION_GETINFO
            API_BASE_URL + "getinfo.php",
            // ACTION_GETITEMNUM
            API_BASE_URL + "getitemnum.php",
            // ACTION_GETINFOS
            API_BASE_URL + "getinfos.php",
            // ACTION_GETINFOS
            API_BASE_URL + "getfriends.php",

             API_BASE_URL + "getxiaoqu.php",
             // ACTION_GETFWINFOS
             API_BASE_URL + "getfwinfos.php"
                       
           };
    
    /** 登录 */
    public static final int ACTION_LOGIN = 0;
    /** 注册 */
    public static final int ACTION_REGISTER = 1;
    /** 小区动态 */
    public static final int ACTION_GETDYNAMICS = 2;    
    /**系统公告 */
    public static final int ACTION_GONGGAO = 3;    
    /**获取信息 */
    public static final int ACTION_GETINFO = 4;
    /**获取信息统计数 */
    public static final int ACTION_GETITEMNUM = 5;
    /**获取信息 */
    public static final int ACTION_GETINFOS = 6;
    /**获取朋友 */
    public static final int ACTION_GETFRIENDS =7;
    /**获取小区 */
    public static final int ACTION_GETXIAOQUS =8;
    /**获取服务 */
    public static final int ACTION_GETFWINFOS = 9;
    
    /**
	 * Register API<br>
	 * Do the register process, UserName, Password, Email must be provided.<br>
	 */
	public static void register(Context context, ApiRequestListener handler,
			String userid, String password, String email) {

		final HashMap<String, Object> params = new HashMap<String, Object>(3);

		params.put("username", userid);
		params.put("password", password);
		params.put("email", email);

		new ApiAsyncTask(context, ACTION_REGISTER, handler, params).execute();
	}
	
	/**
    * Login API<br>
    * Do the login process, UserName, Password must be provided.<br>
    */
   public static void login(Context context, ApiRequestListener handler,
           String username, String password) {

       final HashMap<String, Object> params = new HashMap<String, Object>(2);
       params.put("_userid", username);
       params.put("password", password);

       new ApiAsyncTask(context,ACTION_LOGIN, handler, params).execute();
   }
    
   
	/**
    * dynamics API<br>
    * Do the login process, UserName, Password must be provided.<br>
    */
   public static void dynamics(Context context, ApiRequestListener handler,
           String userid, String rang,String start,String limit) {
       final HashMap<String, Object> params = new HashMap<String, Object>(2);
       params.put("userid", userid);
       params.put("rang", rang);
       params.put("start", start);
       params.put("limit", limit);
       new ApiAsyncTask(context,ACTION_GETDYNAMICS, handler, params).execute();
   }
   
   
   
	/**
    * gonggao API<br>
    * Do the login process, UserName, Password must be provided.<br>
    */
   public static void gonggao(Context context, ApiRequestListener handler) {
       final HashMap<String, Object> params = new HashMap<String, Object>(2);      
       new ApiAsyncTask(context,ACTION_GONGGAO, handler, params).execute();
   }
   
	/**
    * getinfo API<br>
    * Do the login process, UserName, Password must be provided.<br>
    */
   public static void getinfo(Context context, ApiRequestListener handler,String guid) {
       final HashMap<String, Object> params = new HashMap<String, Object>(2);     
       params.put("guid", guid);
       new ApiAsyncTask(context,ACTION_GETINFO, handler, params).execute();      
   }
   
   
	/**
    * dynamics API<br>
    * Do the login process, UserName, Password must be provided.<br>
    */
   public static void getItemNum(Context context, ApiRequestListener handler,String guid) {
       final HashMap<String, Object> params = new HashMap<String, Object>(2);     
       params.put("_guid", guid);
       new ApiAsyncTask(context,ACTION_GETITEMNUM, handler, params).execute();
   }
   
   
	/**
    * getinfos API<br>
    * Do the login process, UserName, Password must be provided.<br>
    */
   public static void getINfos(Context context, ApiRequestListener handler,String userid,String latitude,String longitude,String rang,String status,int start,int limit) {
       final HashMap<String, Object> params = new HashMap<String, Object>(2); 
      
       params.put("_userid", userid);
       params.put("latitude", latitude);
       params.put("longitude", longitude);
       params.put("_rang", rang);
       params.put("_status", status);
       params.put("_start", String.valueOf(start));
       params.put("_limit", String.valueOf(limit));
       
       new ApiAsyncTask(context,ACTION_GETINFOS, handler, params).execute();
       
   
   }
   
   public static void getFriends(Context context, ApiRequestListener handler,String userid) {
       final HashMap<String, Object> params = new HashMap<String, Object>(2); 
      
       params.put("_userid", userid);
       
       new ApiAsyncTask(context,ACTION_GETFRIENDS, handler, params).execute();
       
   
   }


    public static void geXiaoqus(Context context, ApiRequestListener handler,String latitude,String longitude,String keyword) {
        final HashMap<String, Object> params = new HashMap<String, Object>(2);
        params.put("latitude", latitude);
        params.put("longitude", longitude);
        if(keyword!=null&&keyword.length()>0) {
            params.put("keyword", keyword);
        }else
        {
            params.put("keyword", "");
        }
        new ApiAsyncTask(context,ACTION_GETXIAOQUS, handler, params).execute();


    }


    /**
     * getinfos API<br>
     * Do the login process, UserName, Password must be provided.<br>
     */
    public static void getFwINfos(Context context, ApiRequestListener handler,String userid,String latitude,String longitude,String rang,String status,int start,int limit) {
        final HashMap<String, Object> params = new HashMap<String, Object>(2);

        params.put("_userid", userid);
        params.put("latitude", latitude);
        params.put("longitude", longitude);
        params.put("_rang", rang);
        params.put("_status", status);
        params.put("_start", String.valueOf(start));
        params.put("_limit", String.valueOf(limit));

        new ApiAsyncTask(context,ACTION_GETFWINFOS, handler, params).execute();


    }
   /* *//**
     * Get Search Keywords API<br>
     * Default size is 10.
     *//*
    public static void getSearchKeywords(Context context, ApiRequestListener handler) {

        final HashMap<String, Object> params = new HashMap<String, Object>(1);

        params.put("size", 15);

        new ApiAsyncTask(context, 
                ACTION_GET_SEARCH_KEYWORDS, handler, params).execute();
    }
    
  
   
    
    *//**
     * Get Home Page Top Recommends API<br>
     * Default size is 10.
     *//*
    public static void getTopRecommend(Context context, ApiRequestListener handler) {

        final HashMap<String, Object> params = new HashMap<String, Object>(3);

        Session session = Session.get(context);
        
        params.put("platform", session.getOsVersion());
        params.put("screen_size", session.getScreenSize());
        params.put("match_type", session.isFilterApps());

        new ApiAsyncTask(context, 
                ACTION_GET_TOP_RECOMMEND, handler, params).execute();
    }
    
    *//**
     * Get All Category API<br>
     *//*
    public static void getAllCategory(Context context, ApiRequestListener handler) {

        final HashMap<String, Object> params = new HashMap<String, Object>(3);

        Session session = Session.get(context);
        
        params.put("platform", session.getOsVersion());
        params.put("screen_size", session.getScreenSize());
        params.put("match_type", session.isFilterApps());

        new ApiAsyncTask(context, 
                ACTION_GET_ALL_CATEGORY, handler, params).execute();
    }
    
	*//**
	 * Register API<br>
	 * Do the register process, UserName, Password, Email must be provided.<br>
	 *//*
	public static void register(Context context, ApiRequestListener handler,
			String username, String password, String email) {

		final HashMap<String, Object> params = new HashMap<String, Object>(3);

		params.put("username", username);
		params.put("password", password);
		params.put("email", email);

		new ApiAsyncTask(context, ACTION_REGISTER, handler, params).execute();
	}
	
	   *//**
     * Login API<br>
     * Do the login process, UserName, Password must be provided.<br>
     *//*
    public static void login(Context context, ApiRequestListener handler,
            String username, String password) {

        final HashMap<String, Object> params = new HashMap<String, Object>(2);

        params.put("username", username);
        params.put("password", password);

        new ApiAsyncTask(context, 
                ACTION_LOGIN, handler, params).execute();
    }

	*//**
	 * Get Home Recommend API<br>
	 * 首页推荐列表（包含编辑推荐部分和算法生成部分）<br>
	 *//*
	public static void getHomeRecommend(Context context,
			ApiRequestListener handler, int startPosition, int size) {

		Session session = Session.get(context);

		final HashMap<String, Object> params = new HashMap<String, Object>(5);
		params.put("size", size);
		params.put("start_position", startPosition);
		params.put("platform", session.getOsVersion());
		params.put("screen_size", session.getScreenSize());
		params.put("match_type", session.isFilterApps());

		new ApiAsyncTask(context,
				ACTION_GET_HOME_RECOMMEND, handler, params).execute();
	}
	
	*//**
     * Get Rank By Category API<br>
     * 首页排行列表<br>
     *//*
    public static void getRankByCategory(Context context,
            ApiRequestListener handler, int startPosition, int size, String category) {

        Session session = Session.get(context);

        final HashMap<String, Object> params = new HashMap<String, Object>(6);
        params.put("size", size);
        params.put("start_position", startPosition);
        params.put("category", category);
        params.put("platform", session.getOsVersion());
        params.put("screen_size", session.getScreenSize());
        params.put("match_type", session.isFilterApps());

        new ApiAsyncTask(context,
                ACTION_GET_RANK_BY_CATEGORY, handler, params).execute();
    }
    
   

	*//**
	 * Unbind user account with cloud authority
	 *//*
	public static void unbindAccount(Context context, ApiRequestListener handler) {

		new ApiAsyncTask(context, ACTION_UNBIND, handler, null).execute();
	}

	*//**
	 * 获取专题推荐商品列表
	 *//*
	public static void getRecommendProducts(Context context,
			ApiRequestListener handler, String type, int size, int startPosition) {

		Session mSession = Session.get(context);

		final HashMap<String, Object> params = new HashMap<String, Object>(6);
		params.put("type", type);
		params.put("size", size);
		params.put("start_position", startPosition);
		params.put("platform", mSession.getOsVersion());
		params.put("screen_size", mSession.getScreenSize());
		params.put("match_type", mSession.isFilterApps());

		new ApiAsyncTask(context, ACTION_GET_RECOMMEND_PRODUCTS, handler,
				params).execute();
	}

	*//**
	 * 获取应用分类列表
	 *//*
	public static void getCategory(Context context, ApiRequestListener handler,
			String categoryCode) {

		Session mSession = Session.get(context);

		final HashMap<String, Object> params = new HashMap<String, Object>(5);
		params.put("local_version", -1);
		params.put("category_cord", categoryCode);
		params.put("platform", mSession.getOsVersion());
		params.put("screen_size", mSession.getScreenSize());
		params.put("match_type", mSession.isFilterApps());

		new ApiAsyncTask(context, ACTION_GET_CATEGORY, handler, params)
				.execute();
	}

	*//**
	 * 获取应用列表
	 *//*
	public static void getProducts(Context context, ApiRequestListener handler,
			int size, int startPosition, int orderBy, String categoryId) {

		Session mSession = Session.get(context);

		final HashMap<String, Object> params = new HashMap<String, Object>(6);
		params.put("size", size);
		params.put("start_position", startPosition);
		params.put("platform", mSession.getOsVersion());
		params.put("screen_size", mSession.getScreenSize());
		params.put("orderby", orderBy);
		params.put("category_id", categoryId);
		params.put("match_type", mSession.isFilterApps());

		new ApiAsyncTask(context, ACTION_GET_PRODUCTS, handler, params)
				.execute();
	}

	*//**
	 * 获取评论
	 *//*
	public static void getComments(Context context, ApiRequestListener handler,
			String pId, int size, int startPosition) {

		final HashMap<String, Object> params = new HashMap<String, Object>(3);
		params.put("p_id", pId);
		params.put("size", size);
		params.put("start_position", startPosition);

		new ApiAsyncTask(context, ACTION_GET_COMMENTS, handler, params)
				.execute();
	}

	

	*//**
	 * 添加评论
	 *//*
    public static void addComment(Context context, ApiRequestListener handler, String pId,
            String comment) {

        Session mSession = Session.get(context);

        String passwordEnc = SecurityUtil.encryptPassword(mSession.getPassword(),
                mSession.getUserName());
        String verifyCodeEnc = Utils.getUTF8String(Base64.encodeBase64(DigestUtils.md5(String
                .valueOf(mSession.getUserName()) + String.valueOf(pId) + passwordEnc)));

        final HashMap<String, Object> params = new HashMap<String, Object>(3);
        params.put("p_id", pId);
        params.put("uid", mSession.getUid());
        params.put("comment", comment);
        params.put("username", mSession.getUserName());
        params.put("password", passwordEnc);
        params.put("verify_code", verifyCodeEnc);

        new ApiAsyncTask(context, ACTION_ADD_COMMENT, handler, params).execute();
    }

	*//**
	 * 添加评级
	 *//*
    public static void addRating(Context context, ApiRequestListener handler, 
            String pId, int rating) {

        Session mSession = Session.get(context);

        String passwordEnc = SecurityUtil.encryptPassword(mSession.getPassword(),
                mSession.getUserName());
        String verifyCodeEnc = Utils.getUTF8String(Base64.encodeBase64(
                DigestUtils.md5(String.valueOf(mSession.getUserName()) 
                        + String.valueOf(pId) + passwordEnc)));
        final HashMap<String, Object> params = new HashMap<String, Object>(6);
        params.put("p_id", pId);
        params.put("uid", mSession.getUid());
        params.put("rating", rating);
        params.put("username", mSession.getUserName());
        params.put("password", passwordEnc);
        params.put("verify_code", verifyCodeEnc);

        new ApiAsyncTask(context, ACTION_ADD_RATING, handler, params).execute();
    }

	*//**
	 * 购买商品
	 *//*
	public static void purchaseProduct(Context context,
			ApiRequestListener handler, String pId, String password) {

		Session mSession = Session.get(context);

		String passwordEnc = SecurityUtil.encryptPassword(password,
				mSession.getUserName());
        String verifyCodeEnc = Utils.getUTF8String(Base64.encodeBase64(DigestUtils.md5(String
                .valueOf(mSession.getUserName()) + String.valueOf(pId) + passwordEnc)));

		final HashMap<String, Object> params = new HashMap<String, Object>(4);
		params.put("pid", pId);
		params.put("username", mSession.getUserName());
		params.put("password", passwordEnc);
		 params.put("verify_code", verifyCodeEnc);

		new ApiAsyncTask(context, MarketAPI.ACTION_PURCHASE_PRODUCT, handler,
				params).execute();
	}

	*//**
	 * 获取下载链接
	 *//*
	public static void getDownloadUrl(Context context,
			ApiRequestListener handler, String pId, String sourceType) {

		Session mSession = Session.get(context);

		final HashMap<String, Object> params = new HashMap<String, Object>(3);
		params.put("p_id", pId);
		params.put("uid", mSession.getUid());
		params.put("source_type", sourceType);

		new ApiAsyncTask(context, MarketAPI.ACTION_GET_DOWNLOAD_URL, handler,
				params).execute();
	}

	*//**
	 * 搜索
	 *//*
	public static void search(Context context, ApiRequestListener handler,
			int size, int startPosition, int orderBy, String keyword) {

		Session session = Session.get(context);

		final HashMap<String, Object> params = new HashMap<String, Object>(7);
		params.put("size", size);
		params.put("start_position", startPosition);
		params.put("platform", session.getOsVersion());
		params.put("screen_size", session.getScreenSize());
		params.put("orderby", orderBy);
		params.put("keyword", keyword);
		params.put("match_type", session.isFilterApps());

		new ApiAsyncTask(context, ACTION_SEARCH, handler, params).execute();
	}

	*//**
	 * 获取商品详细信息
	 *//*
	public static void getProductDetailWithId(Context context,
			ApiRequestListener handler, int localVersion, String pId,
			String sourceType) {

		final HashMap<String, Object> params = new HashMap<String, Object>(3);
		params.put("local_version", localVersion);
		params.put("p_id", pId);
		params.put("source_type", sourceType);

		new ApiAsyncTask(context, ACTION_GET_PRODUCT_DETAIL, handler, params)
				.execute();
	}

	*//**
	 * 获取商品详细信息(包名)
	 *//*
	public static void getProductDetailWithPackageName(Context context,
			ApiRequestListener handler, int localVersion, String packageName) {

		final HashMap<String, Object> params = new HashMap<String, Object>(3);
		params.put("local_version", localVersion);
		params.put("packagename", packageName);

		new ApiAsyncTask(context, MarketAPI.ACTION_GET_DETAIL, handler, params)
				.execute();
	}

	*//**
	 * 获取消费总额
	 *//*
	public static void getConsumeSum(Context context,
			ApiRequestListener handler, String uId) {

		final HashMap<String, Object> params = new HashMap<String, Object>(1);
		params.put("uid", uId);

		new ApiAsyncTask(context, ACTION_GET_CONSUMESUM, handler, params)
				.execute();
	}

	*//**
	 * 同步用户购买记录
	 *//*
	public static void syncBuyLog(Context context, ApiRequestListener handler) {

		Session mSession = Session.get(context);

		final HashMap<String, Object> params = new HashMap<String, Object>(1);
		params.put("uid", mSession.getUid());

		new ApiAsyncTask(context, ACTION_SYNC_BUYLOG, handler, params)
				.execute();
	}

	*//**
	 * 获取我的评级
	 *//*
	public static void getMyRating(Context context, ApiRequestListener handler,
			String pId) {

		Session mSession = Session.get(context);

		final HashMap<String, Object> params = new HashMap<String, Object>(2);
		params.put("uid", mSession.getUid());
		params.put("p_id", pId);

		new ApiAsyncTask(context, ACTION_GET_MYRATING, handler, params)
				.execute();
	}



	*//**
	 * 获取消费明细
	 *//*
	public static void getConsumeDetail(Context context,
			ApiRequestListener handler, String uid, String type) {

		final HashMap<String, Object> params = new HashMap<String, Object>(2);
		params.put("uid", uid);
		params.put("type", type);

		new ApiAsyncTask(context, ACTION_GET_CONSUME_DETAIL, handler, params)
				.execute();
	}

	*//**
	 * 获取专题列表
	 *//*
	public static void getTopic(Context context, ApiRequestListener handler) {

		Session mSession = Session.get(context);

		final HashMap<String, Object> params = new HashMap<String, Object>(4);
		params.put("platform", mSession.getOsVersion());
		params.put("screen_size", mSession.getScreenSize());
		params.put("match_type", mSession.isFilterApps());

		new ApiAsyncTask(context, ACTION_GET_TOPIC, handler, params).execute();
	}

	*//**
	 * 检查更新（机锋市场）
	 *//*
	public static void checkUpdate(Context context, ApiRequestListener handler) {

		Session mSession = Session.get(context);

		final HashMap<String, Object> params = new HashMap<String, Object>(4);
		params.put("package_name", mSession.getPackageName());
		params.put("version_code", mSession.getVersionCode());
		params.put("sdk_id", mSession.getCpid());
		params.put("type", mSession.getDebugType());

		new ApiAsyncTask(context, ACTION_CHECK_NEW_VERSION, handler, params)
				.execute();
	}

	 *//**
	 * 检查更新（应用）
	 *//*
    public static void checkUpgrade(final Context context) {

        final HashMap<String, Object> params = new HashMap<String, Object>(1);
        params.put("upgradeList", Utils.getInstalledApps(context));

        new ApiAsyncTask(context, ACTION_CHECK_UPGRADE, new ApiRequestListener() {
            @Override
            public void onSuccess(int method, Object obj) {
                // do nothing
            }

            @Override
            public void onError(int method, int statusCode) {
                // do nothing
                Utils.D("check upgrade fail : " + statusCode);
            }
        }, params).execute();
    }

	

	*//**
	 * 检查是否有新splash需要下载
	 * *//*
	public static void checkNewSplash(Context context,
			ApiRequestListener handler) {

		Session mSession = Session.get(context);

		final HashMap<String, Object> params = new HashMap<String, Object>(4);
		params.put("package_name", mSession.getPackageName());
		params.put("version_code", mSession.getVersionCode());
		params.put("sdk_id", mSession.getCpid());
		params.put("time", mSession.getSplashTime());

		new ApiAsyncTask(context, ACTION_CHECK_NEW_SPLASH, handler, params)
				.execute();
	}

	

	

	*//**
	 * 提交所有应用 
	 *//*
    public static void submitAllInstalledApps(final Context context) {

        PackageManager pm = context.getPackageManager();
        List<PackageInfo> packages = pm.getInstalledPackages(0);
        ArrayList<UpgradeInfo> appList = new ArrayList<UpgradeInfo>();
        for (PackageInfo info : packages) {
            UpgradeInfo app = new UpgradeInfo();
            app.name = String.valueOf(info.applicationInfo.loadLabel(pm));
            app.versionName = info.versionName;
            app.versionCode = info.versionCode;
            app.pkgName = info.packageName;
            appList.add(app);
        }
        final HashMap<String, Object> params = new HashMap<String, Object>(1);
        params.put("appList", appList);
        new ApiAsyncTask(context, MarketAPI.ACTION_SYNC_APPS, null, params).execute();
    }*/

}