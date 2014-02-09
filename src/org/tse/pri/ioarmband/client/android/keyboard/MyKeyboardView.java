package org.tse.pri.ioarmband.client.android.keyboard;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MyKeyboardView extends LinearLayout{
	
	Handler mHandler;
	
	public MyKeyboardView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// TODO Auto-generated constructor stub
	}
	public MyKeyboardView(Context context) {
		super(context);
		init();
		// TODO Auto-generated constructor stub
	}
	public MyKeyboardView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
		mHandler = new Handler();
	}
	
	public void changeTextView(final String message)
	{
		
		runOnUiThread(new Runnable() {
		     @Override   
		     public void run() {
		    	 TextView tvEtat = (TextView) findViewById(R.id.tv_etat_bluetooth);
		 		tvEtat.setText(message);
		    }
		});
		
	}

	
	
	private void runOnUiThread(Runnable runnable) {
		mHandler.post(runnable);
    }
	
	private void init()
	{
		
	}

}
