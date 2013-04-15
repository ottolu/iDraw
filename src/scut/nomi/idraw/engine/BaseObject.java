package scut.nomi.idraw.engine;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;

/**
 * @author Nomi
 * @description Object»ù´¡Àà
 */
public abstract class BaseObject {
	
	protected String TAG = "BaseObject";
	protected float height, width;
	protected float x, y;
	protected boolean haveDrawed, dragable, canWork;
	protected Bitmap bitmap = null;
	protected Context context;
	
	public BaseObject(Context context, float x, float y, float height, float width){
		this.context = context;
		this.x = x;
		this.y = y;
		this.height = height;
		this.width = width;
		haveDrawed = false;
		dragable = false;
		canWork = true;
	}
	
	public void setLocation(float x, float y){
		this.x = x;
		this.y = y;
	}
	
	public void setX(float x){
		this.x = x;
	}
	
	public void setY(float y){
		this.y = y;
	}
	
	public void setSize(float height, float width){
		this.height = height;
		this.width = width;
	}
	
	public abstract boolean draw(Canvas canvas, Paint paint);
	
	public void recycle(){
		if (bitmap != null) {
			try {
				bitmap.recycle();
			} catch (Exception e) {
				// TODO: handle exception
			}
		}	
	}
	
	public void errorLog() {
		Log.e(TAG, "ERROR");
	}
	
	public void errorLog(String msg) {
		Log.e(TAG, "ERROR: "+msg);
	}
}
