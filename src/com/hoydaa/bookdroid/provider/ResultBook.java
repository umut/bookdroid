package com.hoydaa.bookdroid.provider;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;

import com.hoydaa.bookdroid.http.DefaultHttpClient;
import com.hoydaa.bookdroid.util.FileSystemUtils;

/**
 * Book object that is used by search activity.
 * 
 * @author Umut Utkan
 */
public class ResultBook {

	private Book book;

	private Drawable thumbnailDrawable;

	public ResultBook(Book book) {
		this.book = book;
	}

	public String getId() {
		return book.getId();
	}

	public String getTitle() {
		return book.getTitle();
	}

	public String getAuthor() {
		return book.getAuthor();
	}

	public Drawable getThumbnailDrawable() {
		if (null == thumbnailDrawable) {
			Bitmap bitmap = FileSystemUtils.loadCover("thumbnail_" + book.getId());
			if (null != bitmap) {
				thumbnailDrawable = new BitmapDrawable(bitmap);
			} else {
				String url = book.getThumbnail();
				if (null != url && null == thumbnailDrawable) {
					byte[] rtn = new DefaultHttpClient().getUrlBytes(url);
					Bitmap temp = BitmapFactory.decodeByteArray(rtn, 0, rtn.length);
					FileSystemUtils.addCover("thumbnail_" + book.getId(), temp);
					thumbnailDrawable = new BitmapDrawable(temp);
				}
			}
		}
		// if (null == thumbnailDrawable) {
		// if (null != book.getThumbnail()) {
		// thumbnailDrawable = new BitmapDrawable(BitmapFactory.decodeByteArray(book.getThumbnail(), 0, book
		// .getThumbnail().length));
		// } else {
		// String url = book.getImage(ImageSize.TINY);
		// if (null != url && null == thumbnailDrawable) {
		// book.setThumbnail(new DefaultHttpClient().getUrlBytes(url));
		// thumbnailDrawable = new BitmapDrawable(BitmapFactory.decodeByteArray(book.getThumbnail(), 0, book
		// .getThumbnail().length));
		// }
		// }
		// }
		return thumbnailDrawable;
	}

	public Book getBook() {
		return book;
	}

}
