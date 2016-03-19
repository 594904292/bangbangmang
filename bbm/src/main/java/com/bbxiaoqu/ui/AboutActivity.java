package com.bbxiaoqu.ui;


import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.jivesoftware.smack.util.Base64.InputStream;

import com.bbxiaoqu.ImageOptions;
import com.bbxiaoqu.R;
import com.bbxiaoqu.ui.sub.SettingsActivity;
import com.bbxiaoqu.view.BaseActivity;
import com.nostra13.universalimageloader.core.ImageLoader;


import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class AboutActivity extends BaseActivity {
	private TextView textView;
	TextView title;
	TextView about_save;
	TextView right_text;
	ImageView top_more;
	ImageView qr_android;
	//private IWXAPI wxApi;
	// Bitmap bitmap=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);	
		initView();
		initData();
		//wxApi = WXAPIFactory.createWXAPI(this, Constants.APP_ID);
	    //wxApi.registerApp(Constants.APP_ID);
		qr_android =(ImageView)findViewById(R.id.qr_android);
		 String url = "http://api.bbxiaoqu.com/wap/qr_android.png";
		 ImageLoader.getInstance().displayImage(url, qr_android, ImageOptions.getOptions());  
		 
		 qr_android.setDrawingCacheEnabled(true);
 		 qr_android.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Uri uri = Uri.parse("http://api.bbxiaoqu.com/wap/index.php");
				Intent it = new Intent(Intent.ACTION_VIEW, uri);  
				startActivity(it);
			}
		});
 		about_save = (TextView) findViewById(R.id.about_save);	 		
 		about_save.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				
				String SAVE_PIC_PATH=Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED) ? Environment.getExternalStorageDirectory().getAbsolutePath() : "/mnt/sdcard";//保存到SD卡
				String SAVE_REAL_PATH = SAVE_PIC_PATH + "/";
				
				SimpleDateFormat bartDateFormat = new SimpleDateFormat("yyyyMMddHHmmss"); 
				Date date = new Date(); 
				String tofilePath =SAVE_REAL_PATH + bartDateFormat.format(date)+".png";
				Bitmap obmp=qr_android.getDrawingCache();
				try {
					saveBitmapToFile(obmp,tofilePath);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				about_save.setText("保存到:"+tofilePath);
			}
		});
 		
 		
		textView = (TextView) findViewById(R.id.about_textView1);	
		textView.setText("帮帮忙（"+getVersionName()+"),作为全球首家个人C2C服务平台，是北京思博易科技有限公司独立开发运营的一款针对家庭小区生活服务而打造的手机端应用，初期为小区用户提供全方位的生活配套信息求助、二手交易服务，服务内容包括：生活求助、二手求购、二手出售等四项服务内容。");
	}
	
	public String getSDPath(){  
        File sdDir = null;  
        boolean sdCardExist = Environment.getExternalStorageState()    
                              .equals(android.os.Environment.MEDIA_MOUNTED);  //判断sd卡是否存在  
        if  (sdCardExist)    
        {                                   
            sdDir = Environment.getExternalStorageDirectory();//获取跟目录  
          }    
        return sdDir.toString();  
          
    }  
	
	/**
     * Save Bitmap to a file.保存图片到SD卡。
     * 
     * @param bitmap
     * @param file
     * @return error message if the saving is failed. null if the saving is
     *         successful.
     * @throws IOException
     */
    public static void saveBitmapToFile(Bitmap bitmap, String _file)
            throws IOException {
        BufferedOutputStream os = null;
        try {
            File file = new File(_file);
             int end = _file.lastIndexOf(File.separator);
            String _filePath = _file.substring(0, end);
            File filePath = new File(_filePath);
            if (!filePath.exists()) {
                filePath.mkdirs();
            }
            file.createNewFile();
            os = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    //Log.e(TAG_ERROR, e.getMessage(), e);
                }
            }
        }
    }
	
	private void initView() {
		title = (TextView)findViewById(R.id.title);
		right_text = (TextView)findViewById(R.id.right_text);
		right_text.setVisibility(View.VISIBLE);
		right_text.setClickable(true);		
		top_more = (ImageView) findViewById(R.id.top_more);	
		top_more.setVisibility(View.VISIBLE);
		top_more.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(AboutActivity.this,SearchActivity.class);									
				startActivity(intent);
				
				
			}
		});
	}

	private void initData() {
		title.setText("关于帮帮忙");
		right_text.setText("");
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		//overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
		finish();
	}
	
	private String getVersionName(){
		//获取packagemanager的实例 
		PackageManager packageManager = getPackageManager();
		//getPackageName()是你当前类的包名，0代表是获取版本信息
		PackageInfo packInfo = null;
		try {
			packInfo = packageManager.getPackageInfo(getPackageName(), 0);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    return packInfo.versionName; 
	}
}
