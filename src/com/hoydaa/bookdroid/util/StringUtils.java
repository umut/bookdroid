package com.hoydaa.bookdroid.util;

import java.util.Collection;

public class StringUtils {

	public static String join(Collection c, String sep) {
		StringBuffer sb = new StringBuffer();
		for(Object o : c) {
			sb.append(sep);
			sb.append(o);
		}
		String rtn = sb.toString();
		if(c.size() > 0) {
			rtn = rtn.substring(sep.length());
		}
		return rtn;
	}
	
	public static boolean hasText(CharSequence str) {
		if (!hasLength(str)) {
			return false;
		}
		int strLen = str.length();
		for (int i = 0; i < strLen; i++) {
			if (!Character.isWhitespace(str.charAt(i))) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean hasLength(String str) {
		return hasLength((CharSequence) str);
	}
	
	public static boolean hasLength(CharSequence str) {
		return (str != null && str.length() > 0);
	}
	
}
