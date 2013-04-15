package scut.nomi.idraw.engine;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

/**
 * @author Nomi
 * @description ª≠Õºœﬂ∑≈÷√≤„
 */
public class LineObject extends BaseObject {

	private float prev_x = 0, prev_y = 0; 
	private int color, r, g, b, to_r, to_g, to_b;
	private boolean randomColor = true;
	private Timer mTimer;
	private Canvas bmCanvas = null;
	private Random random = null;
	
	public LineObject(Context context, float height, float width) {
		super(context, 0, 0, height, width);
		TAG = "LineObject";
		try {
			bitmap = Bitmap.createBitmap((int) width, (int) height,
					Config.ARGB_8888);
			bmCanvas = new Canvas(bitmap);
		} catch (Exception e) {
			errorLog("Cannot Read");
			canWork = false;
		}
		random = new Random();
		
		mTimer = new Timer();
		mTimer.schedule(new ColorChangeTask(), 0, 1500);
	}

	class ColorChangeTask extends TimerTask {
		@Override
		public void run() {
			if (randomColor) {
				randomColor();
			}
		}
	};
	
	@Override
	public boolean draw(Canvas canvas, Paint paint) {
		try {
			canvas.drawBitmap(bitmap, x, y, null);
			return true;
		} catch (Exception e) {
			errorLog("Cannot Draw");
		}
		return false;
	}

	public void drawLine(Paint paint, float x, float y) {
		if (bmCanvas == null) {
			return;
		}
		
		if (randomColor) {
			setRandomPaint(paint);
		}
		
		bmCanvas.drawLine(prev_x, prev_y, x, y, paint);
		prev_x = x;
		prev_y = y;
	}
	
	public void drawPoint(Paint paint, float x, float y) {
		if (bmCanvas == null) {
			return;
		}
		
		if (randomColor) {
			setRandomPaint(paint);
		}
		
		bmCanvas.drawPoint(x, y, paint);
		prev_x = x;
		prev_y = y;
	}
	
	private void setRandomPaint(Paint paint) {
		r = rgbChange(r, to_r);
		g = rgbChange(g, to_g);
		b = rgbChange(b, to_b);

		color = Color.argb(255, r, g, b);
		paint.setColor(color);
	}
	
	public void updatePoint(float x, float y) {
		prev_x = x;
		prev_y = y;
		
		Log.e("update", prev_x+", "+prev_y);
	}
	
	public void randomColor() {
		to_r = Math.abs(random.nextInt()) % 255;
		to_g = Math.abs(random.nextInt()) % 255;
		to_b = Math.abs(random.nextInt()) % 255;
	}
	
	private int rgbChange(int rgb, int to_rgb) {
		if (rgb < to_rgb) {
			rgb += 5;
		} else {
			rgb -= 5;
		}
		return rgb;
	}
	
	public void setRandomColorOff() {
		randomColor = false;
	}
	
	public void setRandomColorOn() {
		randomColor = true;
	}
}
