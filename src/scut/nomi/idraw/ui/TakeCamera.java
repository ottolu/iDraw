package scut.nomi.idraw.ui;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.crypto.spec.IvParameterSpec;

import scut.nomi.idraw.R;
import scut.nomi.idraw.ui.CoverActivity;
import scut.nomi.idraw.ui.DrawActivity;
import scut.nomi.idraw.util.Constants;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;

import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.SurfaceHolder.Callback;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SlidingDrawer;

public class TakeCamera extends BaseActivity implements Callback,
		OnClickListener, AutoFocusCallback {

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.sure:
			saveBitmap();
			break;
		case R.id.cancel:
			init();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_camera, menu);
		return super.onCreateOptionsMenu(menu);
	}

	SurfaceView mySurfaceView;// surfaceView声明
	SurfaceHolder holder;// surfaceHolder声明
	Camera myCamera;// 相机声明
	String filePath;// 照片保存路径
	boolean isClicked = false;// 是否点击标识
	private Bitmap bm;

	// 创建jpeg图片回调数据对象
	PictureCallback jpeg = new PictureCallback() {

		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			// TODO Auto-generated method stub
			bm = BitmapFactory.decodeByteArray(data, 0, data.length);
			myCamera.stopPreview();

			// ImageView iv = new ImageView(TakeCamera.this);
			// iv.setImageBitmap(bm);
			// setContentView(iv);
			//
			init2();
			// openOptionsMenu();

		}
	};

	/**
	 * 保存图片并且跳转
	 */
	private void saveBitmap() {
		try {// 获得图片

			File file = new File(filePath);
			BufferedOutputStream bos = new BufferedOutputStream(
					new FileOutputStream(file));
			bm.compress(Bitmap.CompressFormat.PNG, 100, bos);// 将图片压缩到流中
			bos.flush();// 输出
			bos.close();// 关闭
		} catch (Exception e) {
			e.printStackTrace();
		}

		Intent intent = new Intent(TakeCamera.this, DrawActivity.class);
		Bundle bundle = new Bundle();
		bundle.putBoolean(Constants.NEW_DRAW, true);
		bundle.putString(Constants.EDIT_DRAW, filePath);
		intent.putExtras(bundle);
		startActivity(intent);
		finish();
	}

	private Display display;
	private ImageView take_photo;
	private SlidingDrawer slide;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 设置拍摄方向
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		WindowManager manager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		display = manager.getDefaultDisplay();

		filePath = getIntent().getStringExtra(Constants.PATH_SIGH);
		// Log.w("w",filePath);
		init();
	}

	private void init2() {
		setContentView(R.layout.camera_handle);
		ImageView cancel, ok;
		cancel = (ImageView) findViewById(R.id.cancel);
		ImageView image = (ImageView) findViewById(R.id.imageview1);
		slide = (SlidingDrawer) findViewById(R.id.slide);

		slide.open();
		ok = (ImageView) findViewById(R.id.ok);

		image.setImageBitmap(bm);

		ok.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				saveBitmap();

			}
		});
		cancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				init();
			}
		});
		image.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// Log.e("i","i");
				if (slide.isOpened()) {
					// Log.w("w","open");
					slide.close();
				} else {
					// Log.w("w","close");
					slide.open();
				}
			}
		});
	}

	private void init() {

		setContentView(R.layout.camera_layout);
		mySurfaceView = (SurfaceView) findViewById(R.id.surfaceView1);
		holder = mySurfaceView.getHolder();
		holder.addCallback(this);
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		mySurfaceView.setOnClickListener(this);

		take_photo = (ImageView) findViewById(R.id.take_photo);

		take_photo.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Camera.Parameters params = myCamera.getParameters();
				// Log.w("w","take photo");
				params.setPictureSize(display.getWidth(), display.getHeight());
				params.setPictureFormat(PixelFormat.JPEG);
				myCamera.setParameters(params);
				myCamera.takePicture(null, null, jpeg);
			}
		});
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
		// 设置参数并开始预览
		Camera.Parameters params = myCamera.getParameters();
		params.setPictureFormat(PixelFormat.JPEG);

		params.setPreviewSize(display.getWidth(), display.getHeight());
		myCamera.setParameters(params);
		myCamera.startPreview();

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		// 开启相机
		if (myCamera == null) {
			myCamera = Camera.open();
			try {
				myCamera.setPreviewDisplay(holder);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		// 关闭预览并释放资源
		myCamera.stopPreview();
		myCamera.release();
		myCamera = null;

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (!isClicked) {
			myCamera.autoFocus(this);// 自动对焦
			isClicked = true;
		} else {
			myCamera.startPreview();// 开启预览
			isClicked = false;
		}

	}

	@Override
	public void onAutoFocus(boolean success, Camera camera) {
		// TODO Auto-generated method stub
		if (success) {
			// 设置参数,并拍照

			Camera.Parameters parameters = camera.getParameters();

			parameters.setPreviewSize(display.getWidth(), display.getHeight());

			Log.i("run", display.getWidth() + ":" + display.getHeight());

		}

	}
}