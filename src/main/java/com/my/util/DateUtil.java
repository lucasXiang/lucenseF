package com.my.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
	public static String data2String(Date date,String pattern){
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		return sdf.format(date);
	}
}
