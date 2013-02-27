package com.kabudo.jot;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

public class EditPicActivity extends Activity {
	private JotDbAdapter mDbHelper;
	private EditText caption;
	private ImageView image;
	private Long mRowId;
	private Uri uri;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_pic);
		
		mDbHelper= new JotDbAdapter(this);
		mDbHelper.open();
		
		caption = (EditText) findViewById(R.id.caption);
		image = (ImageView) findViewById(R.id.image);
		
		 mRowId = (savedInstanceState == null) ? null :
	        	(Long) savedInstanceState.getSerializable(JotDbAdapter.KEY_ROWID);
	        if (mRowId == null) {
	        	Bundle extras = getIntent().getExtras();
	        	mRowId = extras.getLong(JotDbAdapter.KEY_ROWID) != 0 ? 
	        			extras.getLong(JotDbAdapter.KEY_ROWID) : null;
	        	
	        }
	        
	        populateFields();
		
	}
	
	public void savePic(View v) {
		setResult(RESULT_OK);
		finish();
	}
	
	//sets the initial values from the database to the view
	public void populateFields() {
		//TODO access and set picture Uri to ImageView
		if (mRowId!=null) {
			Cursor pic = mDbHelper.fetchJot(mRowId);
			caption.setText(pic.getString(pic.getColumnIndexOrThrow(JotDbAdapter.KEY_BODY)));
			uri = Uri.parse(pic.getString(
					pic.getColumnIndexOrThrow(JotDbAdapter.KEY_COLOR)));
			image.setImageURI(uri);
		} else {
			uri = Uri.parse(getIntent().getStringExtra("uri"));
			image.setImageURI(uri);
		}
	}
	
	 @Override
	    protected void onSaveInstanceState(Bundle outState) {
	    	super.onSaveInstanceState(outState);
	    	saveState();
	    	outState.putSerializable(JotDbAdapter.KEY_ROWID, mRowId);
	    }
	    
	    @Override
	    protected void onPause() {
	    	super.onPause();
	    	saveState();
	    }
	    
	    @Override
	    protected void onResume() {
	    	super.onResume();
	    	populateFields();
	    }
	    
	    private void saveState() {
	    	String body = caption.getText().toString();
	    	
	    	if (mRowId == null) {
	    		long id = mDbHelper.createJot("pic",  body, uri.toString());
	    		if (id > 0) {
	    			mRowId = id;
	    		}
	    	} else {
	    		mDbHelper.updateJot(mRowId, "pic", body, uri.toString());
	    	}
	    	
	    }

}
