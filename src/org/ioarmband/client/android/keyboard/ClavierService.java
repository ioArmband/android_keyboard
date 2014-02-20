package org.ioarmband.client.android.keyboard;

import java.util.Random;

import org.ioarmband.android.connection.aidl.IRemoteCommunicationService;
import org.ioarmband.android.connection.aidl.RemoteCommunicationServiceListener;
import org.ioarmband.android.connection.message.impl.android.AppMessageAndroid;
import org.ioarmband.android.connection.message.impl.android.GestureMessageAndroid;
import org.ioarmband.android.connection.message.impl.android.MessageContainer;
import org.ioarmband.net.message.enums.GestureType;
import org.ioarmband.net.message.impl.AppMessage;
import org.ioarmband.net.message.impl.GestureMessage;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.inputmethodservice.InputMethodService;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.Button;



public class ClavierService extends InputMethodService {

	Button btSend;
	MyKeyboardView inputView;
	InputConnection inputConnection;
	Handler mHandler;
	
	protected IRemoteCommunicationService sCommunicationService;
	boolean hasControl;
	
	
	@Override
	public void onCreate() {
		Log.d("ClavierService","onCreate");
		super.onCreate(); 
		hasControl = false;
		initConnection();
	}
	
	@Override
	public View onCreateInputView() {
		Log.d("ClavierService","onCreateInputView");	
		inputView =  (MyKeyboardView) getLayoutInflater().inflate( R.layout.clavier, null);
		btSend = (Button) inputView.findViewById(R.id.bt_send);
		btSend.setOnClickListener(clickBtSend);
		mHandler = new Handler();
		
		return inputView;
	}

	@Override

	public void onStartInputView(EditorInfo info, boolean restarting) {
		// TODO Auto-generated method stub
		super.onStartInputView(info, restarting);
		Log.d("ClavierService","onStartInputView");	
		inputConnection = getCurrentInputConnection();
	
		
		try {
			if(sCommunicationService != null)
			{
				sCommunicationService.useConnection(serviceOutConnection);
			}
				
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		securityWindowsHidden = false;
	}
	
	
	
	@Override
	public void onFinishInput() {
		// TODO Auto-generated method stub
		super.onFinishInput();
		Log.d("ClavierService","onFinishInput");	
		
		try {
			if(sCommunicationService != null && serviceOutConnection!= null)
			{
				sCommunicationService.unUseConnection(serviceOutConnection);
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	boolean securityWindowShown;
	@Override
	public void onWindowShown() {
		// TODO Auto-generated method stub
		super.onWindowShown();
		Log.d("ClavierService","onWindowShown");
		
		if(!hasControl) //bluetoothConnectionManager.isConnected() && 
		{
			if(!securityWindowsHidden)
			{
				try {	
					if(sCommunicationService != null)
					{
						sCommunicationService.useConnection(serviceOutConnection);
					}
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		securityWindowsHidden = false;
	}
	
	boolean securityWindowsHidden;
	@Override
	public void onWindowHidden() {
		super.onWindowHidden();
		Log.d("ClavierService","onWindowHidden");	
		if(hasControl)
		{
			if(!securityWindowsHidden)
			{  
				try {
					if(sCommunicationService != null)
					{
						sCommunicationService.unUseConnection(serviceOutConnection);
					}
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		securityWindowsHidden = false;
	}

	private OnClickListener clickBtSend = new OnClickListener() {
		public void onClick(View v) {
			 InputConnection ic = getCurrentInputConnection();
			 
			 KeyEvent keyEvent = new KeyEvent(KeyEvent.FLAG_SOFT_KEYBOARD,KeyEvent.KEYCODE_ENTER);
			 ic.sendKeyEvent(keyEvent);

		}
	};
	
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
			Log.d("ClavierService","onConnectionStarted");
			inputView.changeTextView("Connexion started");
		}

		@Override
		public void onWinControl() throws RemoteException {
			hasControl = true;
			Log.d("ClavierService","onWinControl");
			inputView.changeTextView("Has control");
			
			AppMessageAndroid msg = new AppMessageAndroid(AppMessage.AppStd.KEYBOARD_NUM);
			
			MessageContainer messageContainer = new MessageContainer();
			messageContainer.setMessageAndroid(msg);
			
			sCommunicationService.sendMessage(messageContainer);
			Log.d("ClavierService","showWindow = true");
			
			if(!isInputViewShown())
			{
				securityWindowsHidden = true;
				showWindowT(true);
			}
		}

		@Override
		public void onLoseControl() throws RemoteException {
			hasControl = false;
			Log.d("ClavierService","onLoseControl");
			inputView.changeTextView("Lose control");
			//showWindowT(false);
			if(isInputViewShown())
			{
				securityWindowsHidden = true;
				hideWindow();
			}
			
		}
		
		@Override
		public void onCommandReiceved(MessageContainer command)
				throws RemoteException {
			Log.d("ClavierService","onCommandReiceved");
			
			if(command.getClazz().equals(GestureMessageAndroid.class.getName()))
			{
				GestureMessageAndroid gestureMessage = (GestureMessageAndroid) command.getMessageAndroid();
				if(gestureMessage.getType() == GestureType.TOUCH)
				{
					String msg = gestureMessage.getSourceName();
					inputConnection.commitText(msg, 1);

				}
			}
			
			
		}

		

		@Override
		public void onConnectionClose() throws RemoteException {

			Log.d("ClavierService","onConnectionClose");	
			inputView.changeTextView("Connexion close");
			
		}
		
	String clazz = ClavierService.class.getName();
		
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


	public void showWindowT(final Boolean isVisible) {

		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				showWindow(isVisible);
			}
		});

	}

	private void runOnUiThread(Runnable runnable) {
		mHandler.post(runnable);
    }


}