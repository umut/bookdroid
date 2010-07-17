package com.hoydaa.bookdroid.provider;

import java.util.HashMap;

import com.hoydaa.bookdroid.activity.Books;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

/**
 * Provides access to a database of books. Each book has an isbn, a title, a
 * comment, a creation date and a modified data.
 * 
 * @author Meltem Atesalp
 * @author Umut Utkan
 */
public class BookdroidProvider extends ContentProvider {

	/**
	 * Authority part of the URI
	 */
	public static final String AUTHORITY = "com.hoydaa.bookdroid.provider.Bookdroid";

	/**
	 * The content:// style URL for this table
	 */
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/books");

	/**
	 * The MIME type of {@link #CONTENT_URI} providing a directory of notes.
	 */
	public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.hoydaa.book";

	/**
	 * The MIME type of a {@link #CONTENT_URI} sub-directory of a single note.
	 */
	public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.hoydaa.book";

	/**
	 * The default sort order for this table
	 */
	public static final String DEFAULT_SORT_ORDER = "modified DESC";

	/**
	 * Tag for log messages
	 */
	private static final String LOG_TAG = "BookdroidProvider";

	private static final String DATABASE_NAME = "bookdroid.db";
	private static final int DATABASE_VERSION = 2;
	private static final String BOOKS_TABLE_NAME = "books";

	private static HashMap<String, String> sBooksProjectionMap;

	private static final int BOOKS = 1;
	private static final int BOOK_ID = 2;

	private static final UriMatcher sUriMatcher;

	/**
	 * This class helps open, create, and upgrade the database file.
	 */
	private static class DatabaseHelper extends SQLiteOpenHelper {

		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE " + BOOKS_TABLE_NAME + " (" + 
					Books._ID + " INTEGER PRIMARY KEY," + 
					Books.SERVICE_ID + " STRING," + 
					Books.TITLE + " TEXT," + 
					Books.ISBN + " TEXT," + 
					Books.COMMENTS + " TEXT," + 
					Books.CREATED_DATE + " INTEGER," + 
					Books.MODIFIED_DATE + " INTEGER," + 
					Books.READ + " INTEGER, " + 
					Books.OWNED + " INTEGER, " + 
					Books.RATING + " FLOAT, " + 
					Books.AUTHOR + " STRING, " + 
					Books.PUBLISHER + " STRING, " +
					Books.PAGES + " STRING, " + 
					Books.DATE + " STRING, " +
					Books.THUMBNAIL + " STRING);");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(LOG_TAG, "Upgrading database from version " + oldVersion + " to " + newVersion
					+ ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS " + BOOKS_TABLE_NAME);
			onCreate(db);
		}
	}

	private DatabaseHelper mOpenHelper;

	@Override
	public boolean onCreate() {
		mOpenHelper = new DatabaseHelper(getContext());
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

		switch (sUriMatcher.match(uri)) {
		case BOOKS:
			qb.setTables(BOOKS_TABLE_NAME);
			qb.setProjectionMap(sBooksProjectionMap);
			break;

		case BOOK_ID:
			qb.setTables(BOOKS_TABLE_NAME);
			qb.setProjectionMap(sBooksProjectionMap);
			qb.appendWhere(Books._ID + "=" + uri.getPathSegments().get(1));
			break;

		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		// If no sort order is specified use the default
		String orderBy;
		if (TextUtils.isEmpty(sortOrder)) {
			orderBy = BookdroidProvider.DEFAULT_SORT_ORDER;
		} else {
			orderBy = sortOrder;
		}

		// Get the database and run the query
		SQLiteDatabase db = mOpenHelper.getReadableDatabase();
		Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, orderBy);

		// Tell the cursor what uri to watch, so it knows when its source data
		// changes
		c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;
	}

