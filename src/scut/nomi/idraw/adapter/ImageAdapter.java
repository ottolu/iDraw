package scut.nomi.idraw.adapter;

import java.util.List;

import scut.nomi.idraw.R;
import scut.nomi.idraw.util.AsyncImageLoader;
import scut.nomi.idraw.util.AsyncImageLoader.ImageCallback;
import scut.nomi.idraw.view.CoverGallery;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader.TileMode;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class ImageAdapter extends BaseAdapter {

	private Context mContext;
	private ImageView addImage;
	private List<String> pathList;
	private int size = 0;
	
	private AsyncImageLoader asyncImageLoader;

	public ImageAdapter(Context c, List<String> _pathList) {
		mContext = c;
		pathList = _pathList;
		size = pathList.size() + 1;
		
		asyncImageLoader = new AsyncImageLoader();
		addImageInit();
	}

	public void rebuildAdapter(List<String> _pathList) {
		pathList = _pathList;
		size = pathList.size() + 1;
	}

	/**
	 * 初始化绿色十字的图片
	 * @author nomi
	 */
	private void addImageInit() {
		Bitmap originalImage = BitmapFactory.decodeResource(
				mContext.getResources(), R.drawable.blank);
		
		addImage = new ImageView(mContext);
		addImage.setImageBitmap(originalImage);
		addImage.setLayoutParams(new CoverGallery.LayoutParams(320, 480));
		// imageView.setScaleType(ScaleType.MATRIX);
		addImage.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
	}

	private Resources getResources() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getCount() {
		return size;
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		if (position == 0) {
			return addImage;
		}
		ImageView imageView = new ImageView(mContext);
		Bitmap cachedImage = asyncImageLoader.loadBitmap(pathList.get(position - 1),
				imageView, new ImageCallback() {

					@Override
					public void imageLoaded(Bitmap imageBitmap,
							ImageView imageView, String imageUrl) {
						// TODO Auto-generated method stub
						imageView.setImageBitmap(imageBitmap);
						imageView.setLayoutParams(new CoverGallery.LayoutParams(320, 480));
						imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
					}
				});
		if (cachedImage == null) {
			imageView.setImageResource(R.drawable.loading);
		} else {
			imageView.setImageBitmap(cachedImage);
			imageView.setLayoutParams(new CoverGallery.LayoutParams(320, 480));
			imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
		}
		return imageView;
	}

	public float getScale(boolean focused, int offset) {
		return Math.max(0, 1.0f / (float) Math.pow(2, Math.abs(offset)));
	}

}
