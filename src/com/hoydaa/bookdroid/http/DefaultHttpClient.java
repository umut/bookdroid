package com.hoydaa.bookdroid.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;

import android.util.Log;

import com.hoydaa.bookdroid.util.IOUtils;

/**
 * Default implementation, uses commons-httpclient from android api.
 * 
 * @author Umut Utkan
 */
public class DefaultHttpClient implements HttpClient {

	private final static String LT = DefaultHttpClient.class.getSimpleName();

	private static org.apache.http.client.HttpClient client = null;

	public DefaultHttpClient() {
		DefaultHttpClient.client = new org.apache.http.impl.client.DefaultHttpClient();
	}

	public DefaultHttpClient(org.apache.http.client.HttpClient client) {
		DefaultHttpClient.client = client;
	}

	public byte[] getUrlBytes(String url) {
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		retrieveWithHandler(url, new ResponseHandler() {

			@Override
			public void handle(int status, InputStream in) {
				try {
					IOUtils.copy(in, baos);
				} catch (IOException e) {

					Log.e(LT, "io exception", e);
				}

			}
		});
		
		return baos.toByteArray();
	}
	
	public String getUrlContent(String url) {
		final StringBuffer sb = new StringBuffer("");
		retrieveWithHandler(url, new ResponseHandler() {

			@Override
			public void handle(int status, InputStream in) {
				if (status == HttpStatus.SC_OK) {
					try {
						sb.append(IOUtils.toString(in));
					} catch (IOException e) {
						Log.e(LT, "io exception", e);
					}
				}
			}
		});
		if (sb.toString().equals("")) {
			return null;
		}
		return sb.toString();
	}

	public void retrieveWithHandler(String url, ResponseHandler handler) {
		Log.i(LT, "Retrieving url content for '" + url + "'");

		HttpEntity entity = null;

		try {
			HttpGet get = new HttpGet(url);
			final HttpResponse respose = client.execute(get);
			entity = respose.getEntity();
			handler.handle(respose.getStatusLine().getStatusCode(), respose.getEntity().getContent());

			Log.i(LT, "Successfully retrieved content for '" + url + "'");
		} catch (IOException e) {
			Log.e(LT, "io exception", e);

			throw new RuntimeException(e);
		} finally {
			if (null != entity) {
				try {
					entity.consumeContent();
				} catch (IOException e) {

				}
			}
		}
	}

}
