package com.hoydaa.bookdroid.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.net.Uri;
import android.util.Log;

import com.hoydaa.bookdroid.http.DefaultHttpClient;
import com.hoydaa.bookdroid.http.HttpClient;
import com.hoydaa.bookdroid.http.ResponseHandler;
import com.hoydaa.bookdroid.provider.Book;

/**
 * Base book service that implements getBook and search methods.
 * 
 * @author Umut Utkan
 */
abstract public class BaseBookService implements BookService {

	private final static String LT = BaseBookService.class.getSimpleName();

	private HttpClient httpClient = new DefaultHttpClient();

	@Override
	public Book getBook(String bookId) {
		final Book book = new Book();
		final Uri.Builder builder = buildGetBookQuery(bookId);

		httpClient.retrieveWithHandler(getHttpHost() + builder.build().toString(), new ResponseHandler() {

			@Override
			public void handle(int status, InputStream in) {
				parseBook(in, book);
			}
		});

		return book;
	}

	@Override
	public List<Book> searchBook(String query, final BookSearchListener listener) {
		final ArrayList<Book> books = new ArrayList<Book>();
		final Uri.Builder builder = buildSearchBookQuery(query);

		httpClient.retrieveWithHandler(getHttpHost() + builder.build().toString(), new ResponseHandler() {

			@Override
			public void handle(int status, InputStream in) {
				parseBooks(in, books, listener);
			}
		});

		return books;
	}

	public void setHttpClient(HttpClient httpClient) {
		this.httpClient = httpClient;
	}

	private void parseBooks(InputStream in, ArrayList<Book> books, BookSearchListener listener) {
		try {
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
			XmlPullParser parser = factory.newPullParser();
			parser.setInput(new InputStreamReader(in));

			BookSearchInfo info = new BookSearchInfo();
			parseSearchInfo(parser, info);
			if (null != listener) {
				listener.onSearchInfo(info);
			}

			while (findNextBook(parser)) {
				Log.i(LT, "Found new book.");

				Book book = new Book();
				if (parseBook(parser, book)) {
					books.add(book);
					if (listener != null) {
						listener.onBookFound(book, books);
					}
				}
			}
		} catch (Exception e) {

		}
	}

	private void parseBook(InputStream in, Book book) {
		try {
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
			XmlPullParser parser = factory.newPullParser();
			parser.setInput(new InputStreamReader(in));
			if (findNextBook(parser)) {
				Log.i(LT, "Found new book.");

				parseBook(parser, book);
			}
		} catch (Exception e) {
			Log.e(LT, "Error while parsing content.", e);
		}
	}

	abstract protected String getHttpHost();

	abstract protected Uri.Builder buildSearchBookQuery(String query);

	abstract protected Uri.Builder buildGetBookQuery(String id);

	abstract protected boolean parseSearchInfo(XmlPullParser parser, BookSearchInfo info)
			throws XmlPullParserException, IOException;

	abstract protected boolean findNextBook(XmlPullParser parser) throws XmlPullParserException, IOException;

	abstract protected boolean parseBook(XmlPullParser parser, Book book) throws XmlPullParserException, IOException;

}
