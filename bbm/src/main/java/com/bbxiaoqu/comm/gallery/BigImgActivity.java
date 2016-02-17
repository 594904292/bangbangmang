package com.bbxiaoqu.comm.gallery;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher.ViewFactory;

import com.bbxiaoqu.R;
import com.bbxiaoqu.ui.SearchActivity;
import com.bbxiaoqu.view.BaseActivity;
import com.nostra13.universalimageloader.core.ImageLoader;

public class BigImgActivity extends BaseActivity implements ViewFactory, OnTouchListener{
	private TextView textView;
	TextView title;
	TextView right_text;
	ImageView top_more;

		/**
		 * ImagaSwitcher 的引用
		 */
		private ImageSwitcher mImageSwitcher;
		/**
		 * 图片id数组
		 */
		private String[] imgIds;
		/**
		 * 当前选中的图片id序号
		 */
		private int currentPosition;
		/**
		 * 按下点的X坐标
		 */
		private float downX;
		/**
		 * 装载点点的容器
		 */
		private LinearLayout linearLayout;
		/**
		 * 点点数组
		 */
		private ImageView[] tips;

		String imageName="";
		String imageNames="";
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.show_photo);
			initView();
			initData();
			Bundle Bundle1 = this.getIntent().getExtras();
			imageName = Bundle1.getString("imageName");
			imageNames = Bundle1.getString("imageNames");
			imgIds=imageNames.split(",");

//			imgIds = new int[]{R.mipmap.item01,R.mipmap.item02,R.mipmap.item03,R.mipmap.item04,
//					R.mipmap.item05, R.mipmap.item06, R.mipmap.item07, R.mipmap.item08,R.mipmap.item09,
//					R.mipmap.item10, R.mipmap.item11, R.mipmap.item12};
			//实例化ImageSwitcher
			mImageSwitcher  = (ImageSwitcher) findViewById(R.id.imageSwitcher1);
			//设置Factory
			mImageSwitcher.setFactory(this);
			//设置OnTouchListener，我们通过Touch事件来切换图片
			mImageSwitcher.setOnTouchListener(this);

			linearLayout = (LinearLayout) findViewById(R.id.viewGroup);

			tips = new ImageView[imgIds.length];
			for(int i=0; i<imgIds.length; i++){
				ImageView mImageView = new ImageView(this);
				tips[i] = mImageView;
				LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT,
						LayoutParams.WRAP_CONTENT));
				layoutParams.rightMargin = 3;
				layoutParams.leftMargin = 3;

				mImageView.setBackgroundResource(R.mipmap.page_indicator_unfocused);
				linearLayout.addView(mImageView, layoutParams);
			}

			//这个我是从上一个界面传过来的，上一个界面是一个GridView
			currentPosition = getIntent().getIntExtra("position", 0);
			//mImageSwitcher.setImageResource(imgIds[currentPosition]);
			Bitmap bitmap = ImageLoader.getInstance().loadImageSync(imgIds[currentPosition]);
			mImageSwitcher.setImageDrawable(new BitmapDrawable(bitmap));

			setImageBackground(currentPosition);

		}

		/**
		 * 设置选中的tip的背景
		 * @param selectItems
		 */
		private void setImageBackground(int selectItems){
			for(int i=0; i<tips.length; i++){
				if(i == selectItems){
					tips[i].setBackgroundResource(R.mipmap.page_indicator_focused);
				}else{
					tips[i].setBackgroundResource(R.mipmap.page_indicator_unfocused);
				}
			}
		}

		@Override
		public View makeView() {
			final ImageView i = new ImageView(this);
			i.setBackgroundColor(0xff000000);
			i.setScaleType(ImageView.ScaleType.CENTER_CROP);
			i.setLayoutParams(new ImageSwitcher.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
			return i ;
		}

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:{
					//手指按下的X坐标
					downX = event.getX();
					break;
				}
				case MotionEvent.ACTION_UP:{
					float lastX = event.getX();
					//抬起的时候的X坐标大于按下的时候就显示上一张图片
					if(lastX > downX){
						if(currentPosition > 0){
							//设置动画，这里的动画比较简单，不明白的去网上看看相关内容
							mImageSwitcher.setInAnimation(AnimationUtils.loadAnimation(getApplication(), R.anim.left_in));
							mImageSwitcher.setOutAnimation(AnimationUtils.loadAnimation(getApplication(), R.anim.right_out));
							currentPosition --;
							Bitmap bitmap = ImageLoader.getInstance().loadImageSync(imgIds[currentPosition % imgIds.length]);
							mImageSwitcher.setImageDrawable(new BitmapDrawable(bitmap));

							//mImageSwitcher.setImageResource(imgIds[currentPosition % imgIds.length]);
							setImageBackground(currentPosition);
						}else{
							Toast.makeText(getApplication(), "已经是第一张", Toast.LENGTH_SHORT).show();
						}
					}

					if(lastX < downX){
						if(currentPosition < imgIds.length - 1){
							mImageSwitcher.setInAnimation(AnimationUtils.loadAnimation(getApplication(), R.anim.right_in));
							mImageSwitcher.setOutAnimation(AnimationUtils.loadAnimation(getApplication(), R.anim.lift_out));
							currentPosition ++ ;
							Bitmap bitmap = ImageLoader.getInstance().loadImageSync(imgIds[currentPosition]);
							mImageSwitcher.setImageDrawable(new BitmapDrawable(bitmap));

							//mImageSwitcher.setImageResource(imgIds[currentPosition]);
							setImageBackground(currentPosition);
						}else{
							Toast.makeText(getApplication(), "到了最后一张", Toast.LENGTH_SHORT).show();
						}
					}
				}

				break;
			}

			return true;
		}

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
	}
