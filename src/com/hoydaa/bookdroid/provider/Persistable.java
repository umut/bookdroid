package com.hoydaa.bookdroid.provider;

import android.content.ContentProvider;
import android.content.ContentValues;

/**
 * Interface for persistables.
 * 
 * @author Umut Utkan
 */
public interface Persistable {

	/**
	 * Converts the object to {@link ContentValues} that can be saved via {@link ContentProvider}s.
	 */
	ContentValues getContentValues();

}
