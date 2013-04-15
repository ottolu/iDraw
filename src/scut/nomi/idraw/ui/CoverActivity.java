package scut.nomi.idraw.ui;

import java.util.List;

import scut.nomi.idraw.R;
import scut.nomi.idraw.adapter.ImageAdapter;
import scut.nomi.idraw.util.Constants;
import scut.nomi.idraw.util.ReferenceManager;
import scut.nomi.idraw.util.SBUtil;
import scut.nomi.idraw.view.CoverGallery;
import scut.nomi.idraw.view.FirstRunDialog;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;

public class CoverActivity extends BaseActivity {

	private List<String> pathList;
	private ImageAdapter adapter;
	private CoverGallery coverGallery;
	private ImageView takePhotoBtn, folder;

	private String photoPath = "";

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.gallery_layout);

		takePhotoBtn = (ImageView) findViewById(R.id.photoBtn);
		folder = (ImageView) findViewById(R.id.folder);

		pathList = SBUtil.getFileDir(SBUtil.getWorkPath());

		adapter = new ImageAdapter(this, pathList);

		coverGallery = (CoverGallery) findViewById(R.id.myGallery);
		coverGallery.setAdapter(adapter);

		coverGallery.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {

				if (coverGallery.getSelectedItemId() == 0) {
					Intent intent = new Intent(CoverActivity.this,
							SelectActivity.class);
					//Bundle bundle = new Bundle();
					//bundle.putBoolean(Constants.NEW_DRAW, true);
					//intent.putExtras(bundle);
					startActivity(intent);
				} else {
					Intent intent = new Intent(CoverActivity.this,
							DrawActivity.class);
					Bundle bundle = new Bundle();
					bundle.putBoolean(Constants.NEW_DRAW, false);
					bundle.putString(Constants.EDIT_DRAW, pathList
							.get((int) (coverGallery.getSelectedItemId() - 1)));
					intent.putExtras(bundle);
					startActivity(intent);
				}
			}
		});

		takePhotoBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				photoPath = Constants.PATH + "/" + Constants.PHOTO_NAME + "/"
						+ SBUtil.getFileName() + "_" + SBUtil.getRandomId()
						+ ".png";
				Intent intent1 = new Intent();
				intent1.setClass(CoverActivity.this, TakeCamera.class);
				intent1.putExtra(Constants.PATH_SIGH, photoPath);
				startActivity(intent1);
			}
		});

		folder.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent i = new Intent(
						Intent.ACTION_PICK,
						android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				startActivityForResult(i, Constants.RESULT_LOAD_IMAGE);
			}
		});

		if (ReferenceManager.getInstance(this).isFirstRun()) {

			FirstRunDialog frd = new FirstRunDialog(this);
			frd.setCancelable(true);
			frd.setTitle("ÌבÊ¾");
			frd.show();
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		pathList = SBUtil.getFileDir(SBUtil.getWorkPath());

		adapter.rebuildAdapter(pathList);

		adapter.notifyDataSetChanged();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		/*
		if (requestCode == Activity.DEFAULT_KEYS_DIALER) {
			if (resultCode == RESULT_OK) {
				if (!"".equals(photoPath)) {
					Intent intent = new Intent(CoverActivity.this,
							DrawActivity.class);
					Bundle bundle = new Bundle();
					bundle.putBoolean(Constants.NEW_DRAW, true);
					bundle.putString(Constants.EDIT_DRAW, photoPath);
					intent.putExtras(bundle);
					startActivity(intent);
				}
			}
		}
		*/

		if (requestCode == Constants.RESULT_LOAD_IMAGE
				&& resultCode == RESULT_OK && null != data) {
			Uri selectedImage = data.getData();
			String[] filePathColumn = { MediaStore.Images.Media.DATA };

			Cursor cursor = getContentResolver().query(selectedImage,
					filePathColumn, null, null, null);
			cursor.moveToFirst();
			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			photoPath = cursor.getString(columnIndex);
			cursor.close();
			Intent intent = new Intent(CoverActivity.this, DrawActivity.class);
			Bundle bundle = new Bundle();
			bundle.putBoolean(Constants.NEW_DRAW, true);
			bundle.putString(Constants.EDIT_DRAW, photoPath);
			intent.putExtras(bundle);
			startActivity(intent);

		}

		super.onActivityResult(requestCode, resultCode, data);
	}
}
