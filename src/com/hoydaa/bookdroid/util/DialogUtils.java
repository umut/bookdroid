package com.hoydaa.bookdroid.util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;

public class DialogUtils {

	public static Dialog createNeutralDialog(Context context, String title, String text) {
		return new AlertDialog.Builder(context).setTitle(title).setMessage(text)
		.setCancelable(false).setNeutralButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub

			}
		}).create();
	}
	
}
