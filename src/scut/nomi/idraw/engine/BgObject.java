package scut.nomi.idraw.engine;

import java.util.Timer;
import java.util.TimerTask;

import scut.nomi.idraw.view.DrawView;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.util.Log;

/**
 * @author Nomi
 * @description ±≥æ∞Õº∑≈÷√≤„
 */
public class BgObject extends BaseObject {

	private Bitmap tmpBitmap;
	private Timer clearTimer;
	private float clearY = 0f;
	private Canvas bmCanvas;
	
	public BgObject(Context context, float height, float width) {
		super(context, 0, 0, height, width);
		TAG = "BgObject";
		try {
			bitmap = Bitmap.createBitmap((int) width, (int) height,
					Config.ARGB_8888);
			Paint paint = new Paint();
			paint.setColor(Color.WHITE);
			paint.setStyle(Style.FILL);
			paint.setAntiAlias(true);
			paint.setStrokeWidth(5);
			bmCanvas = new Canvas(bitmap);
			bmCanvas.drawRect(0, 0, width, height, paint);
		} catch (Exception e) {
			//errorLog("Cannot Read");
			errorLog(e.toString());
			canWork = false;
		}
	}
	
	public BgObject(Context context, String path, float height, float width) {
		super(context, 0, 0, height, width);
		TAG = "BgObject";
		try {
			tmpBitmap = BitmapFactory.decodeFile(path);
			Matrix matrix = new Matrix();
			float wid = tmpBitmap.getWidth(), hgt = tmpBitmap.getHeight();
			if (tmpBitmap.getHeight() < tmpBitmap.getWidth()) {
				matrix.postRotate(90);
				wid = hgt;
				hgt = tmpBitmap.getWidth();
			}
			if (wid > width) {
				matrix.postScale(width / wid, height / hgt);
			}
			bitmap = Bitmap.createBitmap(tmpBitmap, 0, 0, tmpBitmap.getWidth(), tmpBitmap.getHeight(), matrix, true);
			//tmpBitmap.recycle();
		} catch (Exception e) {
			errorLog("Cannot Read");
			canWork = false;
		}
	}

	@Override
	public boolean draw(Canvas canvas, Paint paint) {
		try {
			canvas.drawBitmap(bitmap, x, y, null);
			/*
			Rect rect1 = new Rect((int) (0.1*width), (int) (0.7*height), (int) (0.3*width), (int) (0.9*height));
			canvas.drawBitmap(bitmap, new Rect(0, 0, (int) width, (int) height), rect1, null);
			if (tmpBitmap != null && !tmpBitmap.isRecycled()) {
				Rect rect2 = new Rect((int) (0.6*width), (int) (0.7*height), (int) (0.8*width), (int) (0.9*height));
				canvas.drawBitmap(tmpBitmap, new Rect(0, 0, (int) width, (int) height), rect2, null);
			}
			*/
			return true;
		} catch (Exception e) {
			//errorLog("Cannot Draw");
		}
		return false;
	}
	
	public void setBgTmp(Bitmap bitmap) {
		tmpBitmap = this.bitmap.copy(this.bitmap.getConfig(), this.bitmap.isMutable());
		this.bitmap = bitmap.copy(bitmap.getConfig(), bitmap.isMutable());
		bitmap.recycle();
		bmCanvas = new Canvas(this.bitmap);
	}
	
	public void bgClear() {
		clearTimer = new Timer();
		clearTimer.schedule(new ClearTask(), 0, 33);
	}
	
	class ClearTask extends TimerTask {
		@Override
		public void run() {
			if (clearY <= height) {
				clearY += 10;
				Log.e(TAG,"clearing");
				Rect rect = new Rect(0, 0, (int) width, (int) clearY);
				bmCanvas.drawBitmap(tmpBitmap, rect, rect, null);
			} else {
				Log.e(TAG,"cleared");
				clearY = 0f;
				//tmpBitmap.recycle();
				DrawView.clearing = false;
				clearTimer.cancel();
			}
		}
	}
	
	@Override
	public void recycle() {
		super.recycle();
		try {
			clearTimer.cancel();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
}
