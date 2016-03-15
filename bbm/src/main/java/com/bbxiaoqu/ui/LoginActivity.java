package com.bbxiaoqu.ui;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.bbxiaoqu.DemoApplication;
import com.bbxiaoqu.R;
import com.bbxiaoqu.api.ApiAsyncTask.ApiRequestListener;
import com.bbxiaoqu.api.MarketAPI;
import com.bbxiaoqu.client.baidu.Utils;
import com.bbxiaoqu.comm.service.User;
import com.bbxiaoqu.comm.service.db.UserService;
import com.bbxiaoqu.ui.main.MainActivity;
import com.bbxiaoqu.ui.user.RegisterActivity;
import com.bbxiaoqu.view.BaseActivity;

public class LoginActivity extends BaseActivity implements OnFocusChangeListener,ApiRequestListener {
	private DemoApplication myapplication;
	EditText etUsername;
	EditText etPassword;
	Button login, register,searchpass;
	UserService uService = new UserService(LoginActivity.this);
	private static final int DIALOG_PROGRESS = 0;
	//用户不存在（用户名错误）
	private static final int ERROR_CODE_USERNAME_NOT_EXIST = 211;
	//用户密码错误
	private static final int ERROR_CODE_PASSWORD_INVALID = 212;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Utils.logStringCache = Utils.getLogText(getApplicationContext());
		setContentView(R.layout.activity_login);
		Resources resource = this.getResources();
		String pkgName = this.getPackageName();
		myapplication = (DemoApplication) this.getApplication();
		initView();
	}

	private void initView() {
		// TODO Auto-generated method stub
		etUsername = (EditText) findViewById(R.id.username);
		etPassword = (EditText) findViewById(R.id.password);
		String userid="";
		if(mSession.getUid()!=null)
		{
			userid=mSession.getUid();
		}
		etUsername.setText(userid);
		login = (Button) findViewById(R.id.login);
		register = (Button) findViewById(R.id.register);
		searchpass = (Button) findViewById(R.id.searchpass);
		login.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				String userid = etUsername.getText().toString();
				String pass = etPassword.getText().toString();
				Log.i("TAG", userid + "_" + pass);
				boolean flag = uService.login(userid, pass);
				if (flag) {
					String headface=uService.getheadface(userid);
					myapplication.setUserId(userid);
					uService.online(userid);// 更改状态
					mSession.setUid(userid);
					mSession.setHeadface(headface);
					mSession.setPassword(pass);
					mSession.setIslogin(true);
					Intent intent = new Intent(LoginActivity.this,MainActivity.class);
					startActivity(intent);
				} else {
					login();					
				}
			}
		});
		register.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(LoginActivity.this,	RegisterActivity.class);
				startActivity(intent);
			}
		});

		searchpass.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(LoginActivity.this,	SearchPassActivity.class);
				startActivity(intent);
			}
		});
	}
	
	
		private void login() {
	        if (!isFinishing()) {
	            showDialog(DIALOG_PROGRESS);
	        } else {
	            // 如果当前页面已经关闭，不进行登录操作
	            return;
	        }
			String userName = etUsername.getText().toString();
			String passWord= etPassword.getText().toString();			
			MarketAPI.login(getApplicationContext(), this, userName, passWord);
		}

		@Override
		protected void onPrepareDialog(int id, Dialog dialog) {
			super.onPrepareDialog(id, dialog);
		    if (dialog.isShowing()) {
		    	dialog.dismiss();
		    }
		}
		 @Override
		 protected Dialog onCreateDialog(int id) {
			 switch (id) {
		     case DIALOG_PROGRESS:
		        ProgressDialog mProgressDialog = new ProgressDialog(this);
		        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		        mProgressDialog.setMessage(getString(R.string.singin));
		        return mProgressDialog;
		     default:
		     	return super.onCreateDialog(id);
		     }
		 }
	

	protected void onDestroy() {
		super.onDestroy();
		if (uService.dbHelper != null) {
			uService.close();
		}
	}

	public void onSuccess(int method, Object obj) {
	    switch (method) {
        case MarketAPI.ACTION_LOGIN:   
        	 try{
                 dismissDialog(DIALOG_PROGRESS);
             }catch (IllegalArgumentException e) {
             }
            HashMap<String, String> result = (HashMap<String, String>) obj;
            String JsonContext=result.get("login");           
			JSONObject jsonobj = null;
			try {
				jsonobj = new JSONObject(JsonContext);
				String userid = jsonobj.getString("userid");
				if (userid != "") {
					String pass = etPassword.getText().toString();
					String password = jsonobj.getString("pass");
					if(!pass.equals(password))
					{
						Toast.makeText(LoginActivity.this, "码密错误",Toast.LENGTH_LONG).show();
						return;
					}else {
						String telphone = jsonobj.getString("telphone");
						String headface = jsonobj.getString("headface");
						String username = jsonobj.getString("username");
						User user = new User();
						user.setNickname(username);
						user.setUsername(userid);
						user.setPassword(password);
						user.setTelphone(telphone);
						user.setHeadface(headface);
						uService.register(user);// 注册一个
						boolean flag = uService.login(userid, password);
						myapplication.setUserId(userid);
						myapplication.setPassword(password);
						myapplication.setNickname(username);
						myapplication.setHeadface(headface);
						if (flag) {
							Log.i("TAG", "登录成功");
							uService.online(userid);// 更改状态
							mSession.setUid(userid);
							mSession.setUserName(username);
							mSession.setPassword(password);
							Intent intent = new Intent(LoginActivity.this, MainActivity.class);
							startActivity(intent);
						} else {
							Toast.makeText(LoginActivity.this, "码密错误", Toast.LENGTH_LONG).show();
						}
					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}// 转换为JSONObject
            break;
        default:
            break;
        }
	}

	@Override
    public void onError(int method, int statusCode) {
	    switch (method) {
        case MarketAPI.ACTION_LOGIN:            
            // 隐藏登录框
            try{
                dismissDialog(DIALOG_PROGRESS);
            }catch (IllegalArgumentException e) {
            }            
            String msg = null;
            if(statusCode == ERROR_CODE_USERNAME_NOT_EXIST) {
                msg = getString(R.string.error_login_username);
            } else if(statusCode == ERROR_CODE_PASSWORD_INVALID) {
                msg = getString(R.string.error_login_password);
            } else {
                msg = getString(R.string.error_login_other);
            }
            Utils.makeEventToast(getApplicationContext(), msg, false);
            break;
        default:
            break;
        }
    }
	
	 /*
     * 检查用户名合法性
     * 1 不能为空
     * 2 长度在 3 - 16 个字符之间
     */
    private boolean checkUserName() {
        String input = etUsername.getText().toString();
        if (TextUtils.isEmpty(input)) {
            etUsername.setError(getString(R.string.error_username_empty));
            return false;
        } else {
            etUsername.setError(null);
        }
        int length = input.length();
        if (length < 3 || length > 16) {
            etUsername.setError(getString(R.string.error_username_length_invalid));
            return false;
        } else {
            etUsername.setError(null);
        }
        return true;
    }
    
    /*
     * 检查用户密码合法性
     * 1 不能为空
     * 2 长度在1 - 32 个字符之间
     */
    private boolean checkPassword(EditText input) {
        String passwod = input.getText().toString();
        if (TextUtils.isEmpty(passwod)) {
            input.setError(getString(R.string.error_password_empty));
            return false;
        } else {
            input.setError(null);
        }
        int length = passwod.length();
        if (length > 32) {
            input.setError(getString(R.string.error_password_length_invalid));
            return false;
        } else {
            input.setError(null);
        }
        return true;
    }



	@Override
	public void onFocusChange(View v, boolean flag) {
        switch (v.getId()) {
        case R.id.username:
            if (!flag) {
                checkUserName();
            }
            break;
        case R.id.password:
            if (!flag) {
                checkPassword(etPassword);
            }
            break;
        default:
            break;
        }
    }
}