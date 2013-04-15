package scut.nomi.idraw.engine;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * @author Nomi
 * @description 大部分图元用此object承载 可拖动
 */
public class MainObject extends BaseObject implements Dragable{
	
	public MainObject(Context context, String Name, String path, float x, float y, float height, float width) {
		this(context, path, x, y, height, width);
		TAG = Name;
	}
	
	public MainObject(Context context, String path, float x, float y, float height, float width) {
		super(context, x, y, height, width);
		TAG = "MainObject";
		dragable = true;
		try {
			bitmap = BitmapFactory.decodeFile(path);
		} catch (Exception e) {
			errorLog("Cannot Read");
			canWork = false;
		}
	}
	
	public MainObject(Context context, int rid, float x, float y, float height, float width) {
		super(context, x, y, height, width);
		TAG = "MainObject";
		dragable = true;
		try {
			bitmap = BitmapFactory.decodeResource(context.getResources(), rid);
		} catch (Exception e) {
			errorLog("Cannot Read");
			canWork = false;
		}
	}

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

	@Override
	public void drag(float x, float y) {
		this.x = x;
		this.y = y;
	}

}
