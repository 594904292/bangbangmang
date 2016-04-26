package com.bbxiaoqu.ui.sub;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bbxiaoqu.R;
import com.nostra13.universalimageloader.core.ImageLoader;

public class GalleryAdapter extends BaseAdapter {
	List<String> imagList;
	Context context;

	public GalleryAdapter(List<String> list, Context cx) {
		imagList = list;
		context = cx;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return imagList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getItemViewType(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ImageView imageView = (ImageView) LayoutInflater.from(context).inflate(R.layout.img,
				null);
		Bitmap bitmap = null;
		bitmap = ImageLoader.getInstance().loadImageSync(imagList.get(position));
		if(bitmap!=null)
		{
			imageView.setTag(imagList.get(position));
			int width = bitmap.getWidth();
			int height = bitmap.getHeight();
			float newHeight = 200;
			float newWidth = width*newHeight/height;
			float scaleWidth = ((float) newWidth) / width;
			float scaleHeight = ((float) newHeight) / height;
			
			Matrix matrix = new Matrix();
			matrix.postScale(scaleWidth, scaleHeight);
			Bitmap newbm = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
			System.out.println(newbm.getHeight()+"-----------"+newbm.getWidth());

			imageView.setImageBitmap(newbm);
		}else
		{
			/*AssetManager am = getAssets();
			InputStream is = null;
			try {
				is = am.open("item0_pic.jpg");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Bitmap newbm = BitmapFactory.decodeStream(is);
			imageView.setImageBitmap(newbm);*/
			
			Drawable drawable = context.getResources().getDrawable(R.mipmap.ic_launcher);
			bitmap = Bitmap.createBitmap(  
			                               drawable.getIntrinsicWidth(),  
			                               drawable.getIntrinsicHeight(),  
			                               drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888  
			                                               : Bitmap.Config.RGB_565);
			imageView.setImageBitmap(bitmap);
		}
		return imageView;

	}
}