	@Override
	public String getType(Uri uri) {
		switch (sUriMatcher.match(uri)) {
		case BOOKS:
			return CONTENT_TYPE;

		case BOOK_ID:
			return CONTENT_ITEM_TYPE;

		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues initialValues) {
		// Validate the requested uri
		if (sUriMatcher.match(uri) != BOOKS) {
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		ContentValues values;
		if (initialValues != null) {
			values = new ContentValues(initialValues);
		} else {
			values = new ContentValues();
		}

		Long now = Long.valueOf(System.currentTimeMillis());

		// Make sure that the fields are all set
		if (values.containsKey(Books.CREATED_DATE) == false) {
			values.put(Books.CREATED_DATE, now);
		}

		if (values.containsKey(Books.MODIFIED_DATE) == false) {
			values.put(Books.MODIFIED_DATE, now);
		}

		if (values.containsKey(Books.TITLE) == false) {
			Resources r = Resources.getSystem();
			values.put(Books.TITLE, r.getString(android.R.string.untitled));
		}

		if (values.containsKey(Books.ISBN) == false) {
			values.put(Books.ISBN, "");
		}

		if (values.containsKey(Books.COMMENTS) == false) {
			values.put(Books.COMMENTS, "");
		}

		if (values.containsKey(Books.READ) == false) {
			values.put(Books.READ, false);
		}

		if (values.containsKey(Books.OWNED) == false) {
			values.put(Books.OWNED, false);
		}
		
		if (values.containsKey(Books.RATING) == false) {
			values.put(Books.RATING, .0);
		}
		
		if(values.containsKey(Books.AUTHOR) == false) {
			values.put(Books.AUTHOR, "");
		}
		
		if(values.containsKey(Books.PUBLISHER) == false) {
			values.put(Books.PUBLISHER, "");
		}
		
		if(values.containsKey(Books.PAGES) == false) {
			values.put(Books.PAGES, "");
		}
		
		if(values.containsKey(Books.DATE) == false) {
			values.put(Books.DATE, "");
		}
		
//		if(values.containsKey(Books.THUMBNAIL) == false) {
//			values.put(Books.THUMBNAIL, (byte[]) null);
//		}

		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		long rowId = db.insert(BOOKS_TABLE_NAME, Books.ISBN, values);
		if (rowId > 0) {
			Uri noteUri = ContentUris.withAppendedId(CONTENT_URI, rowId);
			getContext().getContentResolver().notifyChange(noteUri, null);
			return noteUri;
		}

		throw new SQLException("Failed to insert row into " + uri);
	}

	@Override
	public int delete(Uri uri, String where, String[] whereArgs) {
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		int count;
		switch (sUriMatcher.match(uri)) {
		case BOOKS:
			count = db.delete(BOOKS_TABLE_NAME, where, whereArgs);
			break;

		case BOOK_ID:
			String noteId = uri.getPathSegments().get(1);
			count = db.delete(BOOKS_TABLE_NAME, Books._ID + "=" + noteId
					+ (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
			break;

		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	@Override
	public int update(Uri uri, ContentValues values, String where, String[] whereArgs) {
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		int count;
		switch (sUriMatcher.match(uri)) {
		case BOOKS:
			count = db.update(BOOKS_TABLE_NAME, values, where, whereArgs);
			break;

		case BOOK_ID:
			String noteId = uri.getPathSegments().get(1);
			count = db.update(BOOKS_TABLE_NAME, values, Books._ID + "=" + noteId
					+ (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
			break;

		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	static {
		// matches the URI in order to understand what kind of operation is
		// being requested a directory of books or a book
		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		sUriMatcher.addURI(AUTHORITY, "books", BOOKS);
		sUriMatcher.addURI(AUTHORITY, "books/#", BOOK_ID);

		// The projection map maps from column names that the caller passes into
		// query to database column names
		sBooksProjectionMap = new HashMap<String, String>();
		sBooksProjectionMap.put(Books._ID, Books._ID);
		sBooksProjectionMap.put(Books.SERVICE_ID, Books.SERVICE_ID);
		sBooksProjectionMap.put(Books.TITLE, Books.TITLE);
		sBooksProjectionMap.put(Books.ISBN, Books.ISBN);
		sBooksProjectionMap.put(Books.COMMENTS, Books.COMMENTS);
		sBooksProjectionMap.put(Books.CREATED_DATE, Books.CREATED_DATE);
		sBooksProjectionMap.put(Books.MODIFIED_DATE, Books.MODIFIED_DATE);
		sBooksProjectionMap.put(Books.READ, Books.READ);
		sBooksProjectionMap.put(Books.OWNED, Books.OWNED);
		sBooksProjectionMap.put(Books.RATING, Books.RATING);
		sBooksProjectionMap.put(Books.AUTHOR, Books.AUTHOR);
		sBooksProjectionMap.put(Books.PAGES, Books.PAGES);
		sBooksProjectionMap.put(Books.PUBLISHER, Books.PUBLISHER);
		sBooksProjectionMap.put(Books.DATE, Books.DATE);
		sBooksProjectionMap.put(Books.THUMBNAIL, Books.THUMBNAIL);
	}

}
