package com.orange.labs.uk.orangizer.activities;

import android.content.Intent;
import android.os.Bundle;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;
import com.orange.labs.uk.orangizer.R;
import com.orange.labs.uk.orangizer.R.layout;
import com.orange.labs.uk.orangizer.R.menu;
import com.orange.labs.uk.orangizer.utils.Constants;

public class EventsActivity extends SherlockActivity {

	private Facebook mFacebook = new Facebook(Constants.FACEBOOK_APP_ID);
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);
        
        mFacebook.authorize(this, new FacebookDialogListener());
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
    	
    	// Authorization callback for Facebook authentication when coming back from their app.
    	mFacebook.authorizeCallback(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.activity_events, menu);
        return true;
    }
    
    private class FacebookDialogListener implements DialogListener {

		@Override
		public void onComplete(Bundle values) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onFacebookError(FacebookError e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onError(DialogError e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onCancel() {
			// TODO Auto-generated method stub
			
		}
    	
    }
}
