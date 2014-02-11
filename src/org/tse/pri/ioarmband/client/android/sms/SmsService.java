package org.tse.pri.ioarmband.client.android.sms;

import org.tse.pri.ioarmband.client.android.connect.BluetoothAndroidConnectionManager;
import org.tse.pri.ioarmband.io.connection.manager.ServiceConnection;
import org.tse.pri.ioarmband.io.message.Command;
import org.tse.pri.ioarmband.io.message.GestureMessage;
import org.tse.pri.ioarmband.io.message.Message;
import org.tse.pri.ioarmband.io.message.enums.GestureType;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;



public class SmsService extends Service  implements ServiceConnection{

	  private SmsReceiver smsReceiver;
	  private SmsService smsService;
	  private IntentFilter intentFilter;
	  private Message lastMessage;
	  
		BluetoothAndroidConnectionManager bluetoothConnectionManager = null;
		BluetoothAdapter bluetoothAdapter = null;
		
	@Override
	public IBinder onBind(Intent intent) {
		Log.d("SmsService","SmsService onBind");
		return null;
	}
	
	
	@Override
	public void onCreate() {
		
		super.onCreate();
		smsService = this;
		Log.d("SmsService","SmsService onCreate");
		Toast.makeText(this, "SmsService onCreate", Toast.LENGTH_SHORT).show();
		smsReceiver = new SmsReceiver();
		smsReceiver.addSmsRecevierListener(smsRecevierListener);
		intentFilter = new IntentFilter();
		intentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
		registerReceiver(smsReceiver, intentFilter);
		lastMessage = null;
		
		bluetoothConnectionManager = BluetoothAndroidConnectionManager.getInstance();
		
	}

	
	
	
	@Override
	public void onDestroy() {
		Toast.makeText(this, "SmsService onDestroy", Toast.LENGTH_SHORT).show();
		bluetoothConnectionManager.removeUseConnection(smsService);
		super.onDestroy();
		  unregisterReceiver(smsReceiver);
	}
	
	
	
	
	private SmsRecevierListener smsRecevierListener = new SmsRecevierListener() {
		@Override
		public void onSmsReceived() {
			Log.d("SmsService","onSmsReceived");
			
			if(bluetoothConnectionManager.isCurrentServiceControl(smsService) )
			{
				Log.d("SmsService","sendSmsToioArmband");
				sendSmsToioArmband();
			}
			else
			{
				Log.d("SmsService","useConnection");
				bluetoothConnectionManager.useConnection(smsService);
			}
		}
	};
	
	
	private void sendSmsToioArmband()
	{
		Log.d("SmsService","sendSmsToioArmband");
		while(smsReceiver.sizeMessage() >0)
		{
			Log.d("SmsService","send");
			Message msg = smsReceiver.getAndRemoveFirstMessages();
			bluetoothConnectionManager.sendMessage(msg);
			lastMessage = msg;
		}

	}


	@Override
	public void onCommandReiceved(Command command) {
		Log.d("SmsService","onCommandReiceved");
		
		if(command.getClazz().equals(GestureMessage.class.getName()))
		{
			GestureMessage gestureMessage = (GestureMessage) command.getMessage();
			if(gestureMessage.getType() == GestureType.SWIPE)
			{
				
				String msg = gestureMessage.getSourceName();
				if(msg.equals("bottom"))
				{
					bluetoothConnectionManager.removeUseConnection(this);
				}
				
			}
		}
	}


	@Override
	public void onConnectionClose() {
		Log.d("SmsService","onConnectionClose");	
		
	}

	@Override
	public void onWinControl() {
		
		if(smsReceiver.sizeMessage()>0)
		{
			sendSmsToioArmband();
		}else if(lastMessage != null)
			{
				bluetoothConnectionManager.sendMessage(lastMessage);
			
		}
	}


	@Override
	public void onLoseControl() {
		
		
	}


	@Override
	public void onConnectionStarted() {
		Log.d("SmsService","onConnectionStarted");

	
		
	}
}
