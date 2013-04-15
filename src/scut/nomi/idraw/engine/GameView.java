package scut.nomi.idraw.engine;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

/**
 * @author Nomi
 * @description 画图用的view 用于继承用 建议不要直接改写
 */
public class GameView extends SurfaceView implements Callback {

	private SurfaceHolder sfh;
	private Canvas canvas;
	private DrawThread th;
	private BgObject bgObj = null;
	private LineObject lineObject = null;
	
	protected Paint paint;
	protected Context context;
	protected List<BaseObject> objectsList = null;
	protected int heightSreecn, widthScreen;
	protected String bgPath = "";
	protected boolean drawThreadRun = true, surfaceCreated = false;
	
	public GameView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	public GameView(Context context) {
		super(context);
		init();
	}
	
	public GameView(Context context, String path) {
		super(context);
		init();
		bgPath = path;
	}
	
	private void init() {
		this.context = context;
		
		sfh = this.getHolder();
		sfh.addCallback(this);
		
		objectsList = new ArrayList<BaseObject>();
		paint = new Paint();
		//paint.setStyle(Style.FILL_AND_STROKE);
		paint.setStrokeWidth(5);
		paint.setAntiAlias(true);  //反锯齿
		paint.setDither(true);     //防抖
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeJoin(Paint.Join.ROUND);
		paint.setStrokeCap(Paint.Cap.ROUND);
		
		th = new DrawThread(context);
	}

	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		heightSreecn = this.getMeasuredHeight();
		widthScreen = this.getMeasuredWidth();
		
		Log.e("h&w", heightSreecn+"  "+widthScreen);
		
		surfaceCreated = true;
		
		if ("".equals(bgPath) || bgPath == null) {
			bgObj = new BgObject(context, heightSreecn, widthScreen);
		}else {
			bgObj = new BgObject(context, bgPath, heightSreecn, widthScreen);
		}
		lineObject = new LineObject(context, heightSreecn, widthScreen);
		objectsList.add(bgObj);
		objectsList.add(lineObject);
		if (!drawThreadRun) {
			drawThreadRun = true;
		}
		th.start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		drawThreadRun = false;
		surfaceCreated = false;
		Iterator<BaseObject> objListIt = objectsList.iterator();
		while (objListIt.hasNext()) {
			objListIt.next().recycle();
		}
		objectsList.clear();
	}

	protected void randomColor(){
		if (lineObject != null) {
			lineObject.randomColor();
		}
	}
	
	protected void drawLine(Paint paint, float x, float y) {
		if (lineObject != null) {
			lineObject.drawLine(paint, x, y);
		}
	}
	
	protected void drawPoint(Paint paint, float x, float y) {
		if (lineObject != null) {
			lineObject.drawPoint(paint, x, y);
		}
	}
	
	public void updatePoint(float x, float y) {
		if (lineObject != null) {
			lineObject.updatePoint(x, y);
		}
	}
	
	public Bitmap getBitmap() {
		Bitmap bitmap = Bitmap.createBitmap(widthScreen, heightSreecn, Config.ARGB_8888);
		Canvas bmcanvas = new Canvas(bitmap);
		Iterator<BaseObject> objListIt = objectsList.iterator();
		while (objListIt.hasNext()) {
			objListIt.next().draw(bmcanvas, paint);
		}
		return bitmap;
	}
	
	public String getBitmapPath() {
		return bgPath;
	}
	
	private void draw() {
		try {
			canvas = sfh.lockCanvas(); // 得到一个canvas实例
			Iterator<BaseObject> objListIt = objectsList.iterator();
			while (objListIt.hasNext()) {
				objListIt.next().draw(canvas, paint);
			}
			// Log.e("gv", "Draw Done.");
		} catch (Exception ex) {
		} finally { // 备注3
			if (canvas != null)
				sfh.unlockCanvasAndPost(canvas); // 将画好的画布提交
		}
	}
	
	class DrawThread extends Thread{
		
		private Context context;
		
		public DrawThread(Context _context) {
			context = _context;
		}
		
		@Override
		public void run() {
			while (drawThreadRun) {
				draw();
	            try {
	                Thread.sleep(20);
	            } catch (InterruptedException e) {
	                e.printStackTrace();
	            }
	        }
		}
	}
	
	protected void clearView() {
		bgObj.setBgTmp(getBitmap());
		objectsList.clear();
		if (lineObject != null) {
			lineObject.recycle();
		}
		lineObject = new LineObject(context, heightSreecn, widthScreen);
		objectsList.add(bgObj);
		objectsList.add(lineObject);
		bgObj.bgClear();
	}
	
	public void setPenColor(int color) {
		if (lineObject != null) {
			lineObject.setRandomColorOff();
		}
		paint.setColor(color);
	}
	
	public void setRandomColor() {
		if (lineObject != null) {
			lineObject.setRandomColorOn();
		}
	}
	
	public void setPenSize(float width) {
		paint.setStrokeWidth(width);
	}
}
