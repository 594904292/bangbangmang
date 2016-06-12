package com.bbxiaoqu.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by dzy on 2016/6/6.
 */
public class ImageUtil {

    public String compressBmpToFile(Context content, String filePath, int pos){
        SimpleDateFormat bartDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date();
        File afile =new File(filePath);
        String fileName=afile.getName();
        String[] token = fileName.split("\\.");
        String ext = token[1];

        String tofilePath="";
        if(!isFolderExists(content.getFilesDir().getAbsolutePath()+"/temp/"))
        {
            //判断的时候已经创建
        }
        tofilePath =content.getFilesDir().getAbsolutePath()+"/temp/"+ bartDateFormat.format(date)+String.valueOf(pos)+"."+ext;
        Bitmap bmp= BitmapFactory.decodeFile(filePath);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int options = 80;//个人喜欢从80开始,
        bmp.compress(Bitmap.CompressFormat.JPEG, options, baos);
        while (baos.toByteArray().length / 1024 > 100) {
            baos.reset();
            options -= 10;
            bmp.compress(Bitmap.CompressFormat.JPEG, options, baos);
        }
        try {
            FileOutputStream fos = new FileOutputStream(tofilePath);
            fos.write(baos.toByteArray());
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tofilePath;
    }

    public  boolean isFolderExists(String strFolder) {
        File file = new File(strFolder);
        if (!file.exists()) {
            if (file.mkdirs()) {
                return true;
            } else {
                return false;

            }
        }
        return true;

    }


}
