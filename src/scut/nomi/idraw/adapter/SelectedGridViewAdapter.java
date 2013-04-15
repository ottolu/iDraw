package scut.nomi.idraw.adapter;

import java.util.ArrayList;
import java.util.List;

import scut.nomi.idraw.R;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class SelectedGridViewAdapter extends BaseAdapter {

	private List<Integer> icons;
	private List<ImageView> selectedList;
	private LayoutInflater inflater;

	private int selected = -1;
	private int ti = 0;

	private Activity activity;

	/* 构造符 */
	public SelectedGridViewAdapter(Activity con, List<Integer> icon) {
		activity = con;
		icons = icon;
		inflater = (LayoutInflater) con
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		selectedList = new ArrayList<ImageView>(icons.size());
		for (int i = 0; i != icons.size(); ++i)
			selectedList.add(null);
	}

	/**
	 * 更新选中的。
	 * 
	 * @param s 表示新选中的点
	 */
	public void update(int s) {
		Log.e("e", "s:" + s + ",selected:" + selected);
		if (selected != -1) {
			Log.e("e1", "s:" + s + ",selected:" + selected);
			selectedList.get(selected).setAlpha(0);
		}
		Log.e("e1", "s:" + s + ",selected:" + selected);
		selected = s;

		selectedList.get(selected).setAlpha(255);
		activity.openOptionsMenu();
	}

	@Override
	public int getCount() {
		return selectedList.size();
	}

	@Override
	public Object getItem(int arg0) {
		return selectedList.get(arg0);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View v = (View) inflater.inflate(R.layout.grid_child_view, null);
		final int i = position;
		ImageView iv1 = (ImageView) v.findViewById(R.id.grid_selected);
		ImageView iv2 = (ImageView) v.findViewById(R.id.grid_image);
		iv2.setImageResource(icons.get(position));

		if (position == 0) {
			if (ti++ == 1) {
				//Log.e("new set", "`1");
				selectedList.set(position, iv1);
				selectedList.get(position).setAlpha(0);
			}
		} else {
			selectedList.set(position, iv1);
			selectedList.get(position).setAlpha(0);
		}
		
		//Log.e("e", "where is wrong" + selectedList.size() + "," + i);

		iv2.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.e("e", "onclick iv2" + i);
				update(i);
			}
		});
		return v;
	}

	public int getSelected() {
		return selected;
	}
}