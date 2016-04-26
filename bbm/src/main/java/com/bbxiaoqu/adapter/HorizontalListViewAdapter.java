package com.bbxiaoqu.adapter;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bbxiaoqu.R;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

public class HorizontalListViewAdapter extends BaseAdapter{
    List<String> imagList;
    private Context mContext;
    private LayoutInflater mInflater;
    Bitmap iconBitmap;
    private int selectIndex = -1;

    public HorizontalListViewAdapter(Context context, List<String> list){
        this.mContext = context;
        imagList = list;
        mInflater=(LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);//LayoutInflater.from(mContext);
    }
    @Override
    public int getCount() {
        return imagList.size();
    }
    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        ImageView imageView = (ImageView) LayoutInflater.from(mContext).inflate(R.layout.img,
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
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(new LinearLayout.LayoutParams(200, 200));
            imageView.setLayoutParams(lp);

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

            Drawable drawable = mContext.getResources().getDrawable(R.mipmap.ic_launcher);
            bitmap = Bitmap.createBitmap(
                    drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight(),
                    drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                            : Bitmap.Config.RGB_565);
            imageView.setImageBitmap(bitmap);
        }
        return imageView;

    }

   /* private static class ViewHolder {
        private TextView mTitle ;
        private ImageView mImage;
    }

    public void setSelectIndex(int i){
        selectIndex = i;
    }*/
}