package com.bbxiaoqu.ui.sub;

import com.bbxiaoqu.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class SelectPhotoActivity extends Activity implements OnClickListener {
	/** 使用照相机拍照获取图片 */
	public static final int SELECT_PIC_BY_TACK_PHOTO = 1;
	/** 使用相册中的图片 */
	public static final int SELECT_PIC_BY_PICK_PHOTO = 2;
	/** 开启相机 */
	private Button btn_take_photo;
	/** 开启图册 */
	private Button btn_pick_photo;
	/** 取消 */
	private Button btn_cancel;
	/** 获取到的图片路径 */
	private String picPath;
	private Intent lastIntent;
	private Uri photoUri;
	/** 从Intent获取图片路径的KEY */
	public static final String KEY_PHOTO_PATH = "photo_path";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_photo);
		btn_take_photo = (Button) findViewById(R.id.btn_take_photo);
		btn_pick_photo = (Button) findViewById(R.id.btn_pick_photo);
		btn_cancel = (Button) findViewById(R.id.btn_cancel);

		lastIntent = getIntent();
		Bundle Bundle1 = lastIntent.getExtras();
		int pos=Bundle1.getInt("pos");
		lastIntent.putExtra("pos",pos);

		
		
		
		btn_take_photo.setOnClickListener(this);
		btn_pick_photo.setOnClickListener(this);
		btn_cancel.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_take_photo : // 开启相机
				takePhoto();
				break;
			case R.id.btn_pick_photo : // 开启图册
				pickPhoto();
				break;
			case R.id.btn_cancel : // 取消操作
				this.finish();
				break;
			default :
				break;
		}
	}

	/**
	 * 拍照获取图片
	 */
	private void takePhoto() {
		// 执行拍照前，应该先判断SD卡是否存在
		String SDState = Environment.getExternalStorageState();
		if (SDState.equals(Environment.MEDIA_MOUNTED)) {
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);// "android.media.action.IMAGE_CAPTURE"
			/***
			 * 需要说明一下，以下操作使用照相机拍照，拍照后的图片会存放在相册中的 这里使用的这种方式有一个好处就是获取的图片是拍照后的原图
			 * 如果不实用ContentValues存放照片路径的话，拍照后获取的图片为缩略图不清晰
			 */
			ContentValues values = new ContentValues();
			photoUri = this.getContentResolver().insert(
					MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
			intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, photoUri);
			startActivityForResult(intent, SELECT_PIC_BY_TACK_PHOTO);
		} else {
			Toast.makeText(getApplicationContext(), "内存卡不存在",
					Toast.LENGTH_SHORT).show();
		}
	}

	/***
	 * 从相册中取图片
	 */
	private void pickPhoto() {
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(intent, SELECT_PIC_BY_PICK_PHOTO);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		finish();
		return super.onTouchEvent(event);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			doPhoto(requestCode, data);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	
	/**
	 * Gets the corresponding path to a file from the given content:// URI
	 * @param selectedVideoUri The content:// URI to find the file path from
	 * @param contentResolver The content resolver to use to perform the query.
	 * @return the file path as a string
	 */
	public static String getFilePathFromContentUri(Uri selectedVideoUri,
	        ContentResolver contentResolver) {
	    String filePath;
	    String[] filePathColumn = {MediaColumns.DATA};

	    Cursor cursor = contentResolver.query(selectedVideoUri, filePathColumn, null, null, null);
//	    也可用下面的方法拿到cursor
//	    Cursor cursor = this.context.managedQuery(selectedVideoUri, filePathColumn, null, null, null);
	    
	    cursor.moveToFirst();

	    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
	    filePath = cursor.getString(columnIndex);
	    cursor.close();
	    return filePath;
	}
	/**
	 * 选择图片后，获取图片的路径
	 * 
	 * @param requestCode
	 * @param data
	 */
	@SuppressLint("NewApi")
	private void doPhoto(int requestCode, Intent data) {
			if (requestCode == SELECT_PIC_BY_PICK_PHOTO) 
			{// 从相册取图片，有些手机有异常情况，请注意
				if (data == null) {
					Toast.makeText(getApplicationContext(), "选择图片文件出错",
							Toast.LENGTH_SHORT).show();
					return;
				}
				photoUri = data.getData();
				if (photoUri == null) {
					Toast.makeText(getApplicationContext(), "选择图片文件出错",Toast.LENGTH_SHORT).show();
					return;
				}
				if(photoUri.toString().startsWith("content"))
				{

					final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;  
					
					System.out.println("VERSION:"+android.os.Build.VERSION.SDK_INT);
					System.out.println("Build KITKAT VERSION:"+android.os.Build.VERSION_CODES.KITKAT);
					//Android 4.4（代号 KitKat 奇巧
					
					//DocumentProvider  
					if(isKitKat && DocumentsContract.isDocumentUri(getApplicationContext(), photoUri)){
						if (isExternalStorageDocument(photoUri)) {  
				            final String docId = DocumentsContract.getDocumentId(photoUri);  
				            final String[] split = docId.split(":");  
				            final String type = split[0];  
				  
				            if ("primary".equalsIgnoreCase(type)) {  
				            	picPath = Environment.getExternalStorageDirectory() + "/" + split[1];  
				            }  
				  
				            // TODO handle non-primary volumes  
				        }  
				        // DownloadsProvider  
				        else if (isDownloadsDocument(photoUri)) {  
				  
				            final String id = DocumentsContract.getDocumentId(photoUri);  
				            final Uri contentUri = ContentUris.withAppendedId(  
				                    Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));  
				  
				            picPath=getDataColumn(getApplicationContext(), contentUri, null, null);  
				        }  
				        // MediaProvider  
				        else if (isMediaDocument(photoUri)) {  
				            final String docId = DocumentsContract.getDocumentId(photoUri);  
				            final String[] split = docId.split(":");  
				            final String type = split[0];  
				  
				            Uri contentUri = null;  
				            if ("image".equals(type)) {  
				                contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;  
				            } else if ("video".equals(type)) {  
				                contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;  
				            } else if ("audio".equals(type)) {  
				                contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;  
				            }  
				  
				            final String selection = "_id=?";  
				            final String[] selectionArgs = new String[] {  
				                    split[1]  
				            };  
				  
				            picPath= getDataColumn(getApplicationContext(), contentUri, selection, selectionArgs);  
				        }  
					}else
					{
						String[] pojo = {MediaStore.Images.Media.DATA};
						Cursor cursor = getContentResolver().query(photoUri, pojo, null, null, null);
						if (cursor != null) {
							int columnIndex = cursor.getColumnIndexOrThrow(pojo[0]);
							cursor.moveToFirst();
							picPath = cursor.getString(columnIndex);
							cursor.close();
						}
					}
				}else
				{
					picPath=photoUri.toString().substring(7);
				}
			}else
			{//相机
				
				  
				final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;  
				
				System.out.println("VERSION:"+android.os.Build.VERSION.SDK_INT);
				System.out.println("Build KITKAT VERSION:"+android.os.Build.VERSION_CODES.KITKAT);
				//Android 4.4（代号 KitKat 奇巧
				
				//DocumentProvider  
				if(isKitKat && DocumentsContract.isDocumentUri(getApplicationContext(), photoUri)){
					if (isExternalStorageDocument(photoUri)) {  
			            final String docId = DocumentsContract.getDocumentId(photoUri);  
			            final String[] split = docId.split(":");  
			            final String type = split[0];  
			  
			            if ("primary".equalsIgnoreCase(type)) {  
			            	picPath = Environment.getExternalStorageDirectory() + "/" + split[1];  
			            }  
			  
			            // TODO handle non-primary volumes  
			        }  
			        // DownloadsProvider  
			        else if (isDownloadsDocument(photoUri)) {  
			  
			            final String id = DocumentsContract.getDocumentId(photoUri);  
			            final Uri contentUri = ContentUris.withAppendedId(  
			                    Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));  
			  
			            picPath=getDataColumn(getApplicationContext(), contentUri, null, null);  
			        }  
			        // MediaProvider  
			        else if (isMediaDocument(photoUri)) {  
			            final String docId = DocumentsContract.getDocumentId(photoUri);  
			            final String[] split = docId.split(":");  
			            final String type = split[0];  
			  
			            Uri contentUri = null;  
			            if ("image".equals(type)) {  
			                contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;  
			            } else if ("video".equals(type)) {  
			                contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;  
			            } else if ("audio".equals(type)) {  
			                contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;  
			            }  
			  
			            final String selection = "_id=?";  
			            final String[] selectionArgs = new String[] {  
			                    split[1]  
			            };  
			  
			            picPath= getDataColumn(getApplicationContext(), contentUri, selection, selectionArgs);  
			        }  				
				}else
				{
					String[] pojo = {MediaStore.Images.Media.DATA};
					Cursor cursor = getContentResolver().query(photoUri, pojo, null, null, null);
					if (cursor != null) {
						int columnIndex = cursor.getColumnIndexOrThrow(pojo[0]);
						cursor.moveToFirst();
						picPath = cursor.getString(columnIndex);
						cursor.close();
					}
				}
			}
		
		if (picPath != null
				&& (picPath.endsWith(".png") || picPath.endsWith(".PNG")
						|| picPath.endsWith(".jpg") || picPath.endsWith(".JPG"))) {
			lastIntent.putExtra(KEY_PHOTO_PATH, picPath);	
					
			
			setResult(Activity.RESULT_OK, lastIntent);
			finish();
		} else {
			Toast.makeText(getApplicationContext(), "选择图片文件不正确",Toast.LENGTH_SHORT).show();
		}
	}
	public static String getDataColumn(Context context, Uri uri, String selection,  
	        String[] selectionArgs) {  
	  
	    Cursor cursor = null;  
	    final String column = "_data";  
	    final String[] projection = {  
	            column  
	    };  
	  
	    try {  
	        cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,  
	                null);  
	        if (cursor != null && cursor.moveToFirst()) {  
	            final int column_index = cursor.getColumnIndexOrThrow(column);  
	            return cursor.getString(column_index);  
	        }  
	    } finally {  
	        if (cursor != null)  
	            cursor.close();  
	    }  
	    return null;  
	}  
	
	public static boolean isExternalStorageDocument(Uri uri) {  
	    return "com.android.externalstorage.documents".equals(uri.getAuthority());  
	}  
	  
	/** 
	 * @param uri The Uri to check. 
	 * @return Whether the Uri authority is DownloadsProvider. 
	 */  
	public static boolean isDownloadsDocument(Uri uri) {  
	    return "com.android.providers.downloads.documents".equals(uri.getAuthority());  
	}  
	  
	/** 
	 * @param uri The Uri to check. 
	 * @return Whether the Uri authority is MediaProvider. 
	 */  
	public static boolean isMediaDocument(Uri uri) {  
	    return "com.android.providers.media.documents".equals(uri.getAuthority());  
	}  

}
