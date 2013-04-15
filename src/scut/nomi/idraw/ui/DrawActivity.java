package scut.nomi.idraw.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import scut.nomi.idraw.R;
import scut.nomi.idraw.listener.ShakeListener;
import scut.nomi.idraw.util.Constants;
import scut.nomi.idraw.util.SBUtil;
import scut.nomi.idraw.view.ColorPickerView;
import scut.nomi.idraw.view.DrawView;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.SimpleAdapter;
import android.widget.Toast;

/**
 * @author nomi
 * 
 */
public class DrawActivity extends BaseActivity {

	private ShakeListener mShaker;
	private String[] menu_name_array = { "清空画板", "保存作品", "微博分享", "返回" },
					 draw_menu_array = { "画笔大小", "橡皮擦", "调色板", "清空画板" };
	
	int[] menu_image_array = { R.drawable.menu_refresh, R.drawable.menu_save,
			R.drawable.menu_sharepage, R.drawable.menu_return },
		  draw_image_array = { R.drawable.draw_pen, R.drawable.draw_eraser, 
			R.drawable.draw_palette, R.drawable.menu_refresh};
	
	private GridView menuGrid;
	private PopupWindow menuPopWin = null, drawPopWin = null, colorPickerPW = null, penPickerPW = null;
	private DrawView handwritingView;
	private String tmpPath = "", workpath = "";
	private boolean newDraw = true;
	private FileOutputStream out;

	private float x, y;
	private Object object;
	private final int TIME = 100;
	private int lastPointNum = 2, currentPointNum = 2;
	private int lastTouchState = -1;
	private float[] points = new float[8];

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Bundle bundle = getIntent().getExtras();
		newDraw = bundle.getBoolean(Constants.NEW_DRAW);
		workpath = bundle.getString(Constants.EDIT_DRAW);
		//Log.w("w",workpath);
		object = new Object();

		penPickerInt();
		colorPickerInt();
		menu_int();
		draw_menu_int();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		// handwritingView = new HandwritingView(this);
		//Log.w("w",workpath);
		if ("".equals(tmpPath)) {
			if (!"".equals(workpath)) {
				// Toast.makeText(getApplicationContext(), "workpath: " +
				// workpath, Toast.LENGTH_LONG).show();
				handwritingView = new DrawView(this, workpath);
			} else {
				handwritingView = new DrawView(this);
			}
		} else {
			// Toast.makeText(getApplicationContext(), "tmpPath: " + tmpPath,
			// Toast.LENGTH_LONG).show();
			handwritingView = new DrawView(this, tmpPath);
		}

		setContentView(handwritingView);

