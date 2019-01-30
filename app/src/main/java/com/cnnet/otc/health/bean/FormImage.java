package com.cnnet.otc.health.bean;

import android.graphics.Bitmap;

import com.cnnet.otc.health.util.StringUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by SZ512 on 2016/1/11.
 */
public class  FormImage {
    //参数的名称
    private String mName = "upload" ;
    //文件名
    private String mFileName = "head.png" ;
    //文件的 mime，需要根据文档查询
    private String mMime = "image/png";
    //需要上传的图片资源，因为这里测试为了方便起见，直接把 bigmap 传进来，真正在项目中一般不会这般做，而是把图片的路径传过来，在这里对图片进行二进制转换
    private Bitmap mBitmap ;

    private String filePath;

    public FormImage(Bitmap mBitmap) {
        this.mBitmap = mBitmap;
    }

    public FormImage(Bitmap mBitmap, String mMime) {
        this.mBitmap = mBitmap;
        this.mMime = mMime;
    }

    public FormImage(String filePath) {
        this.filePath = filePath;
        if(filePath != null) {
            this.mFileName = filePath.substring(filePath.lastIndexOf("\\") + 1, filePath.length());
        }
    }

    public FormImage(String filePath, String mMime) {
        this.filePath = filePath;
        this.mMime = mMime;
        if(filePath != null) {
            this.mFileName = filePath.substring(filePath.lastIndexOf("\\") + 1, filePath.length());
        }
    }

    public String getName() {
        return mName;
    }

    public String getFileName() {
        return mFileName;
    }
    //对图片进行二进制转换
    private byte[] getImageBytes() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream() ;
        mBitmap.compress(Bitmap.CompressFormat.JPEG,80,bos) ;
        return bos.toByteArray();
    }
    //因为我知道是 png 文件，所以直接根据文档查的
    public String getMime() {
        return mMime;
    }

    public String getFilePath() {
        return filePath;
    }

    private byte[] getFileBytes() {
        //使用InputStream从文件中读取数据，在已知文件大小的情况下，建立合适的存储字节数组
        File f = new File(filePath);
        InputStream in = null;
        byte b[] = null;
        try {
            in = new FileInputStream(f);
            b = new byte[(int)f.length()];     //创建合适文件大小的数组
            in.read(b);    //读取文件中的内容到b[]数组
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return b;
    }

    public byte[] getBytes() {
        if(StringUtil.isNotEmpty(filePath)) {
            return getFileBytes();
        } else {
            return getImageBytes();
        }
    }
}