package scut.nomi.idraw.view;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
/**
 * 
 * @author nomi
 * @description 这个View是旧版实现 已被抛弃 留下当做备份
 */
public class HandwritingView extends SurfaceView implements Callback {

	private final int ACTION_NULL = 0;
	private final int DRAW_MOVE = 1;
	private final int DRAW_POINT = 2;
	private final int CLEAR = 3;

	private SurfaceHolder sfh;
	private Paint paint = null;
	private Random random = null;
	private float last_x, last_y, x, y, clear_y;
	private int flag = ACTION_NULL, backgroundColor;
	private Bitmap bitmap = null, initBitmap = null;
	private Canvas bmCanvas = null, canvas;
	private int color, r, g, b, to_r, to_g, to_b, heightSreecn, widthScreen;
	private Timer mTimer, clearTimer;
	private Object object;
	private final int TIME = 100;
	private String path = "";
	private DrawThread th;

	public HandwritingView(Context context, AttributeSet attrs) {
		this(context);
	}

	public HandwritingView(Context context) {
		super(context);
		clear_y = 0;
		backgroundColor = Color.WHITE;
		sfh = this.getHolder();
		sfh.addCallback(this);
		paint = new Paint();
		paint.setColor(backgroundColor);
		paint.setStyle(Style.FILL);
		paint.setAntiAlias(true);
		paint.setStrokeWidth(5);
		random = new Random();
		object = new Object();
		th = new DrawThread(context);
	}

	class ColorChangeTask extends TimerTask {
		@Override
		public void run() {
			to_r = Math.abs(random.nextInt()) % 255;
			to_g = Math.abs(random.nextInt()) % 255;
			to_b = Math.abs(random.nextInt()) % 255;
			// Log.e("ColorChangeTask", "r:" + to_r + "   g:" + to_g + "   b:"
			// + to_b);
		}
	};

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (flag == CLEAR) {
			return super.onTouchEvent(event);
		}
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			x = event.getX();
			y = event.getY();
			// Log.e("Touch", x+"  "+y);
			flag = DRAW_POINT;
			bitmapDraw();
			last_x = x;
			last_y = y;
			return true;
		} else if (event.getAction() == MotionEvent.ACTION_MOVE) {
			x = event.getX();
			y = event.getY();
			// Log.e("Touch", x+"  "+y);
			flag = DRAW_MOVE;
			bitmapDraw();
			last_x = x;
			last_y = y;
			return true;
		}
		flag = ACTION_NULL;
		synchronized (object) {// 备注2
			try {
				object.wait(TIME);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return super.onTouchEvent(event);
	}

	private int rgbChange(int rgb, int to_rgb) {
		if (rgb < to_rgb) {
			rgb += 5;
		} else {
			rgb -= 5;
		}
		return rgb;
	}

	private void bitmapDraw() {

		if (flag != ACTION_NULL) {
			r = rgbChange(r, to_r);
			g = rgbChange(g, to_g);
			b = rgbChange(b, to_b);

			color = Color.argb(255, r, g, b);
			paint.setColor(color);

			bmCanvas.save(Canvas.ALL_SAVE_FLAG);
			if (flag == DRAW_MOVE) {
				bmCanvas.drawLine(last_x, last_y, x, y, paint);
				// Log.e("bmCanvas", "drawLine");
			} else if (flag == DRAW_POINT) {
				bmCanvas.drawPoint(x, y, paint);
				// Log.e("bmCanvas", "drawPoint");
			} else if (flag == CLEAR) {
				paint.setColor(backgroundColor);
				bmCanvas.drawRect(0, 0, widthScreen, clear_y, paint);
			}
			bmCanvas.restore();
		}
	}

	public void clearView() {
		flag = CLEAR;
		clearTimer = new Timer();
		clearTimer.schedule(new ClearTask(), 0, 33);
	}

	class ClearTask extends TimerTask {
		@Override
		public void run() {
			if (clear_y <= heightSreecn) {
				clear_y += 10;
				bitmapDraw();
			} else {
				clear_y = 0;
				flag = ACTION_NULL;
				clearTimer.cancel();
			}
		}
	}
	
	class DrawThread extends Thread{
		
		private Context context;
		
		public DrawThread(Context _context) {
			context = _context;
		}
		
		@Override
		public void run() {
			while (true) {
				draw();
	            try {
	                Thread.sleep(18);
	            } catch (InterruptedException e) {
	                e.printStackTrace();
	            }
	        }
		}
		
	}
	
	private void draw() {
		try {
			canvas = sfh.lockCanvas(); // 得到一个canvas实例
			canvas.drawBitmap(bitmap, 0, 0, null);
			// Log.e("hwv", "Draw Done.");
		} catch (Exception ex) {
		} finally { // 备注3
			if (canvas != null)
				sfh.unlockCanvasAndPost(canvas); // 将画好的画布提交
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		heightSreecn = this.getMeasuredHeight();
		widthScreen = this.getMeasuredWidth();
		Log.e("xxx", heightSreecn + "     " + widthScreen);
		bitmap = Bitmap.createBitmap(widthScreen, heightSreecn,
				Config.ARGB_8888);
		bmCanvas = new Canvas(bitmap);
		if (initBitmap != null) {
			bmCanvas.drawBitmap(initBitmap, 0, 0, null);
			initBitmap.recycle();
		} else {
			bmCanvas.drawRect(0, 0, widthScreen, heightSreecn, paint);
		}
		th.start();
		mTimer = new Timer();
		mTimer.schedule(new ColorChangeTask(), 0, 1500);
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		try {
			bitmap.recycle();
			mTimer.cancel();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * public boolean recycleBitmap() { try { bitmap.recycle(); return true; }
	 * catch (Exception e) {
	 * 
	 * } return false; }
	 */
	
	public Bitmap getBitmap() {
		return bitmap;
	}

	public String getBitmapPath() {
		return path;
	}

	public void setInitBitmap(String _path) {
		path = _path;
		try {
			initBitmap = BitmapFactory.decodeFile(path);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
}
