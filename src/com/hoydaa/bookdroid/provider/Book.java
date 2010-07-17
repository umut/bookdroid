package com.hoydaa.bookdroid.provider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.database.Cursor;

import com.hoydaa.bookdroid.activity.Books;
import com.hoydaa.bookdroid.util.StringUtils;

/**
 * Represents book details retrieved from google book service.
 * 
 * @author Umut Utkan
 */
public class Book implements Persistable {

	public enum ImageSize {
		THUMNAIL, TINY, LARGE;
	}

	private List<String> isbn = new ArrayList<String>();

	private List<String> titles = new ArrayList<String>();

	private String id;

	private String description;

	private String author;

	private String publisher;

	private Integer pages;

	private String date;

	private boolean owned = false;

	private boolean read = false;

	private float rating;

	private Map<ImageSize, String> images = new HashMap<ImageSize, String>();

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public String getThumbnail() {
		return images.get(ImageSize.TINY);
	}
	
	public void setThumbnail(String url) {
		images.put(ImageSize.TINY, url);
	}
	
	public List<String> getIsbns() {
		return isbn;
	}

	public void addIsbn(String isbn) {
		this.isbn.add(isbn);
	}

	public String getIsbn() {
		return StringUtils.join(this.isbn, ", ");
	}

	public void setIsbn(String isbn) {
		this.isbn.add(isbn);
	}

	public String getIsbn10() {
		for (String isbn : this.isbn) {
			if (isbn.length() == 10) {
				return isbn;
			}
		}
		return null;
	}

	public String getIsbn13() {
		for (String isbn : this.isbn) {
			if (isbn.length() == 13) {
				return isbn;
			}
		}
		return null;
	}

	public List<String> getTitles() {
		return titles;
	}

	public void addTitle(String title) {
		this.titles.add(title);
	}

	public String getTitle() {
		return StringUtils.join(this.titles, " ");
	}

	public void setTitle(String title) {
		titles.add(title);
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getPublisher() {
		return publisher;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	public Integer getPages() {
		return pages;
	}

	public void setPages(Integer pages) {
		this.pages = pages;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public boolean isOwned() {
		return owned;
	}

	public void setOwned(boolean owned) {
		this.owned = owned;
	}

	public boolean isRead() {
		return read;
	}

	public void setRead(boolean read) {
		this.read = read;
	}

	public float getRating() {
		return rating;
	}

	public void setRating(float rating) {
		this.rating = rating;
	}

	public Map<ImageSize, String> getImages() {
		return images;
	}

	public String getImage(ImageSize size) {
		return images.get(size);
	}

	public void putImage(ImageSize size, String url) {
		images.put(size, url);
	}

	@Override
	public ContentValues getContentValues() {
		ContentValues contentValues = new ContentValues();
		contentValues.put(Books.SERVICE_ID, getId());
		contentValues.put(Books.TITLE, getTitle());
		contentValues.put(Books.ISBN, getIsbn13());
		contentValues.put(Books.COMMENTS, getDescription());
		contentValues.put(Books.READ, isRead() ? 1 : 0);
		contentValues.put(Books.OWNED, isOwned() ? 1 : 0);
		contentValues.put(Books.RATING, getRating());
		contentValues.put(Books.AUTHOR, getAuthor());
		contentValues.put(Books.PUBLISHER, getPublisher());
		contentValues.put(Books.DATE, getDate());
		contentValues.put(Books.PAGES, getPages() + "");
		contentValues.put(Books.THUMBNAIL, getThumbnail());
		return contentValues;
	}

	public static Book fromCursor(Cursor c) {
		Book book = new Book();
		book.setId(c.getString(c.getColumnIndex(Books.SERVICE_ID)));
		book.addIsbn(c.getString(c.getColumnIndex(Books.ISBN)));
		book.addTitle(c.getString(c.getColumnIndex(Books.TITLE)));
		book.setAuthor(c.getString(c.getColumnIndex(Books.AUTHOR)));
		book.setDate(c.getString(c.getColumnIndex(Books.DATE)));
		book.setDescription(c.getString(c.getColumnIndex(Books.COMMENTS)));
		int tmp = c.getInt(c.getColumnIndex(Books.OWNED));
		book.setOwned(tmp == 0 ? false : true);
		String pagesTemp = c.getString(c.getColumnIndex(Books.PAGES));
		book.setPages(pagesTemp == null ? null : Integer.parseInt(pagesTemp));
		book.setPublisher(c.getString(c.getColumnIndex(Books.PUBLISHER)));
		book.setRating(c.getFloat(c.getColumnIndex(Books.RATING)));
		tmp = c.getInt(c.getColumnIndex(Books.READ));
		book.setRead(tmp == 0 ? false : true);
		book.setThumbnail(c.getString(c.getColumnIndex(Books.THUMBNAIL)));
		
		return book;
	}

}
