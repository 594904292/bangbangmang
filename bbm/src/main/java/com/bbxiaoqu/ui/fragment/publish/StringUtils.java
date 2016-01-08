/*******************************************************************************
 *
 * Copyright (c) Weaver Info Tech Co. Ltd
 *
 * StringUtils
 *
 * app.util.StringUtils.java
 * TODO: File description or class description.
 *
 * @author: qixiao
 * @since:  Jul 29, 2013
 * @version: 1.0.0
 *
 * @changeLogs:
 *     1.0.0: First created this class.
 *
 ******************************************************************************/
package com.bbxiaoqu.ui.fragment.publish;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author gao-chun
 *
 */
public class StringUtils {

    /**
     * 把字符串转化为JSONArray
     * @param json
     * @return 相应的JSONArray,如果不成功则返回null
     */
    public static JSONArray toJsonArray(String json) {
        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonArray;
    }

    /**
     * 把字符串转化为JSONObject
     * @param json
     * @return  相应的JSONObject,如果不成功则返回null
     */
    public static JSONObject toJsonObject(String json) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    /**
     * 生成8位16进制的缓存因子：规则的8位哈希码，不足前面补零
     * @param string
     * @return
     */
    public static String toRegularHashCode(String string) {
        final String hexHashCode = Integer.toHexString(string.hashCode());
        final StringBuilder stringBuilder = new StringBuilder(hexHashCode);
        while(stringBuilder.length() < 8){
            stringBuilder.insert(0, '0');
        }
        return stringBuilder.toString();
    }

    /**
     * 根据星期编码得到星期几
     * @param str
     * @return 星期
     * */
    public static String getWeekString (String week) {
        String weekStr = null;
        if (week.equals("1")) {
            weekStr = "星期一";
        } else if (week.equals("2")) {
            weekStr = "星期二";
        } else if (week.equals("3")) {
            weekStr = "星期三";
        } else if (week.equals("4")) {
            weekStr = "星期四";
        } else if (week.equals("5")) {
            weekStr = "星期五";
        } else if (week.equals("6")) {
            weekStr = "星期六";
        } else if (week.equals("7")) {
            weekStr = "星期日";
        }
        return weekStr;
    }

    /**
     * 根据节次编码得到第几节
     * @param str
     * @return 第几节
     * */
    public static String getIndexString (String index) {
        String indexStr = null;
        if (index.equals("1")) {
            indexStr = "第1节";
        } else if (index.equals("2")) {
            indexStr = "第2节";
        } else if (index.equals("3")) {
            indexStr = "第3节";
        } else if (index.equals("4")) {
            indexStr = "第4节";
        } else if (index.equals("5")) {
            indexStr = "第5节";
        } else if (index.equals("6")) {
            indexStr = "第6节";
        } else if (index.equals("7")) {
            indexStr = "第7节";
        } else if (index.equals("8")) {
            indexStr = "第8节";
        }
        return indexStr;
    }

    /**
     * 列表图片
     * 拆分图片名称，添加区分图片大小的字符串
     *
     * */
    public static String getListPicSize (String imagePath) {
        String front = imagePath.substring(0, imagePath.lastIndexOf("."));
        String suffix = imagePath.substring(imagePath.lastIndexOf("."), imagePath.length());
        String path = front + "_ldpi" + suffix;
        return path;
    }

    /**
     * 为保护用户隐私，对电话号码进行处理
     * @param mobile
     * @return
     */
    public static String maskMobile(String mobile) {
        final int len = mobile.length();
        final String maskedMobile = mobile.substring(0, 3) + "****" + mobile.substring(7, len);
        return maskedMobile;
    }

    /**
     * 为保护用户隐私，对电子邮件进行处理
     * @param email
     * @return
     */
    public static String maskEmail(String email) {
        final String[] parts = email.split("@");
        final String username = parts[0];
        final String hostname = parts[1];
        final int len = username.length();
        final StringBuffer buffer = new StringBuffer();


        if (len <= 3) {
            // 少于三个字符的，均显示三个星号
            buffer.append("***");
        } else {
            // 其它，首尾明码显示，中间显示星号
            buffer.append(username.substring(0, 1));
            for (int i = 0; i < len - 2; i++) {
                buffer.append("*");
            }
            buffer.append(username.substring(len - 1, len));
        }

        buffer.append("@");
        buffer.append(hostname);

        return buffer.toString();
    }
}
