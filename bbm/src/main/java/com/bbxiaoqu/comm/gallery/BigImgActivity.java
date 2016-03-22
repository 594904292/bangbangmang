package com.bbxiaoqu.comm.gallery;
import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.bbxiaoqu.R;
import com.bbxiaoqu.view.BaseActivity;
import com.nostra13.universalimageloader.core.ImageLoader;

public class BigImgActivity extends BaseActivity implements OnGestureListener
{
	private TextView textView;
	TextView title;
	TextView right_text;
	ImageView top_more;
	private ViewFlipper mViewFlipper = null;
	private LinearLayout mViewGroup = null;

	private GestureDetector mGestureDetector = null;

	private String[] mImageIds ;
	String imageName="";
	String imageNames="";
	private ImageView[] mImageViews = null;
	private ImageView[] mTips = null;

	private int currentIndex = 0;

	private void initView() {
		title = (TextView)findViewById(R.id.title);
		right_text = (TextView)findViewById(R.id.right_text);
		right_text.setVisibility(View.VISIBLE);
		right_text.setClickable(true);
		top_more = (ImageView) findViewById(R.id.top_more);
		top_more.setVisibility(View.GONE);

	}

	private void initData() {
		title.setText("相册");
		right_text.setText("");
	}
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_photo);
		initView();
		initData();
		Bundle Bundle1 = this.getIntent().getExtras();
		imageName = Bundle1.getString("imageName");
		imageNames = Bundle1.getString("imageNames");
		mImageIds=imageNames.split(",");

		mViewFlipper = (ViewFlipper) findViewById(R.id.vf);
		mViewGroup = (LinearLayout) findViewById(R.id.viewGroup);

		mGestureDetector = new GestureDetector(this,this);

		mImageViews = new ImageView[mImageIds.length];
		for(int i = 0; i < mImageViews.length; i++)
		{
			ImageView iv = new ImageView(this);
			int reqWidth = getWindowManager().getDefaultDisplay().getWidth();
			int reqHeight = getWindowManager().getDefaultDisplay().getHeight();
			//iv.setImageBitmap(decodeSampledBitmapFromResource(getResources(),mImageIds[i], reqWidth, reqHeight));

			Bitmap bitmap = ImageLoader.getInstance().loadImageSync(mImageIds[i]);
			//mImageSwitcher.setImageDrawable(new BitmapDrawable(bitmap));
			iv.setImageBitmap(bitmap);

			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
			mViewFlipper.addView(iv,lp);
		}

		mTips = new ImageView[mImageIds.length];
		for(int i = 0; i < mTips.length; i++)
		{
			ImageView iv = new ImageView(this);
			iv.setLayoutParams(new LayoutParams(10,10));
			mTips[i] = iv;

			if(i == 0)
			{
				iv.setBackgroundResource(R.mipmap.page_indicator_focused);
			}else
			{
				iv.setBackgroundResource(R.mipmap.page_indicator_unfocused);
			}
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
			lp.leftMargin = 5;
			lp.rightMargin = 5;
			mViewGroup.addView(iv,lp);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		return mGestureDetector.onTouchEvent(event);
	}

	private static Bitmap decodeSampledBitmapFromResource(Resources res,int resId,int reqWidth,int reqHeight)
	{
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(res, resId);
		int inSampleSize = cacluateInSampledSize(opts, reqWidth, reqHeight);
		opts.inSampleSize = inSampleSize;
		opts.inJustDecodeBounds = false;
		return BitmapFactory.decodeResource(res,resId,opts);
	}

	private static int cacluateInSampledSize(BitmapFactory.Options opts,int width,int height)
	{
		if(opts == null)
		{
			return 1;
		}
		int inSampleSize = 1;
		int realWidth = opts.outWidth;
		int realHeight = opts.outHeight;

		if(realWidth > width || realHeight > height)
		{
			int heightRatio = realHeight/height;
			int widthRatio = realWidth/width;

			inSampleSize = (widthRatio > heightRatio) ? heightRatio : widthRatio;
		}
		return inSampleSize;
	}
	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
						   float velocityY)
	{
		if(e1.getX() - e2.getX() > 120)//显示下一张
		{
			if(currentIndex != mTips.length-1)
			{
				currentIndex++;
				setImageBackground(currentIndex);
				Animation in_right = AnimationUtils.loadAnimation(this,R.anim.right_in);
				Animation out_left = AnimationUtils.loadAnimation(this,R.anim.lift_out);
				mViewFlipper.setInAnimation(in_right);
				mViewFlipper.setOutAnimation(out_left);
				mViewFlipper.showNext();
			}else
			{
				Toast.makeText(this,"已经是最后一张..",0).show();
			}
		}else if(e1.getX() - e2.getX() < -120)//显示上一张
		{
			if(currentIndex != 0)
			{
				currentIndex--;
				setImageBackground(currentIndex);
				Animation in_left = AnimationUtils.loadAnimation(this,R.anim.left_in);
				Animation out_right = AnimationUtils.loadAnimation(this,R.anim.right_out);
				mViewFlipper.setInAnimation(in_left);
				mViewFlipper.setOutAnimation(out_right);
				mViewFlipper.showPrevious();
			}else
			{
				Toast.makeText(this,"已经是第一张..",0).show();
			}
		}
		return true;
	}
	private void setImageBackground(int selectItems)
	{
		for(int i = 0; i < mTips.length; i++)
		{
			if(i == selectItems)
			{
				mTips[i].setBackgroundResource(R.mipmap.page_indicator_focused);
			}else
			{
				mTips[i].setBackgroundResource(R.mipmap.page_indicator_unfocused);
			}
		}
	}
	@Override
	public boolean onDown(MotionEvent e)
	{
		return false;
	}
	@Override
	public void onShowPress(MotionEvent e)
	{
	}
	@Override
	public boolean onSingleTapUp(MotionEvent e)
	{
		return false;
	}
	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
							float distanceY)
	{
		return false;
	}
	@Override
	public void onLongPress(MotionEvent e)
	{
	}
}