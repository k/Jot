package com.kabudo.jot;

import com.kabudo.jot.JotActivity.MyGestureListener;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

public class JotAdapter extends CursorAdapter {
	private Cursor cur;
	private SimpleOnGestureListener mListener;
	
	public JotAdapter(Context c, Cursor cursor, int flags) {
		super(c, cursor, flags);
		cur = cursor;
	}
	
	@Override
	public int getCount() {
		int count = cur.getCount();
		return count;
	}

	public Object getItem(int position) {
		//TODO: figure this out
		return super.getItem(position);
	}

	public long getItemId(int position) {
		Cursor cursor = (Cursor) getItem(position);
		return cursor.getInt(cursor.getColumnIndexOrThrow(JotDbAdapter.KEY_ROWID));
	}

	

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		if (cursor.getString(cursor.getColumnIndexOrThrow(JotDbAdapter.KEY_TITLE)).equals("note")) {
			((TextView) view).setText(cursor.getString(
					cursor.getColumnIndexOrThrow(JotDbAdapter.KEY_BODY)));
			((TextView) view).setBackgroundColor(cursor.getInt(
					cursor.getColumnIndexOrThrow(JotDbAdapter.KEY_COLOR)));
		} else {
			((ImageView) view).setImageURI(Uri.parse(cursor.getString(
					cursor.getColumnIndexOrThrow(JotDbAdapter.KEY_COLOR))));
		}
		
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		if (cursor.getString(cursor.getColumnIndexOrThrow(JotDbAdapter.KEY_TITLE)).equals("note")) {
			TextView textView;
			textView = new TextView(context);
			textView.setLayoutParams(new GridView.LayoutParams(225, 225));
			textView.setPadding(4, 4, 2, 4);
			textView.setText(cursor.getString(cursor.getColumnIndexOrThrow(
				JotDbAdapter.KEY_BODY)));
			textView.setBackgroundColor(cursor.getInt(cursor.getColumnIndexOrThrow(
				JotDbAdapter.KEY_COLOR)));
			textView.setOnTouchListener((MyGestureListener) mListener);
			return textView;
		}
		else if (cursor.getString(cursor.getColumnIndexOrThrow(JotDbAdapter.KEY_TITLE)).equals("pic")) {
			ImageView imageView;
			imageView = new ImageView(context);
			imageView.setLayoutParams(new GridView.LayoutParams(225, 225));
			imageView.setPadding(4, 4, 2, 4);
			imageView.setImageURI(Uri.parse(cursor.getString(cursor.getColumnIndexOrThrow(
					JotDbAdapter.KEY_COLOR))));
			imageView.setOnTouchListener((MyGestureListener) mListener);
			return imageView;
		} else {
			TextView error = new TextView(context);
			error.setText("There was an error loading this item.");
			return error;
		}
	}
}
