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
		 String url = "http://www.bbxiaoqu.com/wap/qr_android.png";
		 ImageLoader.getInstance().displayImage(url, qr_android, ImageOptions.getOptions());  
		 
		 qr_android.setDrawingCacheEnabled(true);
 		 qr_android.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Uri uri = Uri.parse("http://www.bbxiaoqu.com/wap/");
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
		textView.setText("襄助（"+getVersionName()+")是基于位置的是传播正能量的互联网互助平台。让附近的人互相帮忙，我们希望把大众的力量组织起来，有一技之长的人可以通过“襄助”为附近的人提供帮助；普通大众可以通过“襄助” 快速寻求帮助。 “涓滴之水成海洋，颗颗爱心变希望”。");
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
