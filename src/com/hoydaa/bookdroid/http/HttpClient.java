package com.hoydaa.bookdroid.http;

/**
 * Interface for retrieving content of urls.
 * 
 * @author Umut Utkan
 */
public interface HttpClient {

	public byte[] getUrlBytes(String url);
	
	/**
	 * Get the content of the url as string.
	 */
	String getUrlContent(String url);

	/**
	 * Retrieve content with handler.
	 */
	void retrieveWithHandler(String url, ResponseHandler handler);

}
