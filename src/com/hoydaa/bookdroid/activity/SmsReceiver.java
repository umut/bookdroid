package com.hoydaa.bookdroid.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.gsm.SmsMessage;
import android.util.Log;

import com.hoydaa.bookdroid.provider.BookdroidProvider;

/**
 * Receives SMS messages.
 * 
 * @author Umut Utkan
 */
public class SmsReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		SmsMessage retMsgs[] = null;
		Bundle bdl = intent.getExtras();
		try {
			Object pdus[] = (Object[]) bdl.get("pdus");
			retMsgs = new SmsMessage[pdus.length];
			for (int n = 0; n < pdus.length; n++) {
				byte[] byteData = (byte[]) pdus[n];
				retMsgs[n] = SmsMessage.createFromPdu(byteData);
			}
		} catch (Exception e) {
			Log.e("GetMessages", "fail", e);
		}
		for (SmsMessage message : retMsgs) {
			String messageBody = message.getMessageBody();
			String isbnTemp = null;
			if (messageBody.startsWith("isbn:")
					&& (isbnTemp = messageBody.substring(messageBody.indexOf(':') + 1).trim()).length() > 0) {
				Intent findBook = new Intent(Intent.ACTION_INSERT, BookdroidProvider.CONTENT_URI);
				findBook.putExtra(Books.ISBN, isbnTemp);
				findBook.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(findBook);
			}
		}
	}

}
