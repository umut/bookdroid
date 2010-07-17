package com.hoydaa.bookdroid.provider;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;

/**
 * {@link Drawable} wrapper for {@link Bitmap}s.
 * 
 * @author Umut Utkan
 *
 */
public class BitmapDrawable extends Drawable {

	private Bitmap bitmap;

	public BitmapDrawable(Bitmap bitmap) {
		this.bitmap = bitmap;
	}

	@Override
	public void draw(Canvas canvas) {
		canvas.drawBitmap(bitmap, 0.0f, 0.0f, null);
	}

	@Override
	public int getOpacity() {
		return PixelFormat.TRANSLUCENT;
	}

	@Override
	public void setAlpha(int alpha) {
	}

	@Override
	public void setColorFilter(ColorFilter cf) {
	}

	@Override
	public int getIntrinsicWidth() {
		return bitmap.getWidth();
	}

	@Override
	public int getIntrinsicHeight() {
		return bitmap.getHeight();
	}

	@Override
	public int getMinimumWidth() {
		return bitmap.getWidth();
	}

	@Override
	public int getMinimumHeight() {
		return bitmap.getHeight();
	}

	public Bitmap getBitmap() {
		return bitmap;
	}

}
