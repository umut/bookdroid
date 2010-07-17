package com.hoydaa.bookdroid.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.hoydaa.bookdroid.http.DefaultHttpClient;

public class ImageUtilities {

	private final static String LT = ImageUtilities.class.getSimpleName();

	public static Bitmap load(String url) {
		final byte[] data = new DefaultHttpClient().getUrlBytes(url);
		return BitmapFactory.decodeByteArray(data, 0, data.length);
	}

}
