package com.cnnet.otc.health.util;

import android.content.Context;
import android.util.Log;

import com.foxchen.ekengmonitor.R;
import com.cnnet.otc.health.comm.SysApp;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {

	private static final String TAG = "StringUtil";

	/**
	 * 判断当前字符串是否为空
	 * @param str
	 * @return
	 */
	public static boolean isEmpty(String str) {
		if(str == null || str.equals("")) {
			return true;
		}
		return false;
	}

	/**
	 * 判断不为空
	 * @param str
	 * @return
	 */
	public static boolean isNotEmpty(String str) {
		if(str != null && !str.equals("")) {
			return true;
		}
		return false;
	}

	/**
	 * 将字节数据进行哈希计算后转换成字符串
	 * @param bArray
	 * @return
	 */
	public static String byteToHexString(byte[] bArray) {
		StringBuffer sb = new StringBuffer(bArray.length);
		String sTemp;
		for (int i = 0; i < bArray.length; i++) {
			sTemp = Integer.toHexString(0xFF & bArray[i]);
			if (sTemp.length() < 2)
				sb.append(0);
			sb.append(sTemp.toUpperCase());
		}
		return sb.toString();
	}

	/**
	 * Convert hex string to byte[]
	 * @param hexString the hex string
	 * @return byte[]
	 */
	public static byte[] hexStringToBytes(String hexString) {
		if (hexString == null || hexString.equals("")) {
			return null;
		}
		hexString = hexString.toUpperCase();
		int length = hexString.length() / 2;
		char[] hexChars = hexString.toCharArray();
		byte[] d = new byte[length];
		for (int i = 0; i < length; i++) {
			int pos = i * 2;
			d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
		}
		return d;
	}

	/**
	 * Convert char to byte
	 * @param c char
	 * @return byte
	 */
	private static byte charToByte(char c) {
		return (byte) "0123456789ABCDEF".indexOf(c);
	}

	/**
	 * 判断是否为手机号
	 * @param phone
	 * @return
	 */
	public static boolean isTelephone(String phone) {
		if(isEmpty(phone)) {
			return false;
		}
		Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0-9])|(17[6，7,8]))\\d{8}$");
		Matcher m = p.matcher(phone);
		boolean isMobile = m.matches();
		Log.d(TAG, "是手机号么？  " + isMobile);
		return isMobile;
	}

	/**
	 * 获取头像的url
	 * @param path
	 * @return
	 */
	public static String getMemberPictureUrl(String path) {
		return SysApp.getSpManager().getServerUrl() + "/" + path;
	}

	/**
	 * 获取性别字段
	 * @param genderCode
	 * @param ctx
	 * @return
	 */
	public static String getGenderStr(String genderCode, Context ctx) {
		if(genderCode != null && genderCode.equals("F")) {
			return ctx.getString(R.string.female);
		}
		return ctx.getString(R.string.male);
	}

	/**
	 * 获取性别数字类型
	 * @param genderCode
	 * @param ctx
	 * @return
	 */
	public static int getGenderInt(String genderCode) {
		if(genderCode != null && genderCode.equals("F")) {
			return 0;
		}
		return 1;
	}

	/**
	 * 将int十进制转换成2位数的16进制
	 * @param integer
	 * @return
	 */
	public static String encode2Hex(int integer) {
		StringBuffer buf = new StringBuffer(2);
		if (((int) integer & 0xff) < 0x10) {
			buf.append("0");
		}
		buf.append(Long.toString((int) integer & 0xff, 16));
		return buf.toString();
	}

	/**
	 * 获取第一个数字的位置
	 * @param str
	 * @return
	 */
	public static int getFirstDigitalIndex(String str) {
		if(str != null) {
			Pattern pattern = Pattern.compile("[0-9]");
			Matcher matcher = pattern.matcher(str);
			if (matcher.find()) {
				return str.indexOf(matcher.group());
			}
		}
		return  -1;
	}

	/**
	 * 获取字符串中第一个
	 * @param str
	 * @return
	 */
	public static String getFirstFloatStr(String str) {
		if(str != null) {
			Pattern pattern = Pattern.compile("[\\d\\.]+");
			Matcher matcher = pattern.matcher(str);
			if (matcher.find()) {
				return matcher.group();  //打印
			}
		}
		return null;
	}

	/**
	 * 保留当前浮点型 scale 位小数
	 * @param scale 设置位数
	 * @param ft  值
	 * @return
	 */
	public static float getBigDecimal(int scale, float ft) {
		int roundingMode  =  4;//表示四舍五入，可以选择其他舍值方式，例如去尾，等等.
		BigDecimal bd  =   new  BigDecimal((double)ft);
		bd = bd.setScale(scale, roundingMode);
		ft = bd.floatValue();
		return ft;
	}

	/**
	 * 当前insRange为两种类型：1整形，2浮点型
	 * @param insRange
	 * @param value
	 * @param insName
	 * @return
	 */
	public static String getInsValueStr(int insRange, float value, String insName) {
		if(insRange == 1) {
			return String.valueOf((int)value) + insName;
		}
		return String.valueOf(value) + insName;
	}

	/**
	 * 获取一位小数的浮点类型
	 * @param value
	 * @return
	 */
	public static float getDecimalsOne(double value) {
		DecimalFormat df1  = new DecimalFormat("##.0");  //一位小数
		return Float.parseFloat(df1.format(value));
	}

	public static void main(String[] args) {
		/*String str = "0A4E0A4136302C3030303034302C302C342C312C312C4E2C227F808120202020203A2033303933353337220A4136302C3030303038302C302C342C312C312C4E2C2282832020202020203A2032303135203031203233220A4136302C3030303132302C302C342C312C312C4E2C2284852020202020203A2032313A3131220A4136302C3030303136302C302C342C312C312C4E2C228C8D2020202020203A20A7A8A9AA220A4136302C3030303230302C302C342C312C312C4E2C228E812020202020203A2050343433220A4136302C3030303234302C302C342C312C312C4E2C2243484F4C202020203A203C2020322E3539206D6D6F6C2F4C220A4136302C3030303238302C302C342C312C312C4E2C2248444C2043484F4C3A2020302E3339206D6D6F6C2F4C220A4136302C3030303332302C302C342C312C312C4E2C2254524947202020203A2020302E3631206D6D6F6C2F4C220A4136302C3030303336302C302C342C312C312C4E2C2243414C43204C444C3A202D2D2D2D20220A4136302C3030303430302C302C342C312C312C4E2C2254432F48444C20203A202D2D2D2D20220A50310A0A4E0A4136302C3030303034302C302C342C312C312C4E2C2220202020202020202020202020202020202020202020202020220A4136302C3030303038302C302C342C312C312C4E2C2220202020202020202020202020202020202020202020202020220A4136302C3030303132302C302C342C312C312C4E2C2220202020202020202020202020202020202020202020202020220A4136302C3030303136302C302C342C312C312C4E2C2220202020202020202020202020202020202020202020202020220A4136302C3030303230302C302C342C312C312C4E2C2220202020202020202020202020202020202020202020202020220A4136302C3030303234302C302C342C312C312C4E2C2220202020202020202020202020202020202020202020202020220A4136302C3030303238302C302C342C312C312C4E2C2220202020202020202020202020202020202020202020202020220A4136302C3030303332302C302C342C312C312C4E2C2220202020202020202020202020202020202020202020202020220A4136302C3030303336302C302C342C312C312C4E2C2220202020202020202020202020202020202020202020202020220A4136302C3030303430302C302C342C312C312C4E2C2220202020202020202020202020202020202020202020202020220A4136302C3030303434302C302C342C312C312C4E2C2220202020202020202020202020202020202020202020202020220A4136302C3030303438302C302C342C312C312C4E2C2220202020202020202020202020202020202020202020202020220A0A4E0A";
		//System.out.println(new String(hexStringToBytes(str)));
		String testString =" <  2.59 " ;
		Pattern pattern = Pattern.compile("[\\d\\.]+");
		Matcher matcher = pattern.matcher(testString);
		while(matcher.find())
		{
			System.out.println(matcher.group());  //打印
		}*/

		System.out.println("over==="+encode2Hex(170));
	}
}
