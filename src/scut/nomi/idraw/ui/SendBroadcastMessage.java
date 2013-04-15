package scut.nomi.idraw.ui;

import java.io.File;

import scut.nomi.idraw.R;
import scut.nomi.idraw.util.Constants;
import scut.nomi.idraw.util.ReferenceManager;
import weibo4android.Weibo;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class SendBroadcastMessage extends BaseActivity {

	private String mText = "", photopath = "";

	private TextView mTextNum;
	private Button mSend;
	private ImageView img;
	private EditText mEdit;
	private ProgressDialog dialog = null;
	private Bitmap bm;

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			// update ok
			case 1:
				if (dialog != null) {
					dialog.dismiss();
				}
				Toast.makeText(SendBroadcastMessage.this, "发布成功",
						Toast.LENGTH_SHORT).show();
				if (bm != null) {
					bm.recycle();
				}
				SendBroadcastMessage.this.finish();
				break;
			// update error
			case 2:
				if (dialog != null) {
					dialog.dismiss();
				}
				Toast.makeText(SendBroadcastMessage.this, "发布失败",
						Toast.LENGTH_SHORT).show();
				if (bm != null) {
					bm.recycle();
				}
				SendBroadcastMessage.this.finish();
				break;

			default:
				break;
			}
		}
	};

	private OnClickListener mOnClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.btnSend:
				System.setProperty("weibo4j.oauth.consumerKey", Constants.consumerKey);
				System.setProperty("weibo4j.oauth.consumerSecret", Constants.consumerSecret);

				dialog.show();

				new Thread() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						super.run();
						try {
							Weibo weibo = new Weibo();
							SharedPreferences setting = ReferenceManager
									.getInstance(SendBroadcastMessage.this)
									.getSetting();
							weibo.setToken(setting.getString(
									Constants.ACCESS_TOKEN_KEY, ""), setting
									.getString(Constants.ACCESS_TOKEN_SECRET,
											""));
							if (bm != null) {
								File f = new File(photopath);
								weibo.uploadStatus(mEdit.getText().toString(),
										f);
							} else {
								weibo.updateStatus(mEdit.getText().toString());
							}
							Message msg = new Message();
							msg.what = 1;
							mHandler.sendMessage(msg);
						} catch (Exception e) {
							e.printStackTrace();
							Message msg = new Message();
							msg.what = 2;
							mHandler.sendMessage(msg);
						}
					}
				}.start();
				break;
			case R.id.btnClose:
				SendBroadcastMessage.this.finish();
				break;
			default:
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		Bundle bundle = getIntent().getExtras();
		photopath = bundle.getString(Constants.SEND_DRAW);

		dialog = new ProgressDialog(SendBroadcastMessage.this);
		dialog.setMessage("微博分享中...");
		dialog.setIndeterminate(false);
		dialog.setCancelable(true);

		setContentView(R.layout.broad_cast_message);

		initComponent();

		photoShow(photopath);
	}

	/**
	 * init component of the view
	 */
	private void initComponent() {
		Button close = (Button) this.findViewById(R.id.btnClose);
		close.setOnClickListener(mOnClickListener);
		mSend = (Button) this.findViewById(R.id.btnSend);
		mSend.setOnClickListener(mOnClickListener);
		mTextNum = (TextView) this.findViewById(R.id.tv_text_limit);
		mEdit = (EditText) this.findViewById(R.id.etEdit);
		img = (ImageView) findViewById(R.id.photo_take);
		mEdit.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				mText = mEdit.getText().toString();
				int len = mText.length();
				if (len <= 140) {
					len = 140 - len;
					mTextNum.setTextColor(R.color.dark_blue);
					if (!mSend.isEnabled())
						mSend.setEnabled(true);
				} else {
					len = len - 140;
					mTextNum.setTextColor(Color.RED);
					if (mSend.isEnabled())
						mSend.setEnabled(false);
				}
				mTextNum.setText(String.valueOf(len));
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});
	}

	private void photoShow(String path) {
		try {
			File f = new File(path);
			if (f.exists()) {
				bm = BitmapFactory.decodeFile(path);
				img.setImageBitmap(bm);
			} else {
				Toast.makeText(this, "无法打开该图片", Toast.LENGTH_SHORT).show();
			}
		} catch (Exception e) {
			Toast.makeText(this, "无法打开该图片", Toast.LENGTH_SHORT).show();
		}
	}
}
