package com.hoydaa.bookdroid.activity;

import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.SimpleCursorAdapter.ViewBinder;

import com.hoydaa.bookdroid.provider.Book;
import com.hoydaa.bookdroid.provider.ResultBook;

/**
 * New {@link ViewBinder} that has the ability to bind blobs to {@link ImageView}s.
 * 
 * @author Umut Utkan
 */
public class ExtendedViewBinder implements SimpleCursorAdapter.ViewBinder {

	@Override
	public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
		// bind string
		if (view instanceof TextView) {
			((TextView) view).setText(cursor.getString(columnIndex));
			return true;
		}
		Book book = Book.fromCursor(cursor);
		// bind thumbnail
		if (view instanceof ImageView) {
			Drawable d = new ResultBook(book).getThumbnailDrawable();
			if (null != d) {
				((ImageView) view).setImageDrawable(d);
			}
			return true;
		}
		// bind rating bar
		if(view instanceof RatingBar) {
			((RatingBar) view).setRating(book.getRating());
			return true;
		}
		return false;
	}

}
