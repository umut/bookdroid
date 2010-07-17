package com.hoydaa.bookdroid.provider;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;

import com.hoydaa.bookdroid.activity.Books;

/**
 * Book manager to use content resolver accordingly to insert/update, retrieve,
 * delete and delete all book(s);
 * 
 * @author Umut Utkan
 */
public class BookManager {

	public static Book retrieveBook(ContentResolver resolver, Uri id) {
		Cursor c = resolver.query(id, Books.PROJECTION, null, null, null);
		if (c.moveToFirst()) {
			return Book.fromCursor(c);
		}
		return null;
	}
	
	public static Uri addBook(ContentResolver resolver, Book book) {
		return resolver.insert(BookdroidProvider.CONTENT_URI, book.getContentValues());
	}
	
	public static int updateBook(ContentResolver resolver, Uri id, Book book) {
		return resolver.update(id, book.getContentValues(), null, null);
	}

	public static Book retrieveBook(ContentResolver resolver, Long id) {
		return retrieveBook(resolver, ContentUris.withAppendedId(BookdroidProvider.CONTENT_URI, id));
	}

	public static int deleteBook(ContentResolver resolver, Long id) {
		int result = resolver.delete(ContentUris.withAppendedId(BookdroidProvider.CONTENT_URI, id), null, null);
		return result;
	}

	public static int deleteAll(ContentResolver resolver) {
		int result = resolver.delete(BookdroidProvider.CONTENT_URI, null, null);
		return result;
	}

}
