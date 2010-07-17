package com.hoydaa.bookdroid.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class FileSystemUtils {

	private final static String COVER_DIRECTORY = "com.hoydaa.bookdroid/covers";

	public static File getCoverDirectory() {
		return IOUtils.getExternalFile(COVER_DIRECTORY);
	}

	public static boolean addCover(String name, Bitmap bitmap) {
		File cacheDirectory;
		try {
			cacheDirectory = ensureCoverDirectory();
		} catch (IOException e) {
			return false;
		}

		File coverFile = new File(cacheDirectory, name);
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(coverFile);
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
		} catch (FileNotFoundException e) {
			return false;
		} finally {
			try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return true;
	}

	public static Bitmap loadCover(String id) {
		final File file = new File(getCoverDirectory(), id);
		if (file.exists()) {
			InputStream stream = null;
			try {
				stream = new FileInputStream(file);
				return BitmapFactory.decodeStream(stream, null, null);
			} catch (FileNotFoundException e) {
				// Ignore
			} finally {
				try {
					stream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	private static File ensureCoverDirectory() throws IOException {
		File cacheDirectory = getCoverDirectory();
		if (!cacheDirectory.exists()) {
			cacheDirectory.mkdirs();
			new File(cacheDirectory, ".nomedia").createNewFile();
		}
		return cacheDirectory;
	}

}