		mShaker = new ShakeListener(this);
		mShaker.setOnShakeListener(new ShakeListener.OnShakeListener() {
			public void onShake() {
				handwritingView.clearView();
			}
		});
	}

	private void colorPickerInt() {
		LayoutInflater mLayoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		ViewGroup menuView = (ViewGroup) mLayoutInflater.inflate(
				R.layout.color_picker, null, true);
		
		final ColorPickerView colorPickerView = (ColorPickerView) menuView.findViewById(R.id.ColorPicker);
		Button btnColorPicker = (Button) menuView.findViewById(R.id.btnColorPicker);
		final CheckBox checkBox = (CheckBox) menuView.findViewById(R.id.cbColorPicker);
		
		colorPickerPW = new PopupWindow(menuView ,LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT, true);
		colorPickerPW.setBackgroundDrawable(new BitmapDrawable());
		colorPickerPW.setAnimationStyle(R.style.DrawPopupAnimation);
		colorPickerPW.update();
		
		btnColorPicker.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(checkBox.isChecked()){
					handwritingView.setRandomColor();
				} else {
					handwritingView.setPenColor(colorPickerView.getColor());
				}
				colorPickerPW.dismiss();
			}
		});
	}
	
	private void penPickerInt() {
		LayoutInflater mLayoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		ViewGroup menuView = (ViewGroup) mLayoutInflater.inflate(
				R.layout.pen_picker, null, true);

		Button btnPenPicker = (Button) menuView.findViewById(R.id.btnPenPicker);
		final SeekBar seekBar = (SeekBar) menuView.findViewById(R.id.sbPenPicker);
		
		penPickerPW = new PopupWindow(menuView ,LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT, true);
		penPickerPW.setBackgroundDrawable(new BitmapDrawable());
		penPickerPW.setAnimationStyle(R.style.DrawPopupAnimation);
		penPickerPW.update();
		
		btnPenPicker.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				handwritingView.setPenSize(seekBar.getProgress() <= 1 ? 1 : seekBar.getProgress());
				penPickerPW.dismiss();
			}
		});
	}
	
	private void menu_int() {

		LayoutInflater mLayoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		ViewGroup menuView = (ViewGroup) mLayoutInflater.inflate(
				R.layout.menu_pop, null, true);
		menuGrid = (GridView) menuView.findViewById(R.id.gridview);
		menuGrid.setAdapter(getMenuAdapter(menu_name_array, menu_image_array));
		menuGrid.requestFocus();
		menuGrid.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if (newDraw) {
					handwritingView.setBgPath("");
					newDraw = false;
				}

				switch (arg2) {
				case 0:
					handwritingView.clearView();
					break;

				case 1:
					tmpPath = handwritingView.getBitmapPath();
					if ("".equals(tmpPath)) {
						tmpPath = Constants.PATH + '/' + SBUtil.getFileName()
								+ "_" + SBUtil.getRandomId() + ".png";
					}
					photoSave(tmpPath);
					break;

				case 2:
					tmpPath = handwritingView.getBitmapPath();
					if ("".equals(tmpPath)) {
						tmpPath = Constants.PATH + '/' + SBUtil.getFileName()
								+ "_" + SBUtil.getRandomId() + ".png";
					}
					photoSave(tmpPath);

					Intent intent = new Intent(DrawActivity.this,
							SendBroadcastMessage.class);
					Bundle bundle = new Bundle();
					bundle.putString(Constants.SEND_DRAW, tmpPath);
					intent.putExtras(bundle);
					startActivity(intent);
					break;

				case 3:
					DrawActivity.this.finish();
					break;

				default:
					break;
				}
				menuPopWin.dismiss();
			}
		});
		menuGrid.setVerticalScrollBarEnabled(false);
		menuGrid.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				return MotionEvent.ACTION_MOVE == event.getAction() ? true
                        : false;
			}
		});

		menuPopWin = new PopupWindow(menuView, LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT, true);
		menuPopWin.setBackgroundDrawable(new BitmapDrawable());
		menuPopWin.setAnimationStyle(R.style.PopupAnimation);
		menuPopWin.update();
	}
	
	private void draw_menu_int() {

		LayoutInflater mLayoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		ViewGroup menuView = (ViewGroup) mLayoutInflater.inflate(
				R.layout.draw_menu_pop, null, true);
		menuGrid = (GridView) menuView.findViewById(R.id.gridview);
		menuGrid.setAdapter(getMenuAdapter(draw_menu_array, draw_image_array));
		menuGrid.requestFocus();
		menuGrid.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {

				switch (arg2) {
				case 0:
					penPickerPW.showAtLocation(handwritingView, Gravity.CENTER_VERTICAL
							| Gravity.CENTER_HORIZONTAL, 0, 0);
					break;

				case 1:
					handwritingView.setPenColor(Color.WHITE);
					handwritingView.setPenSize(15);
					break;

				case 2:
					colorPickerPW.showAtLocation(handwritingView, Gravity.CENTER_VERTICAL
							| Gravity.CENTER_HORIZONTAL, 0, 0);
					break;

				case 3:
					handwritingView.clearView();
					break;

				default:
					break;
				}
				drawPopWin.dismiss();
			}
		});
		menuGrid.setVerticalScrollBarEnabled(false);
		menuGrid.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				return MotionEvent.ACTION_MOVE == event.getAction() ? true
                        : false;
			}
		});

		drawPopWin = new PopupWindow(menuView, LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT, true);
		drawPopWin.setBackgroundDrawable(new BitmapDrawable());
		drawPopWin.setAnimationStyle(R.style.DrawPopupAnimation);
		drawPopWin.update();
	}

	private ListAdapter getMenuAdapter(String[] menuNameArray,
			int[] menuImageArray) {
		ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
		for (int i = 0; i < menuNameArray.length; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("itemImage", menuImageArray[i]);
			map.put("itemText", menuNameArray[i]);
			data.add(map);
		}
		SimpleAdapter simperAdapter = new SimpleAdapter(this, data,
				R.layout.item_menu, new String[] { "itemImage", "itemText" },
				new int[] { R.id.item_image, R.id.item_text });
		return simperAdapter;
	}

	private boolean photoSave(String path) {
		if (!Constants.CAN_SAVE_OR_NOT) {
			Toast.makeText(getApplicationContext(), "找不到SD卡.保存功能不能使用.",
					Toast.LENGTH_LONG).show();
			return false;
		}
		Bitmap bitmap = handwritingView.getBitmap();
		if (bitmap == null) {
			Toast.makeText(getApplicationContext(), "作品保存失败.",
					Toast.LENGTH_LONG).show();
			return false;
		}
		File file = new File(path);
		try {
			out = new FileOutputStream(file);// 设置输出流
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			Toast.makeText(getApplicationContext(), "作品保存失败.",
					Toast.LENGTH_LONG).show();
		}
		try {
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
			Toast.makeText(getApplicationContext(), "作品保存失败.",
					Toast.LENGTH_LONG).show();
		}
		if (file.length() > 0) {
			Toast.makeText(getApplicationContext(), "作品已保存于SD卡IDraw文件夹内.",
					Toast.LENGTH_LONG).show();
			return true;
		}
		return false;
	}

	@Override
	/**
	 * 创建MENU
	 */
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add("menu");// 必须创建一项
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	/**
	 * 拦截MENU
	 */
	public boolean onMenuOpened(int featureId, Menu menu) {
		Log.e("menu", "click");
		if (menuPopWin != null) {
			if (menuPopWin.isShowing())
				menuPopWin.dismiss();
			else {
				menuPopWin.showAtLocation(handwritingView, Gravity.BOTTOM
						| Gravity.LEFT, 0, 0);
			}
		}
		return false;// 返回为true 则显示系统menu
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		// Toast.makeText(getApplicationContext(), ""+event.getPointerCount(),
		// Toast.LENGTH_SHORT).show();

		if (handwritingView.clearing) {
			return super.onTouchEvent(event);
		}

		lastPointNum = currentPointNum;
		currentPointNum = event.getPointerCount();

		if (event.getAction() == MotionEvent.ACTION_POINTER_2_DOWN) {
			points[0] = event.getX(0);
			points[2] = event.getX(1);
			points[1] = event.getY(0);
			points[3] = event.getY(1);
		}

		if (event.getAction() == MotionEvent.ACTION_UP) {

			if (lastTouchState == MotionEvent.ACTION_DOWN) {
				x = event.getX();
				y = event.getY();
				handwritingView.drawPoint(x, y);
			}
			return true;
		} 
		
		if (event.getAction() == MotionEvent.ACTION_DOWN) {

			x = event.getX();
			y = event.getY();
			lastTouchState = MotionEvent.ACTION_DOWN;
			handwritingView.updatePoint(x, y);
			Log.e("touch", "down");
			return true;

		} else if (event.getAction() == MotionEvent.ACTION_MOVE) {

			Log.e("touch", "move");
			
			lastTouchState = MotionEvent.ACTION_MOVE;

			if (currentPointNum == 2) {
				points[4] = event.getX(0);
				points[6] = event.getX(1);
				points[5] = event.getY(0);
				points[7] = event.getY(1);
				if (pathCalc(points)) {
					//Toast.makeText(getApplicationContext(), "双指滑动~!", Toast.LENGTH_SHORT).show();
					drawPopWin.showAtLocation(handwritingView, Gravity.TOP
						| Gravity.LEFT, 0, 0);
				}
			} else if (lastPointNum == 1 && currentPointNum == 1) {
				x = event.getX();
				y = event.getY();
				handwritingView.drawLine(x, y);
			}
			return true;
		}
		synchronized (object) { // 避免过度占用资源
			try {
				object.wait(TIME);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return super.onTouchEvent(event);
	}

	private boolean pathCalc(float[] points) {

		if ((points[4] - points[0] < 100) && (points[6] - points[2] < 100)
				&& (points[5] - points[1] > 100)
				&& (points[7] - points[3] > 100)) {
			return true;
		}
		return false;
	}
}