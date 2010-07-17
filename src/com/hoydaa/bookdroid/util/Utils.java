package com.hoydaa.bookdroid.util;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;

import com.hoydaa.bookdroid.provider.Book;

public class Utils {

	public static boolean tweet(Activity activity, Book book) {
		String message = "Finished reading '" + book.getTitle()
				+ "', it is nice. Testing android twitter intent, sorry for buzzing:)";
		// Boolean to show if we succeeded or not
		// we assume we did until proven otherwise.
		boolean success = true;

		// Try twidroidpro first
		Intent intent = new Intent("com.twidroidpro.SendTweet");
		intent.putExtra("com.twidroidpro.extra.MESSAGE", message);
		intent.setType("application/twitter");
		try {
			activity.startActivityForResult(intent, 1);
		} catch (ActivityNotFoundException e) {
			success = false;
		}

		// Then twidroid if we failed
		if (!success) {
			success = true;
			intent = new Intent("com.twidroid.SendTweet");
			intent.putExtra("com.twidroid.extra.MESSAGE", message);
			intent.setType("application/twitter");
			try {
				activity.startActivityForResult(intent, 1);
			} catch (ActivityNotFoundException e) {
				success = false;
			}
		}

		// Then send general intent if we failed again
		if (!success) {
			success = true;
			try {
				intent = new Intent(Intent.ACTION_SEND);
				intent.putExtra(Intent.EXTRA_TEXT, message);
				intent.setType("application/twitter");
				activity.startActivity(Intent.createChooser(intent, null));
			} catch (ActivityNotFoundException e) {
				success = false;
			}
		}
		// return indicating if we were successful in bringing up an intent
		// of some description
		return success;
	}
	
}
