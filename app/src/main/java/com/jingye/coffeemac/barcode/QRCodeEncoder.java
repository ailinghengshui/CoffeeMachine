package com.jingye.coffeemac.barcode;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.jingye.coffeemac.util.ScreenUtil;
import com.jingye.coffeemac.util.log.LogUtil;

import java.util.EnumMap;
import java.util.Map;

public class QRCodeEncoder {

	private static final int BLACK = 0xff000000;

	private static final int WHITE = 0xffffffff;

	public static Bitmap getQrCodeBitmap(Context context, String str, final int size) throws WriterException {
		return getQrCodeBitmap(context, str, size, BLACK, WHITE);
	}

	private static Bitmap getQrCodeBitmap(Context context, String str, final int size, final int fillColor, int oppositeColor)
			throws WriterException {
		Bitmap bitmap = null;
		Map<EncodeHintType, Object> hints = null;
		hints = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
		hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
		hints.put(EncodeHintType.MARGIN, 0);
		hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
		bitmap = createQRCode(str, ScreenUtil.dip2px(size), ScreenUtil.dip2px(size), hints, fillColor, oppositeColor);
		return bitmap;
	}

	private static Bitmap createQRCode(String str, int w, int h, Map<EncodeHintType, Object> hints, int fillColor,
			int oppositeColor) throws WriterException {
		BitMatrix matrix = new MultiFormatWriter().encode(str, BarcodeFormat.QR_CODE, w, h, hints);
		int width = matrix.getWidth();
		int height = matrix.getHeight();
		int[] pixels = new int[width * height];
		for (int y = 0; y < height; y++) {
			int offset = y * width;
			for (int x = 0; x < width; x++) {
				pixels[offset + x] = matrix.get(x, y) ? fillColor : oppositeColor;
			}
		}
		Bitmap bitmap = null;
		try {
			bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
			bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
		} catch (OutOfMemoryError e) {
			LogUtil.e("createQRCode", e.getMessage());
		}

		return bitmap;
	}



	/**
	 * 在二维码中间添加Logo图案
	 */
	public static Bitmap addLogo(Bitmap src, Bitmap logo) {
		if (src == null) {
			return null;
		}
		if (logo == null) {
			return src;
		}

		//获取图片的宽高
		int srcWidth = src.getWidth();
		int srcHeight = src.getHeight();
		int logoWidth = logo.getWidth();
		int logoHeight = logo.getHeight();

		if (srcWidth == 0 || srcHeight == 0) {
			return null;
		}
		if (logoWidth == 0 || logoHeight == 0) {
			return src;
		}

		//logo大小为二维码整体大小的1/5
		float scaleFactor = srcWidth * 1.0f / 5 / logoWidth;
		Bitmap bitmap = Bitmap.createBitmap(srcWidth, srcHeight, Bitmap.Config.ARGB_8888);
		try {
			Canvas canvas = new Canvas(bitmap);
			canvas.drawBitmap(src, 0, 0, null);
			canvas.scale(scaleFactor, scaleFactor, srcWidth / 2, srcHeight / 2);
			canvas.drawBitmap(logo, (srcWidth - logoWidth) / 2, (srcHeight - logoHeight) / 2, null);
			canvas.save(Canvas.ALL_SAVE_FLAG);
			canvas.restore();
		} catch (Exception e) {
			bitmap = null;
			e.getStackTrace();
		}
		return bitmap;
	}
}
