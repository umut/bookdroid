package com.hoydaa.bookdroid.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.hoydaa.bookdroid.R;
import com.hoydaa.bookdroid.provider.BookManager;
import com.hoydaa.bookdroid.provider.BookdroidProvider;

/**
 * The main application activity that is responsible for listing all the books.
 * 
 * @author Meltem Atesalp
 * @author Umut Utkan
 */
public class ListBooksActivity extends ListActivity {

	private static final int ADD_NEW = Menu.FIRST;

	private static final int DELETE_ALL = Menu.FIRST + 1;

	private static final int SEARCH_BOOK = Menu.FIRST + 2;
	
	private static final int GET_CONFIRMATION = 1;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.book_list);

		final Cursor cursor = managedQuery(BookdroidProvider.CONTENT_URI, Books.PROJECTION, null, null,
				BookdroidProvider.DEFAULT_SORT_ORDER);

		((TextView) findViewById(R.id.booklist_info)).setText("There are " + cursor.getCount()
				+ " books in your inventory.");

		// Used to map book entries from the database to views
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.book_list_item, cursor, new String[] {
				Books.TITLE, Books.AUTHOR, Books.THUMBNAIL, Books.RATING }, new int[] { R.id.firstLine, R.id.secondLine,
				R.id.thumbnail, R.id.rating });
		adapter.registerDataSetObserver(new DataSetObserver() {

			@Override
			public void onChanged() {
				((TextView) findViewById(R.id.booklist_info)).setText("There are " + cursor.getCount()
						+ " books in your inventory.");
			}
			
		});
		adapter.setViewBinder(new ExtendedViewBinder());
		//BookAddapter adapter = new BookAddapter(this, cursor);
		//BookCursorAdapter adapter = new BookCursorAdapter(this, cursor);
		
		setListAdapter(adapter);
		registerForContextMenu(getListView());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		// add menu items for this activity; ADD_NEW and DELETE_ALL
		menu.add(0, ADD_NEW, 0, R.string.insert).setShortcut('3', 'a').setIcon(android.R.drawable.ic_menu_add);
		menu.add(0, DELETE_ALL, 0, R.string.delete).setIcon(android.R.drawable.ic_menu_delete);
		menu.add(0, SEARCH_BOOK, 0, android.R.string.search_go).setIcon(android.R.drawable.ic_menu_search);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case ADD_NEW:
			Intent intent = new Intent(Intent.ACTION_INSERT, BookdroidProvider.CONTENT_URI);
			// intent.putExtra(Books.ISBN, "9780330262132");
			startActivity(intent);
			return true;
		case DELETE_ALL:
			showDialog(GET_CONFIRMATION);
			return true;
		case SEARCH_BOOK:
			intent = new Intent("com.hoydaa.bookdroid.SEARCH");
			startActivity(intent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog;
		switch (id) {
		case GET_CONFIRMATION:
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Are you sure you want to remove all?").setCancelable(false).setPositiveButton("Yes",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							BookManager.deleteAll(getContentResolver());
						}
					}).setNegativeButton("No", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.cancel();
				}
			});
			dialog = builder.create();
			break;
		default:
			dialog = null;
			break;
		}
		return dialog;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.book_contextual, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info;
		try {
			info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		} catch (ClassCastException e) {
			return false;
		}
		Uri bookUri = ContentUris.withAppendedId(BookdroidProvider.CONTENT_URI, info.id);
		switch (item.getItemId()) {
		case R.id.deleteitem:
			getContentResolver().delete(bookUri, null, null);
			break;
		case R.id.edititem:
			startActivity(new Intent(Intent.ACTION_EDIT, bookUri));
			break;
		case R.id.voteitem:
			break;
		default:
			break;
		}

		return super.onContextItemSelected(item);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		startActivity(new Intent(Intent.ACTION_EDIT, ContentUris.withAppendedId(BookdroidProvider.CONTENT_URI, id)));
	}

}
