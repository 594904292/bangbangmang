package com.bbxiaoqu.comm.gallery;

import java.io.IOException;

import com.bbxiaoqu.R;
import com.bbxiaoqu.ui.main.ViewActivity;
import com.bbxiaoqu.ui.sub.ViewUserInfoActivity;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class BigImgActivity extends Activity {
	private ImageView imageView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.img);
		imageView = (ImageView)findViewById(R.id.waterfall_image);
		Intent intent = getIntent();
		String imageName = intent.getStringExtra("imageName");
		Bitmap bitmap = ImageLoader.getInstance().loadImageSync(imageName);
		imageView.setImageBitmap(bitmap);
		imageView.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				onBackPressed();
			}
		});
	}
	
	
    public static Bitmap convertViewToBitmap(View view)
    {
        view.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();
        return bitmap;
    }    

}
