package scut.nomi.idraw.ui;

import scut.nomi.idraw.R;
import scut.nomi.idraw.adapter.SelectedGridViewAdapter;
import scut.nomi.idraw.util.Constants;
import scut.nomi.idraw.util.ImageUtil;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridView;
import android.widget.Toast;

/**
 * @author houzhi
 * 
 */
public class SelectActivity extends BaseActivity {
	private SelectedGridViewAdapter adapter;
	private GridView gv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.select_board);

		gv = (GridView) findViewById(R.id.select_gridview);

		adapter = new SelectedGridViewAdapter(this, ImageUtil.getImageValues());
		gv.setAdapter(adapter);

		//Log.e("e", "where is wrong");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 0, 0, R.string.sure);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int selected = adapter.getSelected();
		if (selected != -1) {
			Intent intent = new Intent(SelectActivity.this, DrawActivity.class);
			Bundle bundle = new Bundle();
			bundle.putBoolean(Constants.NEW_DRAW, true);

			if (selected != 4) {
				bundle.putString(Constants.EDIT_DRAW, Constants.PATH + "/"
						+ Constants.BOARD + "/" + selected + ".png");
			}

			intent.putExtras(bundle);
			startActivity(intent);
			SelectActivity.this.finish();
			return true;
		} else {
			Toast.makeText(this, R.string.plase_select, Toast.LENGTH_SHORT)
					.show();
			return false;
		}

	}
}
