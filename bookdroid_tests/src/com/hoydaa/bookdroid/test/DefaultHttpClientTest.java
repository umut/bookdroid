package com.hoydaa.bookdroid.test;

import com.hoydaa.bookdroid.http.DefaultHttpClient;

import android.test.suitebuilder.annotation.MediumTest;
import junit.framework.TestCase;

/**
 * 
 * @author Umut Utkan
 *
 */
public class DefaultHttpClientTest extends TestCase {
	
	@MediumTest
	public void testContentRetrieval() {
		DefaultHttpClient client = new DefaultHttpClient(new org.apache.http.impl.client.DefaultHttpClient());
		String content = client.getUrlContent("http://www.google.com.tr");
		System.out.println(content);
	}
	
}
