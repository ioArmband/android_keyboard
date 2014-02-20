package org.ioarmband.client.android.sms;

import org.ioarmband.android.connection.aidl.IRemoteCommunicationService;
import org.ioarmband.android.connection.aidl.RemoteCommunicationServiceListener;
import org.ioarmband.android.connection.message.impl.android.GestureMessageAndroid;
import org.ioarmband.android.connection.message.impl.android.MessageAndroid;
import org.ioarmband.android.connection.message.impl.android.MessageContainer;
import org.ioarmband.android.connection.message.impl.android.TextMessageAppMessageAndroid;
import org.ioarmband.client.android.keyboard.ClavierService;
import org.ioarmband.net.message.enums.GestureType;
import org.ioarmband.net.message.impl.GestureMessage;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;



public class SmsService extends Service{

	  private SmsReceiver smsReceiver;
	  private IntentFilter intentFilter;
	  private MessageAndroid lastMessage;
	  

		
		protected IRemoteCommunicationService sCommunicationService;
		boolean hasControl;
		
		
	@Override
	public IBinder onBind(Intent intent) {
		Log.d("SmsService","SmsService onBind");
		return null;
	}
	
	
	@Override
	public void onCreate() {
		hasControl = false;
		super.onCreate();
		
		Log.d("SmsService","SmsService onCreate");
		Toast.makeText(this, "SmsService onCreate", Toast.LENGTH_SHORT).show();
		smsReceiver = new SmsReceiver();
		smsReceiver.addSmsRecevierListener(smsRecevierListener);
		intentFilter = new IntentFilter();
		intentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
		registerReceiver(smsReceiver, intentFilter);
		lastMessage = null;
		
		initConnection();

	}

	
	
	
	@Override
	public void onDestroy() {
		Toast.makeText(this, "SmsService onDestroy", Toast.LENGTH_SHORT).show();
		
		try {
			sCommunicationService.unUseConnection(serviceOutConnection);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		
		super.onDestroy();
		unregisterReceiver(smsReceiver);
	}
	
	
	
	
	private SmsRecevierListener smsRecevierListener = new SmsRecevierListener() {
		@Override
		public void onSmsReceived() {
			Log.d("SmsService","onSmsReceived");
			
			if(hasControl)
			{
				Log.d("SmsService","sendSmsToioArmband");
				sendSmsToioArmband();
			}
			else
			{
				Log.d("SmsService","useConnection");
				try {
					sCommunicationService.useConnection(serviceOutConnection);
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	};
	
	
	private void sendSmsToioArmband()
	{
		Log.d("SmsService","sendSmsToioArmband");
		while(smsReceiver.sizeMessage() >0)
		{
			Log.d("SmsService","send");
			TextMessageAppMessageAndroid textMessageAppMessageAndroid = new TextMessageAppMessageAndroid(smsReceiver.getAndRemoveFirstMessages());
								
			MessageContainer messageContainer = new MessageContainer();
			messageContainer.setMessageAndroid(textMessageAppMessageAndroid);
			
			try {
				sCommunicationService.sendMessage(messageContainer);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			lastMessage = textMessageAppMessageAndroid;
		}

	}


	public void initConnection()
	{
		Log.d("MainActivity", "initConnection");
		
		 
		if(serviceConnection != null)
		{ 
			Log.d("MainActivity", "initConnection"+ IRemoteCommunicationService.class.getName());
			Intent it = new Intent();
			it.setAction("com.remote.service.CALCULATOR");
			// binding to remote service
			bindService(it, serviceConnection, Service.BIND_AUTO_CREATE);
		}
	}

	
	
	
	
	
	
	
	ServiceConnection serviceConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			Log.d("MainActivity", "onServiceConnected");
			
			sCommunicationService = IRemoteCommunicationService.Stub.asInterface(service);
			Log.d("MainActivity", "serviceOutConnection = "+serviceOutConnection);
			try {
				sCommunicationService.useConnection(serviceOutConnection);
			} catch (RemoteException e) {
				Log.e("MainActivity", e.toString());
				e.printStackTrace();
			}
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			sCommunicationService = null;
			Log.d("MainActivity", "onServiceDisconnected");
		}
	};
	
	
	
	
	
	
	
	
	
	
	RemoteCommunicationServiceListener serviceOutConnection = new RemoteCommunicationServiceListener.Stub()
	{

		@Override
		public void onConnectionStarted() throws RemoteException {
			Log.d("SmsService","onConnectionStarted");
		}

		@Override
		public void onWinControl() throws RemoteException {
			hasControl = true;
			if(smsReceiver.sizeMessage()>0)
			{
				sendSmsToioArmband();
			}else if(lastMessage != null)
				{
				MessageContainer messageContainer = new MessageContainer();
				messageContainer.setMessageAndroid(lastMessage);
				sCommunicationService.sendMessage(messageContainer);
			}	
		}

		@Override
		public void onCommandReiceved(MessageContainer command)
				throws RemoteException {
			Log.d("SmsService","onCommandReiceved");
			
			if(command.getClazz().equals(GestureMessageAndroid.class.getName()))
			{
				GestureMessageAndroid gestureMessage = (GestureMessageAndroid) command.getMessageAndroid();
				Log.d("SmsService","onCommandReiceved "+gestureMessage.getType()+ " "+gestureMessage.getSourceName());
				if(gestureMessage.getType() == GestureType.SWIPE)
				{
					String msg = gestureMessage.getSourceName();
					if(msg.equals("bottom"))
					{
						sCommunicationService.unUseConnection(serviceOutConnection);
					}
				}
			}
		}

		@Override
		public void onLoseControl() throws RemoteException {
			hasControl = false;
			Log.d("SmsService","onLoseControl");
			
		}

		@Override
		public void onConnectionClose() throws RemoteException {
			Log.d("SmsService","onConnectionClose");		
		}
		
		String clazz = SmsService.class.getName();
		
		@Override
		public boolean isEquals(String clazz) throws RemoteException {
			return this.clazz.equals(clazz);
		}

		@Override
		public boolean isEqual(RemoteCommunicationServiceListener arg0)
				throws RemoteException {
			isEquals(arg0.getIdClass());
			return false;
		}
		
		@Override
		public String getIdClass() throws RemoteException {
		
			return clazz;
		}
	};
}
