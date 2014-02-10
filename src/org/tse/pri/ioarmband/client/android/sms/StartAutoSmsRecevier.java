package org.tse.pri.ioarmband.client.android.sms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class StartAutoSmsRecevier extends BroadcastReceiver {

	 @Override
	    public void onReceive(Context context, Intent intent) {
		 
		 	Log.d("StartAutoSmsRecevier","StartAutoSmsRecevier onReceive");
		 	Toast.makeText(context, "StartAutoSmsRecevier onReceive", Toast.LENGTH_SHORT).show();
	        Intent service = new Intent(context, SmsService.class);
	        context.startService(service);
	        
	    }
	 
}
