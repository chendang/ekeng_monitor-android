package com.cnnet.otc.health.util;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by Administrator on 2017/5/19.
 */
public class LogFile {
    String file_name;
    FileOutputStream outputStream;
    public LogFile(Context ctx,String file_name)
    {
        try {
            File fi=new File(file_name);
            if(fi.exists())
            {
                fi.delete();
            }
            outputStream=new FileOutputStream(fi);
        }
        catch(Exception e)
        {
            outputStream=null;
            Log.e("LOG",e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    public void write(String str)
    {
        if(outputStream==null)
            return;
        try {
            outputStream.write(str.getBytes());
            outputStream.flush();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public void finish()
    {
        if(outputStream==null)
            return;
        try {
            outputStream.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public void write(byte [] datas)
    {
        String str="";
        for(byte b :datas)
        {
            str+=b+" ";
        }
        write(str);
    }

}
