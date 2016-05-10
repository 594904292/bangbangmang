/*
 * Copyright (C) 2010 mAPPn.Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bbxiaoqu.api.util;


import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.util.EntityUtils;

import com.bbxiaoqu.R;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;
import android.widget.Toast;



/**
 * Common Utils for the application
 * 
 * @author dzyang
 * @date 2010-9-19
 * @since Version 0.4.0
 */
public class Utils {

    public static boolean sDebug;
    public static String sLogTag;

    private static final String TAG = "Utils";

    // UTF-8 encoding
    private static final String ENCODING_UTF8 = "UTF-8";

    private static WeakReference<Calendar> calendar;

    /**
     * <p>
     * Get UTF8 bytes from a string
     * </p>
     * 
     * @param string
     *            String
     * @return UTF8 byte array, or null if failed to get UTF8 byte array
     */
    public static byte[] getUTF8Bytes(String string) {
        if (string == null)
            return new byte[0];

        try {
            return string.getBytes(ENCODING_UTF8);
        } catch (UnsupportedEncodingException e) {
            /*
             * If system doesn't support UTF-8, use another way
             */
            try {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                DataOutputStream dos = new DataOutputStream(bos);
                dos.writeUTF(string);
                byte[] jdata = bos.toByteArray();
                bos.close();
                dos.close();
                byte[] buff = new byte[jdata.length - 2];
                System.arraycopy(jdata, 2, buff, 0, buff.length);
                return buff;
            } catch (IOException ex) {
                return new byte[0];
            }
        }
    }

    /**
     * <p>
     * Get string in UTF-8 encoding
     * </p>
     * 
     * @param b
     *            byte array
     * @return string in utf-8 encoding, or empty if the byte array is not encoded with UTF-8
     */
    public static String getUTF8String(byte[] b) {
        if (b == null)
            return "";
        return getUTF8String(b, 0, b.length);
    }

    /**
     * <p>
     * Get string in UTF-8 encoding
     * </p>
     */
    public static String getUTF8String(byte[] b, int start, int length) {
        if (b == null) {
            return "";
        } else {
            try {
                return new String(b, start, length, ENCODING_UTF8);
            } catch (UnsupportedEncodingException e) {
                return "";
            }
        }
    }

    /**
     * <p>
     * Parse int value from string
     * </p>
     * 
     * @param value
     *            string
     * @return int value
     */
    public static int getInt(String value) {
        if (TextUtils.isEmpty(value)) {
            return 0;
        }

        try {
            return Integer.parseInt(value.trim(), 10);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * <p>
     * Parse float value from string
     * </p>
     * 
     * @param value
     *            string
     * @return float value
     */
    public static float getFloat(String value) {
        if (value == null)
            return 0f;

        try {
            return Float.parseFloat(value.trim());
        } catch (NumberFormatException e) {
            return 0f;
        }
    }

    /**
     * <p>
     * Parse long value from string
     * </p>
     * 
     * @param value
     *            string
     * @return long value
     */
    public static long getLong(String value) {
        if (value == null)
            return 0L;

        try {
            return Long.parseLong(value.trim());
        } catch (NumberFormatException e) {
            return 0L;
        }
    }

    public static void V(String msg) {
        if (sDebug) {
            Log.v(sLogTag, msg);
        }
    }

    public static void V(String msg, Throwable e) {
        if (sDebug) {
            Log.v(sLogTag, msg, e);
        }
    }

    public static void D(String msg) {
        if (sDebug) {
            Log.d(sLogTag, msg);
        }
    }

    public static void D(String msg, Throwable e) {
        if (sDebug) {
            Log.d(sLogTag, msg, e);
        }
    }

    public static void I(String msg) {
        if (sDebug) {
            Log.i(sLogTag, msg);
        }
    }

    public static void I(String msg, Throwable e) {
        if (sDebug) {
            Log.i(sLogTag, msg, e);
        }
    }

    public static void W(String msg) {
        if (sDebug) {
            Log.w(sLogTag, msg);
        }
    }

    public static void W(String msg, Throwable e) {
        if (sDebug) {
            Log.w(sLogTag, msg, e);
        }
    }

    public static void E(String msg) {
        if (sDebug) {
            Log.e(sLogTag, msg);
        }
    }

    public static void E(String msg, Throwable e) {
        if (sDebug) {
            Log.e(sLogTag, msg, e);
        }
    }

    public static String formatDate(long time) {
        if (calendar == null || calendar.get() == null) {
            calendar = new WeakReference<Calendar>(Calendar.getInstance());
        }
        Calendar target = calendar.get();
        target.setTimeInMillis(time);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(target.getTime());
    }

    public static String getTodayDate() {
        if (calendar == null || calendar.get() == null) {
            calendar = new WeakReference<Calendar>(Calendar.getInstance());
        }
        Calendar today = calendar.get();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(today.getTime());
    }

    /**
     * Returns whether the network is available
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
            Log.w(TAG, "couldn't get connectivity manager");
        } else {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0, length = info.length; i < length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Returns whether the network is roaming
     */
    public static boolean isNetworkRoaming(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
            // Log.w(Constants.TAG, "couldn't get connectivity manager");
        } else {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null && info.getType() == ConnectivityManager.TYPE_MOBILE) {
            } else {
            }
        }
        return false;
    }

