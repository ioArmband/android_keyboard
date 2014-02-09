package org.tse.pri.ioarmband.client.android.keyboard;

import org.tse.pri.ioarmband.client.android.connect.BluetoothConnectionManager;
import org.tse.pri.ioarmband.client.android.connect.BluetoothDiscoveryManager;
import org.tse.pri.ioarmband.client.android.connect.IBeginConnectionListener;
import org.tse.pri.ioarmband.io.connection.IConnectionListener;
import org.tse.pri.ioarmband.io.connection.StreamedConnection;
import org.tse.pri.ioarmband.io.message.AppMessage;
import org.tse.pri.ioarmband.io.message.Command;
import org.tse.pri.ioarmband.io.message.GestureMessage;
import org.tse.pri.ioarmband.io.message.KeyboardAppMessage;
import org.tse.pri.ioarmband.io.message.enums.GestureType;

import android.bluetooth.BluetoothAdapter;
import android.inputmethodservice.InputMethodService;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.Button;


public class ClavierService extends InputMethodService {

	BluetoothAdapter bluetoothAdapter;
	BluetoothConnectionManager bluetoothConnectionManager;
	Button btSend;
	MyKeyboardView inputView;
	InputConnection inputConnection;
	
	@Override
	public View onCreateInputView() {
		
		inputView =  (MyKeyboardView) getLayoutInflater().inflate( R.layout.clavier, null);

		
		btSend = (Button) inputView.findViewById(R.id.bt_send);
		btSend.setOnClickListener(clickBtSend);
		
		return inputView;
	}

	@Override

	public void onStartInputView(EditorInfo info, boolean restarting) {
		// TODO Auto-generated method stub
		super.onStartInputView(info, restarting);
		inputConnection = getCurrentInputConnection();
		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		
		if (bluetoothAdapter == null)
		{
			//tvEtat.setText("pas de bluetooth");
		}
		
		bluetoothConnectionManager = BluetoothConnectionManager.getInstance();
		
		if (bluetoothAdapter.isEnabled()) {
			BluetoothDiscoveryManager.startdiscoveryDevice();
		}
		
		bluetoothConnectionManager.addConnectionListener(connectionBluetooth);
		bluetoothConnectionManager.addBeginConnectionListener(connectionBegin);	
	}
	
	
	
	@Override
	public void onFinishInput() {
		// TODO Auto-generated method stub
		super.onFinishInput();
		if(bluetoothConnectionManager != null)
			bluetoothConnectionManager.closeConnection();
		
	}
	
	
	
	
	private IConnectionListener connectionBluetooth = new IConnectionListener() {

		@Override
		public void onConnectionClose() {
			Log.d("MainActivity","onConnectionClose");	
			inputView.changeTextView("Connexion close");
		
		}

		@Override
		public void onCommandReiceved(Command command) {
			Log.d("MainActivity","onCommandReiceved");
			
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
	};
	

	private OnClickListener clickBtSend = new OnClickListener() {
		public void onClick(View v) {
			 InputConnection ic = getCurrentInputConnection();
			 KeyEvent keyEvent = new KeyEvent(KeyEvent.FLAG_SOFT_KEYBOARD,KeyEvent.KEYCODE_ENTER);
			 ic.sendKeyEvent(keyEvent);

		}
	};
	
	private IBeginConnectionListener connectionBegin = new IBeginConnectionListener() {	
		@Override
		public void onConnectionBegin() {
			Log.d("MainActivity","onConnectionBegin");
			inputView.changeTextView("Connexion done");
			

			AppMessage msg = new AppMessage(AppMessage.AppStd.KEYBOARD_NUM);
			StreamedConnection streamedConnection = bluetoothConnectionManager.getStreamConnection();
			
			if(streamedConnection != null)
			{
				streamedConnection.sendCommand(new Command(msg));
			}
			
		}
	};
}