package scut.nomi.idraw.util;

import java.lang.ref.SoftReference;
import java.util.HashMap;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader.TileMode;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;

public class AsyncImageLoader {

	// SoftReference是软引用，是为了更好的为了系统回收变量
	private static HashMap<String, SoftReference<Bitmap>> imageCache;
	
	private int reflectionGap = 4;

	static {
		imageCache = new HashMap<String, SoftReference<Bitmap>>();
	}

	public AsyncImageLoader() {

	}

	public Bitmap loadBitmap(final String imageUrl,
			final ImageView imageView, final ImageCallback imageCallback) {
		if (imageCache.containsKey(imageUrl)) {
			// 从缓存中获取
			SoftReference<Bitmap> softReference = imageCache.get(imageUrl);
			Bitmap bitmap = softReference.get();
			
			if (bitmap != null) {
				return bitmap;
			}
		}
		final Handler handler = new Handler() {
			public void handleMessage(Message message) {
				imageCallback.imageLoaded((Bitmap) message.obj, imageView,
						imageUrl);
			}
		};
		// 建立新一个新的线程下载图片
		new Thread() {
			@Override
			public void run() {
				BitmapFactory.Options bitmapFactoryOptions = new BitmapFactory.Options();
				bitmapFactoryOptions.inJustDecodeBounds = false;
				bitmapFactoryOptions.inSampleSize = 2;
				
				Bitmap originalImage = BitmapFactory.decodeFile(imageUrl,
						bitmapFactoryOptions);
				
				int width = originalImage.getWidth();
				int height = originalImage.getHeight();

				Matrix matrix = new Matrix();
				matrix.preScale(1, -1);

				Bitmap reflectionImage = Bitmap.createBitmap(originalImage, 0,
						height / 2, width, height / 2, matrix, false);

				Bitmap bitmapWithReflection = Bitmap.createBitmap(width,
						(height + height / 2), Config.ARGB_8888);

				Canvas canvas = new Canvas(bitmapWithReflection);

				canvas.drawBitmap(originalImage, 0, 0, null);
				
				Paint framePaint = new Paint();

				framePaint.setColor(Color.BLUE);
				framePaint.setStyle(Style.STROKE);
				framePaint.setStrokeWidth(3);

				canvas.drawRect(0, 0, width, height, framePaint);

				canvas.drawBitmap(reflectionImage, 0, height + reflectionGap,
						null);

				Paint paint = new Paint();
				LinearGradient shader = new LinearGradient(0,
						originalImage.getHeight(), 0,
						bitmapWithReflection.getHeight() + reflectionGap,
						0xa0ffffff, 0x00ffffff, TileMode.MIRROR);

				paint.setShader(shader);

				paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));

				canvas.drawRect(0, height, width,
						bitmapWithReflection.getHeight() + reflectionGap, paint);
				
				imageCache.put(imageUrl, new SoftReference<Bitmap>(bitmapWithReflection));
				Message message = handler.obtainMessage(0, bitmapWithReflection);
				handler.sendMessage(message);
				
				originalImage.recycle();
				reflectionImage.recycle();
			}
		}.start();
		return null;
	}

	// 回调接口
	public interface ImageCallback {
		public void imageLoaded(Bitmap imageBitmap, ImageView imageView,
				String imageUrl);
	}
}