package com.kabudo.jot;

import android.app.Activity;
import android.os.Bundle;

/*TODO
 * This class is going to be the activity that allows the user to share
 * to various social networks customizable by the user
 */
public class ShareActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.share_screen);
		
		
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		setResult(RESULT_OK);
		finish();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		setResult(RESULT_OK);
		finish();
	}

}
