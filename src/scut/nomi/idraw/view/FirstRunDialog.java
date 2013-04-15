/**
 * 
 */
package scut.nomi.idraw.view;

import oauth.signpost.OAuthProvider;
import oauth.signpost.basic.DefaultOAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import scut.nomi.idraw.R;
import scut.nomi.idraw.util.Constants;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * @author Administrator
 * 
 */
public class FirstRunDialog extends Dialog {
	private Activity activity;
	private Button btnOk;
    
    public static CommonsHttpOAuthConsumer httpOauthConsumer;
    public static OAuthProvider httpOauthprovider;
	
	public FirstRunDialog(Activity activity) {
		super(activity);
		this.activity = activity;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.first_run);
		System.setProperty("weibo4j.oauth.consumerKey", Constants.consumerKey);
		System.setProperty("weibo4j.oauth.consumerSecret", Constants.consumerSecret);
		btnOk = (Button) findViewById(R.id.first_run_btn);
		btnOk.setOnClickListener(mOncliClickListener);
	}

	private View.OnClickListener mOncliClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.first_run_btn: 
				try{
		        	httpOauthConsumer = new CommonsHttpOAuthConsumer(Constants.consumerKey, Constants.consumerSecret);
		    		httpOauthprovider = new DefaultOAuthProvider("http://api.t.sina.com.cn/oauth/request_token","http://api.t.sina.com.cn/oauth/access_token","http://api.t.sina.com.cn/oauth/authorize");
		    		String authUrl = httpOauthprovider.retrieveRequestToken(httpOauthConsumer, Constants.callBackUrl);
		    		activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(authUrl)));
		    		FirstRunDialog.this.dismiss();
		    		try {
						activity.finish();
					} catch (Exception e) {
						e.printStackTrace();
					}
		    	}catch(Exception e){
		    		e.printStackTrace();
		    	}
				break;
			default:
				break;
			}
		}
	};

}
