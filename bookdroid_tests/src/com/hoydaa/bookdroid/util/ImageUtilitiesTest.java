package com.hoydaa.bookdroid.util;

import junit.framework.TestCase;
import android.graphics.Bitmap;

public class ImageUtilitiesTest extends TestCase {
	
	public void testLoadImage() {
		Bitmap b = ImageUtilities.load("http://www.google.com.tr/intl/en_com/images/logo_plain.png");
		assertNotNull(b);
		assertEquals(276, b.getWidth());
		assertEquals(110, b.getHeight());
	}

}
