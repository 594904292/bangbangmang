package com.bbxiaoqu;

import com.nostra13.universalimageloader.core.DisplayImageOptions;

public class ImageOptions {

	public static DisplayImageOptions options=null;

	public static DisplayImageOptions getOptions() {
		if(options==null)
		{
			options = new DisplayImageOptions.Builder()  
		     .showStubImage(R.mipmap.load)
		     .showImageForEmptyUri(R.mipmap.empty)
		     .showImageOnFail(R.mipmap.error)
		     .cacheInMemory(true)  
		     .cacheOnDisk(true)  
		     .build();  
		}
		return options;
	}

	public static void setOptions(DisplayImageOptions options) {
		ImageOptions.options = options;
	}
	
}
