package scut.nomi.idraw.view;

import java.util.Timer;
import java.util.TimerTask;

import scut.nomi.idraw.engine.GameView;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class DrawView extends GameView {

	public static boolean clearing = false;

	//private float x, y;
	//private Object object;
	//private final int TIME = 100;
	private String path = "";
	
	public DrawView(Context context, String path) {
		super(context, path);
		init();
	}

	public DrawView(Context context) {
		super(context);
		init();
	}

	public DrawView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public void init() {
		//object = new Object();
		clearing = false;
	}
	
	public void drawPoint(float x, float y) {
		super.drawPoint(paint, x, y);
	}
	
	public void drawLine(float x, float y) {
		super.drawLine(paint, x, y);
	}
	
	public void updatePoint(float x, float y) {
		super.updatePoint(x, y);
	}

	public void clearView() {
		if (clearing == false) {
			clearing = true;
			super.clearView();
		}
	}
	
	public void setBgPath(String path) {
		bgPath = path; 
	}
}
