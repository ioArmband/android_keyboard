package org.tse.pri.ioarmband.client.android.keyboard;

import org.tse.pri.ioarmband.client.android.connect.BluetoothConnectionManager;
import org.tse.pri.ioarmband.client.android.connect.ServiceConnection;
import org.tse.pri.ioarmband.io.message.AppMessage;
import org.tse.pri.ioarmband.io.message.CloseAppMessage;
import org.tse.pri.ioarmband.io.message.Command;
import org.tse.pri.ioarmband.io.message.GestureMessage;
import org.tse.pri.ioarmband.io.message.enums.GestureType;

import android.bluetooth.BluetoothAdapter;
import android.inputmethodservice.InputMethodService;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.Button;
import android.widget.TextView;


public class ClavierService extends InputMethodService implements ServiceConnection{

	BluetoothAdapter bluetoothAdapter;
	BluetoothConnectionManager bluetoothConnectionManager;
	Button btSend;
	MyKeyboardView inputView;
	InputConnection inputConnection;
	Handler mHandler;
	
	@Override
	public View onCreateInputView() {
		
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
		inputConnection = getCurrentInputConnection();
	
		
		bluetoothConnectionManager = BluetoothConnectionManager.getInstance();

		bluetoothConnectionManager.useConnection(this);
		
	}
	
	
	
	@Override
	public void onFinishInput() {
		// TODO Auto-generated method stub
		super.onFinishInput();
		Log.d("ClavierService","onFinishInput");	
		bluetoothConnectionManager = BluetoothConnectionManager.getInstance();
		bluetoothConnectionManager.removeUseConnection(this);
		
	}
	
	@Override
	public void onWindowShown() {
		// TODO Auto-generated method stub
		super.onWindowShown();
		Log.d("ClavierService","onWindowShown");
		
		if(bluetoothConnectionManager.isConnected())
		{
			Log.d("MainActivity","send AppMessage during onWindowShown");
			AppMessage msg = new AppMessage(AppMessage.AppStd.KEYBOARD_NUM);
			bluetoothConnectionManager.sendMessage(msg);
		}
		
	}
	
	@Override
	public void onWindowHidden() {
		super.onWindowHidden();
		Log.d("ClavierService","onWindowHidden");	
		if(bluetoothConnectionManager.isControlConnected(this))
		{
			CloseAppMessage msg = new CloseAppMessage();
			bluetoothConnectionManager.sendMessage(msg);
		}
		

	}

	private OnClickListener clickBtSend = new OnClickListener() {
		public void onClick(View v) {
			 InputConnection ic = getCurrentInputConnection();
			 
			 KeyEvent keyEvent = new KeyEvent(KeyEvent.FLAG_SOFT_KEYBOARD,KeyEvent.KEYCODE_ENTER);
			 ic.sendKeyEvent(keyEvent);

		}
	};
	


	@Override
	public void onCommandReiceved(Command command) {
		Log.d("ClavierService","onCommandReiceved");
		
		if(command.getClazz().equals(GestureMessage.class.getName()))
		{
			GestureMessage gestureMessage = (GestureMessage) command.getMessage();
			if(gestureMessage.getType() == GestureType.TOUCH)
			{
				String msg = gestureMessage.getSourceName();
				inputConnection.commitText(msg, 1);

			}
		}
	}

	@Override
	public void onConnectionClose() {
		Log.d("ClavierService","onConnectionClose");	
		inputView.changeTextView("Connexion close");
	}

	@Override
	public void onConnectionBegin() {

		Log.d("ClavierService","onConnectionBegin");
		inputView.changeTextView("Connexion done");

		AppMessage msg = new AppMessage(AppMessage.AppStd.KEYBOARD_NUM);
		bluetoothConnectionManager.sendMessage(msg);

	}

	@Override
	public void onWinControl() {
		AppMessage msg = new AppMessage(AppMessage.AppStd.KEYBOARD_NUM);
		bluetoothConnectionManager.sendMessage(msg);
		Log.d("ClavierService","showWindow = true");
		showWindowT(true);
		
		
	}
	public void showWindowT(final Boolean isVisible)
	{
		
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

	@Override
	public void onLoseControl() {
		Log.d("ClavierService","showWindow = false");
		//showWindowT(false);
		hideWindow();
		
	}
}