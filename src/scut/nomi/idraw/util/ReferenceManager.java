/**
 * 
 */
package scut.nomi.idraw.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * @author Administrator
 * 
 */
public class ReferenceManager {
	private static final String TAG = "ReferenceManager";
	private static ReferenceManager instance = null;
	private SharedPreferences settings;

	private ReferenceManager(Context context) {
		settings = context.getSharedPreferences(Constants.PREF_NAME, 0);
	}

	public static synchronized ReferenceManager getInstance(Context context) {
		if (instance == null) {
			instance = new ReferenceManager(context);
		}
		return instance;
	}

	public SharedPreferences getSetting() {
		return settings;
	}

	private void commitBoolean(String key, boolean value) {
		settings.edit().putBoolean(key, value).commit();

	}

	private void commitString(String key, String value) {
		settings.edit().putString(key, value).commit();
	}

	private void commitInt(String key, int value) {
		settings.edit().putInt(key, value).commit();
	}

	private void commitFloat(String key, float value) {
		settings.edit().putFloat(key, value).commit();
	}

	private void commitLong(String key, long value) {
		settings.edit().putLong(key, value).commit();
	}

	public boolean isFirstRun() {
		return settings.getBoolean(Constants.IS_FIRST_RUN, true);
	}

	public void setFirstRun(boolean value) {
		commitBoolean(Constants.IS_FIRST_RUN, value);
	}

	public void setWeiboToken(String accessUserID, String accessTokenKey, String accessTokenSecret) {
		commitString(Constants.ACCESS_USER_ID, accessTokenKey);
		commitString(Constants.ACCESS_TOKEN_KEY, accessTokenKey);
		commitString(Constants.ACCESS_TOKEN_SECRET, accessTokenSecret);
	}

}
