package scut.nomi.idraw.util;

import java.io.File;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.os.Environment;

public class SBUtil {

	private static Random rm = new Random();
	private static String chartable[] = { "a", "A", "b", "B", "c", "C", "D",
			"d", "E", "e", "F", "f", "G", "g", "H", "h", "J", "j", "K", "k",
			"L", "M", "m", "N", "n", "P", "p", "Q", "R", "T", "t", "Z", "1",
			"2", "3", "4", "5", "6", "7", "8", "9" };

	public static String getRandomPwd() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 6; i++) {
			sb.append(getRandomIntNum(9));
		}
		return sb.toString();
	}

	public static String getRandomId() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 6; i++) {
			sb.append(chartable[getRandomIntNum(chartable.length)]);
		}
		return sb.toString();
	}

	private static int getRandomIntNum(int limit) {
		return rm.nextInt(limit);
	}

	public static String getFileName() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss_SS");
		String fileName = format.format(new Timestamp(System
				.currentTimeMillis()));
		return fileName;
	}

	public static String getWorkPath() {
		return getSDPath() + Constants.FOLDER_ROUTE;
	}

	public static String getSDPath() {

		String state = Environment.getExternalStorageState();

		if (Environment.MEDIA_MOUNTED.equals(state)) {
			if (Environment.getExternalStorageDirectory().canWrite()) {
				return Environment.getExternalStorageDirectory().getPath();
			}
		}
		return null;
	}

	public static List<String> getFileDir(String filePath) {

		List<String> pathsList = new ArrayList<String>();

		File f = new File(filePath);
		File[] files = f.listFiles();

		for (int i = 0; i < files.length; i++) {
			String filename = files[i].getName();
			if (filename.endsWith(".png") || filename.endsWith(".jpg") || filename.endsWith(".bmp")) {
				pathsList.add(files[i].getPath());
			}
		}
		
		return pathsList;
	}
}