    // /**
    // * Get the decrypted HTTP response body<br>
    // *
    // * @return if response is empty or some error occured when decrypt process
    // * will return EMPTY string
    // */
    // public static String getDecryptedResponseBody(HttpEntity entity) {
    // byte[] response = SecurityUtil.decryptHttpEntity(entity);
    // return Utils.getUTF8String(response);
    // }
    //
    // private static GoogleAnalyticsTracker mTracker;
    //
   
    // /**
    // * Show toast information
    // *
    // * @param context
    // * application context
    // * @param text
    // * the information which you want to show
    // * @return show toast dialog
    // */
    // public static void makeEventToast(Context context, String text, boolean isLongToast) {
    //
    // Toast toast = null;
    // if (isLongToast) {
    // toast = Toast.makeText(context, "", Toast.LENGTH_LONG);
    // } else {
    // toast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
    // }
    // View v = LayoutInflater.from(context).inflate(R.layout.toast_view, null);
    // TextView textView = (TextView) v.findViewById(R.id.text);
    // textView.setText(text);
    // toast.setView(v);
    // toast.show();
    // }

    /**
     * 格式化时间（Format：yyyy-MM-dd HH:mm）
     * 
     * @param timeInMillis
     * @return
     */
    public static String formatTime(long timeInMillis) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return sdf.format(new Date(timeInMillis));
    }

    // /**
    // * 从商家版Zip包中获取加密文件输入流
    // */
    // public static DecryptStream getDecryptStream(File file, String entryName) {
    // try {
    // ZipFile zipPackage = new ZipFile(file);
    // ZipEntry entry = zipPackage.getEntry(entryName);
    // if (entry == null) {
    // return null;
    // }
    // return new DecryptStream(zipPackage.getInputStream(entry));
    // } catch (IOException e) {
    // }
    // return null;
    // }

    // /**
    // * 从商家版Zip包中获取普通文件输入流
    // */
    // public static InputStream getNormalStream(File file, String entryName) {
    // try {
    // ZipFile zipPackage = new ZipFile(file);
    // ZipEntry entry = zipPackage.getEntry(entryName);
    // if (entry == null) {
    // return null;
    // }
    // return zipPackage.getInputStream(entry);
    // } catch (IOException e) {
    // }
    // return null;
    // }

    // /**
    // * 获取商家版加密后的APK文件，并拷贝到SD卡上（/sdcard/gfan/apk）
    // * @param root 商家版应用包文件
    // * @param entryName APK文件
    // * @return 拷贝后的文件
    // */
    // public static File getEncryptApk(File root, String entryName) {
    //
    // InputStream in = null;
    // FileOutputStream fos = null;
    // File outputFile = null;
    // try {
    // outputFile = new File(Environment.getExternalStorageDirectory() + "/gfan/apk",
    // entryName);
    // fos = new FileOutputStream(outputFile);
    // in = getDecryptStream(root, entryName);
    // if (in == null) {
    // return null;
    // }
    // copyFile(in, fos);
    // } catch (FileNotFoundException e) {
    // e.printStackTrace();
    // } catch (IOException e) {
    // e.printStackTrace();
    // }
    // return outputFile;
    // }

    // /**
    // * 获取商家版APK对应的ICON文件，并拷贝到SD卡上（/sdcard/gfan/.cache）
    // * @param root 商家版应用包文件
    // * @param entryName ICON文件
    // * @return 拷贝后的文件
    // */
    // public static File getApkIcon(File root, String entryName) {
    // InputStream in = null;
    // FileOutputStream fos = null;
    // File outputFile = null;
    // try {
    // outputFile = new File(Environment.getExternalStorageDirectory() + "/gfan/.cache",
    // entryName);
    // fos = new FileOutputStream(outputFile);
    // in = getNormalStream(root, entryName);
    // if (in == null) {
    // return null;
    // }
    // copyFile(in, fos);
    // } catch (FileNotFoundException e) {
    // e.printStackTrace();
    // } catch (IOException e) {
    // e.printStackTrace();
    // }
    // return outputFile;
    // }

    /**
     * 文件拷贝工具类
     * 
     * @param src
     *            源文件
     * @param dst
     *            目标文件
     * @throws IOException
     */
    public static void copyFile(InputStream in, FileOutputStream dst) throws IOException {
        byte[] buffer = new byte[8192];
        int len = 0;
        while ((len = in.read(buffer)) > 0) {
            dst.write(buffer, 0, len);
        }
        in.close();
        dst.close();
    }

    /**
     * 解析HTTP String Entity
     * 
     * @param response
     *            HTTP Response
     * @return 市场API返回的消息(String)
     */
    public static String getStringResponse(HttpResponse response) {
        HttpEntity entity = response.getEntity();
        try {
            return entity == null ? null : EntityUtils.toString(response.getEntity());
        } catch (ParseException e) {
            D("getStringResponse meet ParseException", e);
        } catch (IOException e) {
            D("getStringResponse meet IOException", e);
        }
        return null;
    }


    /**
     * 界面切换动画
     * 
     * @return
     */
    public static LayoutAnimationController getLayoutAnimation() {
        AnimationSet set = new AnimationSet(true);

        Animation animation = new AlphaAnimation(0.0f, 1.0f);
        animation.setDuration(50);
        set.addAnimation(animation);

        animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, -1.0f,
                Animation.RELATIVE_TO_SELF, 0.0f);
        animation.setDuration(100);
        set.addAnimation(animation);

        LayoutAnimationController controller = new LayoutAnimationController(set, 0.5f);
        return controller;
    }

   
    /**
     * 比较两个文件的签名是否一致
     */
    public static boolean compareFileWithSignature(String path1, String path2) {

        long start = System.currentTimeMillis();
        if (TextUtils.isEmpty(path1) || TextUtils.isEmpty(path2)) {
            return false;
        }

        String signature1 = getFileSignatureMd5(path1);
        String signature2 = getFileSignatureMd5(path2);

        V("compareFileWithSignature total time is " + (System.currentTimeMillis() - start));
        if (!TextUtils.isEmpty(signature1) && signature1.equals(signature2)) {
            return true;
        }
        return false;
    }

    /**
     * 获取应用签名MD5
     */
    public static String getFileSignatureMd5(String targetFile) {

        try {
            JarFile jarFile = new JarFile(targetFile);
            // 取RSA公钥
            JarEntry jarEntry = jarFile.getJarEntry("AndroidManifest.xml");

            if (jarEntry != null) {
                InputStream is = jarFile.getInputStream(jarEntry);
                byte[] buffer = new byte[8192];
                while (is.read(buffer) > 0) {
                    // do nothing
                }
                is.close();
                Certificate[] certs = jarEntry == null ? null : jarEntry.getCertificates();
                if (certs != null && certs.length > 0) {
                    String rsaPublicKey = String.valueOf(certs[0].getPublicKey());
                    return getMD5(rsaPublicKey);
                }
            }
        } catch (IOException e) {
            W("occur IOException when get file signature", e);
        }
        return "";
    }

    /**
     * Get MD5 Code
     */
    public static String getMD5(String text) {
        try {
            byte[] byteArray = text.getBytes("utf8");
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(byteArray, 0, byteArray.length);
            return convertToHex(md.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Convert byte array to Hex string
     */
    private static String convertToHex(byte[] data) {
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < data.length; i++) {
            int halfbyte = (data[i] >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                if ((0 <= halfbyte) && (halfbyte <= 9))
                    buf.append((char) ('0' + halfbyte));
                else
                    buf.append((char) ('a' + (halfbyte - 10)));
                halfbyte = data[i] & 0x0F;
            } while (two_halfs++ < 1);
        }
        return buf.toString();
    }

    /**
     * Check whether the SD card is readable
     */
    public static boolean isSdcardReadable() {
        final String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)
                || Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /**
     * Check whether the SD card is writable
     */
    public static boolean isSdcardWritable() {
        final String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    
    /**
     * 解析二维码地址
     */
    public static HashMap<String, String> parserUri(Uri uri) {
        HashMap<String, String> parameters = new HashMap<String, String>();
        String paras[] = uri.getQuery().split("&");
        for (String s : paras) {
            if (s.indexOf("=") != -1) {
                String[] item = s.split("=");
                parameters.put(item[0], item[1]);
            } else {
                return null;
            }
        }
        return parameters;
    }
    
    /**
     * 检查默认Proxy
     */
    public static HttpHost detectProxy(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni != null 
                && ni.isAvailable() 
                && ni.getType() == ConnectivityManager.TYPE_MOBILE) {
            String proxyHost = android.net.Proxy.getDefaultHost();
            int port = android.net.Proxy.getDefaultPort();
            if (proxyHost != null) {
                return new HttpHost(proxyHost, port, "http");
            }
        }
        return null;
    }
    
  
    
    public static String submitLogs() {
        Process mLogcatProc = null;
        BufferedReader reader = null;
        try {
            mLogcatProc = Runtime.getRuntime().exec(
                    new String[] { "logcat", "-d" , "机锋市场:v"});

            reader = new BufferedReader(new InputStreamReader(mLogcatProc.getInputStream()));

            String line;
            final StringBuilder log = new StringBuilder();
            String separator = System.getProperty("line.separator");

            while ((line = reader.readLine()) != null) {
                log.append(line);
                log.append(separator);
            }
            return log.toString();

            // do whatever you want with the log. I'd recommend using Intents to
            // create an email
        } catch (IOException e) {
        } finally {
            if (reader != null)
                try {
                    reader.close();
                } catch (IOException e) {
                }
        }
        return "";
    }
    
    public static void clearCache(Context context) {
        File file = Environment.getDownloadCacheDirectory();
        File[] files = file.listFiles();
        if (files != null) {
            for (File f : files) {
                f.delete();
            }
        }
        file = context.getCacheDir();
        files = file.listFiles();
        if (files != null) {
            for (File f : files) {
                f.delete();
            }
        }
    }
    
    public static void makeEventToast(Context context, String text, boolean isLongToast) {
        
         Toast toast = null;
         if (isLongToast) {
         toast = Toast.makeText(context, "", Toast.LENGTH_LONG);
         } else {
         toast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
         }
         View v = LayoutInflater.from(context).inflate(R.layout.toast_view, null);
         TextView textView = (TextView) v.findViewById(R.id.text);
         textView.setText(text);
         toast.setView(v);
         toast.show();
        }
    
    
    public static byte[] read(InputStream inStream)
			throws Exception {	
		try {
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int len = 0;
			while ((len = inStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, len);
			}
			inStream.close();
			// ProgressDialog1.dismiss();
			return outputStream.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
	
		}
		return null;
	}
}