package com.cnnet.otc.health.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

/**
 * Created by chendang on 2016-10-26.
 */

public class SHA1Util {
    /**
     * sha1加密算法
     * @param val
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static String getSHA(String val) throws NoSuchAlgorithmException{
        MessageDigest md5 = MessageDigest.getInstance("SHA-1");
        md5.update(val.getBytes());
        byte[] m = md5.digest();//加密


        StringBuilder hex = new StringBuilder(m.length * 2);
        for (byte b : m) {
            if ((b & 0xFF) < 0x10) hex.append("0");
            hex.append(Integer.toHexString(b & 0xFF));
        }
        return hex.toString();        //16进制转换

    }
    private static String getString(byte[] b){
        StringBuffer sb = new StringBuffer();
        for(int i = 0; i < b.length; i ++){
            sb.append(b[i]);
        }


        return sb.toString();
    }

    public static void main(String[] args) {
        String base = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        try {
            System.out.println("Key ---- " + SHA1Util.getSHA(base));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
}
