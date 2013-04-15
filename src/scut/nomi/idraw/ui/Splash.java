package scut.nomi.idraw.ui;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import scut.nomi.idraw.R;
import scut.nomi.idraw.util.Constants;
import scut.nomi.idraw.util.ImageUtil;
import scut.nomi.idraw.util.SBUtil;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;

public class Splash extends BaseActivity {

	private File file;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.splash);

		new Handler().postDelayed(new Runnable() {
			public void run() {
				Intent mainIntent = null;
				mainIntent = new Intent(Splash.this, CoverActivity.class);
				Splash.this.startActivity(mainIntent);
				Splash.this.finish();
			}
		}, 2000);
		
		if (SBUtil.getSDPath() == null) {
			Constants.CAN_SAVE_OR_NOT = false;
		}
		try {
			Constants.PATH = SBUtil.getWorkPath();
			file = new File(Constants.PATH);
			if (!file.exists()) {
				file.mkdir();
			}
			file = new File(Constants.PATH + "/" + Constants.PHOTO_NAME);
			if (!file.exists()) {
				file.mkdir();
			}
			file = new File(Constants.PATH + "/" + Constants.TEMP_NAME);
			if (!file.exists()) {
				file.mkdir();
			}
			
			//Ìí¼Ó  »­°å
			file = new File(Constants.PATH+"/"  + Constants.BOARD);
			if (!file.exists()) {
				file.mkdir();
				List<Integer> li= ImageUtil.getImageValues();
				//Log.e("e",li+"");
				for(int i=0;i!=li.size();++i){
					try{
						file = new File(Constants.PATH+"/"+Constants.BOARD+"/"+i+".png");
						file.createNewFile();
						FileOutputStream fOut = new FileOutputStream(file);
						Bitmap b =BitmapFactory.decodeResource(getResources(), li.get(i));
						b.compress(CompressFormat.PNG, 100, fOut);
						fOut.flush();
						fOut.close();
						//Log.e("e1111",file.getAbsolutePath());
					}catch(IOException e){
						e.printStackTrace();
						//Log.e("e", e.getMessage());
					}
				}
			}
			
		} catch (Exception e) {
			// TODO: handle exception
		}

	}
}
