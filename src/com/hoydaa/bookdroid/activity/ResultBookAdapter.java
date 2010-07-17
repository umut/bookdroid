package com.hoydaa.bookdroid.activity;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hoydaa.bookdroid.R;
import com.hoydaa.bookdroid.provider.Book;
import com.hoydaa.bookdroid.provider.ResultBook;

/**
 * Adapter for {@link ResultBook}s.
 * 
 * @author Umut Utkan
 */
public class ResultBookAdapter extends ArrayAdapter<ResultBook> {

	private LayoutInflater _layoutInflater;

	private Drawable _defaultCover;

	public ResultBookAdapter(Context context) {
		super(context, 0);
		_layoutInflater = LayoutInflater.from(context);
		_defaultCover = new BitmapDrawable(BitmapFactory.decodeResource(context.getResources(),
				R.drawable.no_cover_thumb));
		// Toast.makeText(context, "Birt birt birt birt", 10000);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = _layoutInflater.inflate(R.layout.book_list_item, parent, false);

			holder = new ViewHolder();
			holder.cover = (ImageView) convertView.findViewById(R.id.thumbnail);
			holder.title = (TextView) convertView.findViewById(R.id.firstLine);
			holder.author = (TextView) convertView.findViewById(R.id.secondLine);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		final ResultBook book = getItem(position);
		holder.book = book.getBook();
		holder.title.setText(book.getTitle());
		holder.author.setText(book.getAuthor());

		final boolean hasCover = book.getThumbnailDrawable() != null;
		holder.cover.setImageDrawable(hasCover ? book.getThumbnailDrawable() : _defaultCover);

		return convertView;
	}

	private static class ViewHolder {
		ImageView cover;
		TextView title;
		TextView author;
		Book book;
	}

}
