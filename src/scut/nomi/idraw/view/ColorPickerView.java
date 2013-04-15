package scut.nomi.idraw.view;

import scut.nomi.idraw.R;
import android.R.integer;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ComposeShader;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class ColorPickerView extends View {

	private Context mContext;
	
	private Paint mRightPaint;
	
	private int mHeight;
	private int mWidth;
	private int[] mRightColors;
	private int RIGHT_WIDTH;
	private int LEFT_WIDTH;
	
	private Bitmap mLeftBitmap;
	private Bitmap mLeftBitmap2;
	private Bitmap mRightBitmap;
	private Bitmap mRightBitmap2;
	
	private Paint mBitmapPaint;
	
	private final int SPLIT_WIDTH = 24;
	
	private boolean downInLeft = false;
	private boolean downInRight = false;
	
	private PointF mLeftSelectPoint; 
	private PointF mRightSelectPoint;
	
	private OnColorChangedListener mChangedListener;
	
	private boolean mLeftMove = false;
	private boolean mRightMove = false;
	
	private float mLeftBitmapRadius;
	private Bitmap mGradualChangeBitmap;
	private float mRightBitmapHalfHeight;
	private float mRightBitmapQuarterWidth;
	
	private int mCallBackColor = Integer.MAX_VALUE;
	
	public ColorPickerView(Context context) {
		this(context, null);
	}
	
	public ColorPickerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		init();
	}

	public void setOnColorChangedListenner(OnColorChangedListener listener) {
		mChangedListener = listener;
	}
	
	private void init() {
		mRightPaint = new Paint(); 
		mRightPaint.setStyle(Paint.Style.FILL);
		mRightPaint.setStrokeWidth(1);
		
		mRightColors = new int[3];
		mRightColors[0] = Color.WHITE;
		mRightColors[2] = Color.BLACK;
		
		mBitmapPaint = new Paint();
		
		mLeftBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.reading__color_view__button);
		mLeftBitmap2 = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.reading__color_view__button_press);
		mLeftBitmapRadius = mLeftBitmap.getWidth() / 2;
		mLeftSelectPoint = new PointF(SPLIT_WIDTH, SPLIT_WIDTH);
		
		mRightBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.reading__color_view__saturation);
		mRightBitmap2 = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.reading__color_view__saturation_press);
		mRightSelectPoint = new PointF(SPLIT_WIDTH, SPLIT_WIDTH);
		mRightBitmapHalfHeight = mRightBitmap.getHeight() / 2;
		mRightBitmapQuarterWidth = mRightBitmap.getWidth() / 4;
		RIGHT_WIDTH = mRightBitmap.getWidth() / 2;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		// 左边
		canvas.drawBitmap(getGradual() , null , new Rect(SPLIT_WIDTH, SPLIT_WIDTH, LEFT_WIDTH + SPLIT_WIDTH, mHeight - SPLIT_WIDTH), mBitmapPaint);
		
		// 右边
		mRightColors[1] = mRightPaint.getColor();
		Shader rightShader = new LinearGradient(mWidth - SPLIT_WIDTH - RIGHT_WIDTH / 2, SPLIT_WIDTH, mWidth - SPLIT_WIDTH - RIGHT_WIDTH / 2, mHeight - SPLIT_WIDTH, mRightColors, null, Shader.TileMode.MIRROR);  
		mRightPaint.setShader(rightShader);  
		canvas.drawRect(new Rect(mWidth - SPLIT_WIDTH - RIGHT_WIDTH, SPLIT_WIDTH, mWidth - SPLIT_WIDTH, mHeight-SPLIT_WIDTH), mRightPaint);

		// 两个图标
		if (mLeftMove) {
			canvas.drawBitmap(mLeftBitmap, mLeftSelectPoint.x - mLeftBitmapRadius, mLeftSelectPoint.y - mLeftBitmapRadius, mBitmapPaint);
		} else {
			canvas.drawBitmap(mLeftBitmap2, mLeftSelectPoint.x - mLeftBitmapRadius, mLeftSelectPoint.y - mLeftBitmapRadius, mBitmapPaint);
		}
		
		if (mRightMove) {
			canvas.drawBitmap(mRightBitmap, mWidth - SPLIT_WIDTH - RIGHT_WIDTH - mRightBitmapQuarterWidth, mRightSelectPoint.y - mRightBitmapHalfHeight, mBitmapPaint);
		} else {
			canvas.drawBitmap(mRightBitmap2, mWidth - SPLIT_WIDTH - RIGHT_WIDTH - mRightBitmapQuarterWidth, mRightSelectPoint.y - mRightBitmapHalfHeight, mBitmapPaint);
		}
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int width = MeasureSpec.getSize(widthMeasureSpec);
		int height = MeasureSpec.getSize(heightMeasureSpec);
		if (widthMode == MeasureSpec.EXACTLY) {
			mWidth = width;
		} else {
			mWidth = 480;
		}
		if (heightMode == MeasureSpec.EXACTLY) {
			mHeight = height;
		} else {
			mHeight = 350;
		}
		LEFT_WIDTH = mWidth - SPLIT_WIDTH * 3 - RIGHT_WIDTH;
		setMeasuredDimension(mWidth, mHeight);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float x = event.getX();
		float y = event.getY();
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
		case MotionEvent.ACTION_MOVE:
			downInLeft = inLeftPanel(x, y);
			downInRight = inRightPanel(x, y);
			if (downInLeft) {
				mLeftMove = true;
				proofLeft(x, y);
				mRightPaint.setColor(getLeftColor(mLeftSelectPoint.x-SPLIT_WIDTH, mLeftSelectPoint.y-SPLIT_WIDTH));
			} else if (downInRight) {
				mRightMove = true;
				proofRight(x, y);
			}
			
			invalidate();
			int rightColor = getRightColor(mRightSelectPoint.y - SPLIT_WIDTH);
			if (mCallBackColor == Integer.MAX_VALUE || mCallBackColor != rightColor) {
				mCallBackColor = rightColor;
			} else {
				break;
			}
			if (mChangedListener != null) {
				mChangedListener.onColorChanged(mCallBackColor, 
						mRightPaint.getColor(),
						(mRightSelectPoint.y - SPLIT_WIDTH) / (mHeight - 2 * SPLIT_WIDTH));
			}
			break;
		case MotionEvent.ACTION_UP:
			if (downInLeft) {
				downInLeft = false;
			} else if (downInRight) {
				downInRight = false;
			}
			mLeftMove = false;
			mRightMove = false;
			invalidate();
			if (mChangedListener != null) {
				mChangedListener.onColorChanged(getRightColor(mRightSelectPoint.y - SPLIT_WIDTH), 
						mRightPaint.getColor(),
						(mRightSelectPoint.y - SPLIT_WIDTH) / (mHeight - 2 * SPLIT_WIDTH));
			}
		}
		return true;
	}
	
	@Override
	protected void onDetachedFromWindow() {
		/*
		if (mGradualChangeBitmap != null && mGradualChangeBitmap.isRecycled() == false) {
			mGradualChangeBitmap.recycle();
		}
		if (mLeftBitmap != null && mLeftBitmap.isRecycled() == false) {
			mLeftBitmap.recycle();
		}
		if (mLeftBitmap2 != null && mLeftBitmap2.isRecycled() == false) {
			mLeftBitmap2.recycle();
		}
		if (mRightBitmap != null && mRightBitmap.isRecycled() == false) {
			mRightBitmap.recycle();
		}
		if (mRightBitmap2 != null && mRightBitmap2.isRecycled() == false) {
			mRightBitmap2.recycle();
		}
		*/
		super.onDetachedFromWindow();
	}
	
	private Bitmap getGradual() {
		if (mGradualChangeBitmap == null) {
			Paint leftPaint = new Paint();
			leftPaint.setStrokeWidth(1);
			mGradualChangeBitmap = Bitmap.createBitmap(LEFT_WIDTH, mHeight - 2 * SPLIT_WIDTH, Config.RGB_565);
			Canvas canvas = new Canvas(mGradualChangeBitmap);
			int bitmapWidth = mGradualChangeBitmap.getWidth();
			LEFT_WIDTH = bitmapWidth;
			int bitmapHeight = mGradualChangeBitmap.getHeight();
			int[] leftColors = new int[] {Color.RED, Color.YELLOW, Color.GREEN, Color.CYAN, Color.BLUE, Color.MAGENTA};
			Shader leftShader = new LinearGradient(0, bitmapHeight / 2, bitmapWidth, bitmapHeight / 2, leftColors, null, TileMode.REPEAT);
			LinearGradient shadowShader = new LinearGradient(bitmapWidth / 2, 0, bitmapWidth / 2, bitmapHeight,
					Color.WHITE, Color.BLACK, Shader.TileMode.CLAMP);
			ComposeShader shader = new ComposeShader(leftShader, shadowShader, PorterDuff.Mode.SCREEN);
			leftPaint.setShader(shader);
			canvas.drawRect(0, 0, bitmapWidth, bitmapHeight, leftPaint);
		}
		return mGradualChangeBitmap;
	}
	
	private boolean inLeftPanel(float x, float y) {
		if ( 0 < x && x < SPLIT_WIDTH + LEFT_WIDTH + SPLIT_WIDTH / 2 && 0 < y && y < mWidth) {
			return true;
		} else {
			return false;
		}
	}
	
	private boolean inRightPanel(float x, float y) {
		if (mWidth - SPLIT_WIDTH - RIGHT_WIDTH - SPLIT_WIDTH / 2 < x && x < mWidth && 0 < y && y < mHeight) {
			return true;
		} else {
			return false;
		}
	}
	
	// 校正xy
	private void proofLeft(float x, float y) {
		if (x < SPLIT_WIDTH) {
			mLeftSelectPoint.x = SPLIT_WIDTH;
		} else if (x > (SPLIT_WIDTH + LEFT_WIDTH)) {
			mLeftSelectPoint.x = SPLIT_WIDTH + LEFT_WIDTH;
		} else {
			mLeftSelectPoint.x = x;
		}
		if (y < SPLIT_WIDTH) {
			mLeftSelectPoint.y = SPLIT_WIDTH;
		} else if (y > (mHeight - SPLIT_WIDTH)) {
			mLeftSelectPoint.y = mHeight - SPLIT_WIDTH;
		} else {
			mLeftSelectPoint.y = y;
		}
	}
	
	private void proofRight(float x, float y) {
		if (x < SPLIT_WIDTH) {
			mRightSelectPoint.x = SPLIT_WIDTH;
		} else if (x > (SPLIT_WIDTH + LEFT_WIDTH)) {
			mRightSelectPoint.x = SPLIT_WIDTH + LEFT_WIDTH;
		} else {
			mRightSelectPoint.x = x;
		}
		if (y < SPLIT_WIDTH) {
			mRightSelectPoint.y = SPLIT_WIDTH;
		} else if (y > (mHeight - SPLIT_WIDTH)) {
			mRightSelectPoint.y = mHeight - SPLIT_WIDTH;
		} else {
			mRightSelectPoint.y = y;
		}
	}
	
	private int getLeftColor(float x, float y) {
		Bitmap temp = getGradual();
		// 为了防止越界
		int intX = (int) x;
		int intY = (int) y;
		if (intX >= temp.getWidth()) {
			intX = temp.getWidth() - 1;
		}
		if (intY >= temp.getHeight()) {
			intY = temp.getHeight() - 1;
		}
        return temp.getPixel(intX, intY);
	}
	
	private int getRightColor(float y) {
		int a, r, g, b, so, dst;  
		float p;  
		
		float rightHalfHeight = (mHeight - (float)SPLIT_WIDTH * 2) / 2;
		if (y < rightHalfHeight) {
			so = mRightColors[0];   
			dst = mRightColors[1];
			p =  y / rightHalfHeight;
		} else {
			so = mRightColors[1];
			dst = mRightColors[2];
			p = (y - rightHalfHeight) / rightHalfHeight;
		}
  
        a = ave(Color.alpha(so), Color.alpha(dst), p);  
        r = ave(Color.red(so), Color.red(dst), p);  
        g = ave(Color.green(so), Color.green(dst), p);  
        b = ave(Color.blue(so), Color.blue(dst), p);  
        return Color.argb(a, r, g, b);
	} 
	
	private int ave(int s, int d, float p) {
		return s + Math.round(p * (d - s));
	}
	
	// ### 内部类 ###
	public interface OnColorChangedListener {
		void onColorChanged(int color, int originalColor, float saturation);
	}
	
	public int getColor() {
		return mCallBackColor;
	}
}
