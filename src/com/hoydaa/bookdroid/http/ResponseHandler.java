package com.hoydaa.bookdroid.http;

import java.io.InputStream;

/**
 * Interface for handling response, handlers are suitable for streaming
 * otherwise you can safely use {@link HttpClient#getUrlContent(String)}.
 * 
 * @author Umut Utkan
 */
public interface ResponseHandler {

	public void handle(int status, InputStream in);

}
