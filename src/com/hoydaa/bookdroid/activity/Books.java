package com.hoydaa.bookdroid.activity;

import android.provider.BaseColumns;

/**
 * Domain object that represents the columns.
 * 
 * @author Meltem Atesalp
 */
public class Books implements BaseColumns {

	/**
	 * The title of the book
	 * <P>
	 * Type: TEXT
	 * </P>
	 */
	public static final String TITLE = "title";

	/**
	 * The isbn of the book
	 * <P>
	 * Type: TEXT
	 * </P>
	 */
	public static final String ISBN = "isbn";

	/**
	 * The comments about book
	 * <P>
	 * Type: TEXT
	 * </P>
	 */
	public static final String COMMENTS = "comments";

	/**
	 * The timestamp for when the book was created
	 * <P>
	 * Type: INTEGER (long from System.curentTimeMillis())
	 * </P>
	 */
	public static final String CREATED_DATE = "created";

	/**
	 * The timestamp for when the book was last modified
	 * <P>
	 * Type: INTEGER (long from System.curentTimeMillis())
	 * </P>
	 */
	public static final String MODIFIED_DATE = "modified";

	public static final String READ = "read";
	
	public static final String OWNED = "owned";
	
	public static final String RATING = "rating";
	
	public final static String AUTHOR = "author";
	
	public final static String PAGES = "pages";
	
	public final static String PUBLISHER = "publisher";
	
	public final static String DATE = "date";
	
	public final static String THUMBNAIL = "thumbnail";
	
	public final static String SERVICE_ID = "serviceId";
	
	/**
	 * The columns we are interested in --while displaying-- from the database.
	 */
	public static final String[] PROJECTION = new String[] { Books._ID,
			SERVICE_ID,
			TITLE,
			ISBN,
			COMMENTS,
			READ,
			OWNED,
			RATING,
			AUTHOR,
			PAGES,
			PUBLISHER,
			DATE,
			THUMBNAIL
	};

}
