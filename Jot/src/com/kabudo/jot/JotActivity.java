package com.kabudo.jot;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CursorAdapter;
import android.widget.GridView;
import android.widget.Toast;

public class JotActivity extends Activity {
	boolean mExternalStorageAvailable = false;
    boolean mExternalStorageWriteable = false;
    
	private static final int ACTIVITY_CREATE=0;
    private static final int ACTIVITY_EDIT=1;
    private static final int ACTIVITY_SHARE=2;
    private static final int ACTIVITY_PIC=3;

    private static final int INSERT_NOTE_ID = Menu.FIRST;
    private static final int DELETE_ID = Menu.FIRST + 1;
    private static final int SHARE_ID = Menu.FIRST + 2;
    private static final int INSERT_PIC_ID = Menu.FIRST + 3;

	private JotDbAdapter mDbHelper;
	private GridView gridview;
	protected MyGestureListener myGestureListener;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        checkExStorage();
        
        
        //myGestureListener = new MyGestureListener(this);
        
        mDbHelper = new JotDbAdapter(this);
        mDbHelper.open();
        
        gridview = (GridView) findViewById(R.id.grid);
        fillData();
        
        
        gridview.setOnItemClickListener(new OnItemClickListener() {
        	public void onItemClick(AdapterView<?> parent, View v, 
        			int position, long id) {
        		//Toast.makeText(JotNoteActivity.this, "" + position, Toast.LENGTH_SHORT).show();
        		editJot(id);
        	}
        });
        //gridview.setOnTouchListener(myGestureListener);
        registerForContextMenu(gridview);
    }
    /*
    @Override
    public boolean onTouchEvent(MotionEvent event){
    	return myGestureListener.getDetector().onTouchEvent(event);
    }*/
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	super.onCreateOptionsMenu(menu);
    	menu.add(0, INSERT_NOTE_ID, 0, R.string.add_label);
    	menu.add(0, INSERT_PIC_ID, 0, R.string.add_pic);
    	//MenuInflater inflater = getMenuInflater();
    	//inflater.inflate(R.menu.main_menu, menu);
    	return true;
    }
    
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
    	switch (item.getItemId()){
    	case INSERT_NOTE_ID: 
    		addNote(); 
    		return true;
    	case INSERT_PIC_ID:
    		addPic();
    		return true;
    	}
		return super.onMenuItemSelected(featureId, item);
    }
    
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, 
    		ContextMenuInfo menuInfo) {
    	super.onCreateContextMenu(menu, v, menuInfo);
    	menu.add(0, DELETE_ID, 0, R.string.delete_label);
    	menu.add(0, SHARE_ID, 0, R.string.share_label);
    	
    }
    
    @Override
    public boolean onContextItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
    	case DELETE_ID: 
    		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
            mDbHelper.deleteJot(info.id);
            fillData();
            return true;
    	case SHARE_ID:
    		AdapterContextMenuInfo info1 = (AdapterContextMenuInfo) item.getMenuInfo();
    		Cursor c = mDbHelper.fetchJot(info1.id);
    		String type = c.getString(c.getColumnIndexOrThrow(JotDbAdapter.KEY_TITLE));
    		Intent i = new Intent(android.content.Intent.ACTION_SEND);
    		if (type.equals("note")) {
    			i.setType("text/plain");
    			String body = c.getString(c.getColumnIndexOrThrow(JotDbAdapter.KEY_BODY));
    			i.putExtra(android.content.Intent.EXTRA_TEXT, body);
    			startActivity(Intent.createChooser(i, "Share via"));
    		}
    		else if (type.equals("pic")) {
    			i.setType("image/jpeg");
    			String caption = c.getString(c.getColumnIndexOrThrow(JotDbAdapter.KEY_BODY));
    			Uri uri = Uri.parse(c.getString(c.getColumnIndexOrThrow(
    					JotDbAdapter.KEY_COLOR)));
    			i.putExtra(android.content.Intent.EXTRA_TITLE, caption);
    			i.putExtra(android.content.Intent.EXTRA_STREAM, uri);
    			startActivity(Intent.createChooser(i, "Share via"));
    		}
    	}
    	return false;
    }
    
   /**
    * Called in Response to a touch on Add Note in the Menu
    * brings up an edit screen to add a new note
    */
    public void addNote() {
    	Intent i = new Intent(this, EditActivity.class);
		startActivityForResult(i, ACTIVITY_CREATE); 
    }
    
    /**
     * Called in response to a touch on Add Pic in the Menu.
     * Starts starst an ACTION_PICK activity to choose an existing photo
     * and add it to the GridView
     */
    public void addPic() {
    	if (mExternalStorageAvailable) {
    		startActivityForResult(new Intent(Intent.ACTION_PICK, 
            		android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI), ACTIVITY_PIC);
    	}
    	else Toast.makeText(this, "External Storage Unavailable!", Toast.LENGTH_SHORT);
    	
    }
    
    /**
     * Opens up the Jot edit screen
     * @param id the id of the jot within the GridView
     */
    public void editJot(long id) {
    	Cursor cursor = mDbHelper.fetchJot(id);
    	Intent i;
		if (cursor.getString(cursor.getColumnIndexOrThrow(JotDbAdapter.KEY_TITLE)).equals("note"))
			i = new Intent(this, EditActivity.class);
		else if (cursor.getString(cursor.getColumnIndexOrThrow(JotDbAdapter.KEY_TITLE)).equals("pic"))
			i = new Intent(this, EditPicActivity.class);
		//TODO when we do links
		else i = new Intent();
    	
        i.putExtra(JotDbAdapter.KEY_ROWID, id);
        startActivityForResult(i, ACTIVITY_EDIT);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode==ACTIVITY_PIC) {
        	Uri uri = intent.getData();
        	Intent i = new Intent(this, EditPicActivity.class);
        	i.putExtra("uri", uri.toString());
        	startActivityForResult(i, ACTIVITY_CREATE);
        }
        fillData();
    }
    
    /**
     * Fills the GridView with Jots
     */
    public void fillData() {
    	gridview.setAdapter(new JotAdapter(this, mDbHelper.fetchAllJots(), 
    			CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER));
    }
    
    /**
     * Checks to make sure the External Storage is available
     */
    public void checkExStorage() {
    	 //Check if External Storage is available
        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            // We can read and write the media
            mExternalStorageAvailable = mExternalStorageWriteable = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            // We can only read the media
            mExternalStorageAvailable = true;
            mExternalStorageWriteable = false;
        } else {
            // Something else is wrong. It may be one of many other states, but all we need
            //  to know is we can neither read nor write
            mExternalStorageAvailable = mExternalStorageWriteable = false;
        }
    }
    
    /**This is to respond to gestures. 
     * TODO We need to figure out how to find which view
     * within the gridView was touched
     * @author kennethbambridge
     */
    public class MyGestureListener extends SimpleOnGestureListener implements
	OnTouchListener {
    	Context mContext;
    	GestureDetector mDetector;
    	
    	public MyGestureListener() {
    		super();
    	}

    	public MyGestureListener(Context context) {
    		this(context, null);
    	}
    	
    	public MyGestureListener(Context context, GestureDetector gDetector) {
    		if (gDetector == null) gDetector = new GestureDetector(context, this);
    		mContext = context;
    		mDetector = gDetector;
    	}
    	
    	@Override
    	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
    		if (velocityX > 0) {
    			Toast.makeText(mContext, "fling right", Toast.LENGTH_SHORT).show();
    		}
    		else {
    			Toast.makeText(mContext, "fling left", Toast.LENGTH_SHORT).show();
    		}
    		return super.onFling(e1,  e2, velocityX, velocityY);
    	}
    	
    	@Override
    	public boolean onSingleTapConfirmed(MotionEvent e) {
    		Toast.makeText(mContext, "single click", Toast.LENGTH_SHORT).show();
    		editJot(e.getSource());
    		return super.onSingleTapConfirmed(e);
    	}

    	public boolean onTouch(View v, MotionEvent event) {
    		return mDetector.onTouchEvent(event);
    	}
    	
    	@Override
    	public boolean onDown(MotionEvent e) {
			return true;
    		
    	}
    	
    	public GestureDetector getDetector() {
    		return mDetector;
    	}

    }
}