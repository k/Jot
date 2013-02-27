package com.kabudo.jot;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class EditActivity extends Activity {
	private EditText text;
	private Button colorChanger;
	private Long mRowId;
	private JotDbAdapter mDbHelper;
	public static final int limeGreen = Color.rgb(195,255,104);
	public static final int iceBlue = Color.rgb(15,232,247);
	public static final int hotPink = Color.rgb(253,12,108);
	public static final int nairYellow = Color.rgb(249,251,15);
	public static final int neonOrange = Color.rgb(255,161,20);
	public static final int sexRed = Color.rgb(240,35,17);
	private int colors[] = {limeGreen, iceBlue, hotPink, nairYellow, neonOrange, sexRed};
	private int theColor;
	private int colorCounter = 0;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_screen);
		setTitle(R.string.edit_note);
		
		mDbHelper= new JotDbAdapter(this);
		mDbHelper.open();
		
		colorChanger = (Button) findViewById(R.id.color_changer);
		
		text = (EditText) findViewById(R.id.edit);
		
		 mRowId = (savedInstanceState == null) ? null :
	        	(Long) savedInstanceState.getSerializable(JotDbAdapter.KEY_ROWID);
	        if (mRowId == null) {
	        	Bundle extras = getIntent().getExtras();
	        	mRowId = extras != null ? extras.getLong(JotDbAdapter.KEY_ROWID) : null;
	        	
	        }
	        
	        populateFields();

	}
	
	public void saveNote(View v) {
		setResult(RESULT_OK);
		//Intent i = new Intent(getApplicationContext(), JotNoteActivity.class);
		//i.putExtra("note", text.getText());
		//startActivity(i);
		finish();
		
	}
	
	public void populateFields() {
		if (mRowId != null) {
    		Cursor note = mDbHelper.fetchJot(mRowId);
    		text.setText(note.getString(note.getColumnIndexOrThrow(JotDbAdapter.KEY_BODY)));
    		setColor(Integer.parseInt(note.getString(note.getColumnIndexOrThrow(
    				JotDbAdapter.KEY_COLOR))));
    		switch (theColor) {
    		case (Color.RED): colorCounter = 1;
    		case (Color.BLUE): colorCounter = 2;
    		case (Color.GREEN): colorCounter = 3;
    		case (Color.CYAN): colorCounter = 4;
    		case (Color.MAGENTA): colorCounter = 5;
    		}
		}
		else setColor(limeGreen);
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
    	String body = text.getText().toString();
    	
    	if (mRowId == null) {
    		long id = mDbHelper.createJot("note",  body, "" + theColor);
    		if (id > 0) {
    			mRowId = id;
    		}
    	} else {
    		mDbHelper.updateJot(mRowId, "note", body, "" + theColor);
    	}
    	
    }
    
    private void setColor(int color) {
    	text.setBackgroundColor(color);
    	colorChanger.setBackgroundColor(color);
    	theColor = color;
    }
    
    public void changeColor(View v) {
    	if (colorCounter<5) {
    		setColor(colors[colorCounter++ + 1]);
    	} else { 
    		colorCounter = 0;
    		setColor(colors[colorCounter]);
    	}
    	
    }

}
