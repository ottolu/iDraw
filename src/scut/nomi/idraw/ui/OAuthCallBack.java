package scut.nomi.idraw.ui;

import java.util.SortedSet;

import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;
import scut.nomi.idraw.util.ReferenceManager;
import scut.nomi.idraw.view.FirstRunDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

public class OAuthCallBack extends BaseActivity {
	private static String TAG = "OAuthCallBack";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		Uri uri = this.getIntent().getData();
		try {
			String verifier = uri
					.getQueryParameter(oauth.signpost.OAuth.OAUTH_VERIFIER);
			try {
				FirstRunDialog.httpOauthprovider.setOAuth10a(true);
				FirstRunDialog.httpOauthprovider.retrieveAccessToken(FirstRunDialog.httpOauthConsumer,
						verifier);
			} catch (OAuthMessageSignerException ex) {
				ex.printStackTrace();
			} catch (OAuthNotAuthorizedException ex) {
				ex.printStackTrace();
			} catch (OAuthExpectationFailedException ex) {
				ex.printStackTrace();
			} catch (OAuthCommunicationException ex) {
				ex.printStackTrace();
			}
			SortedSet<String> user_id = FirstRunDialog.httpOauthprovider
					.getResponseParameters().get("user_id");
			String userId = user_id.first(), userKey = FirstRunDialog.httpOauthConsumer
					.getToken(), userSecret = FirstRunDialog.httpOauthConsumer
					.getTokenSecret();

			//Toast.makeText(this, userId +"\n"+ userKey +"\n"+ userSecret, Toast.LENGTH_SHORT).show();
			
			ReferenceManager rm = ReferenceManager
					.getInstance(OAuthCallBack.this);
			rm.setWeiboToken(userId, userKey, userSecret);
			rm.setFirstRun(false);
			if (!"".equals(user_id)) {
				Toast.makeText(this, "认证成功", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(this, "认证失败", Toast.LENGTH_SHORT).show();
			}
			OAuthCallBack.this.startActivity(new Intent(this, CoverActivity.class));
			OAuthCallBack.this.finish();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
}
