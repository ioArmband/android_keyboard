package org.tse.pri.ioarmband.client.android.keyboard;

import org.tse.pri.ioarmband.client.android.connect.BluetoothConnectionManager;
import org.tse.pri.ioarmband.client.android.connect.BluetoothDiscoveryManager;
import org.tse.pri.ioarmband.client.android.connect.IBeginConnectionListener;
import org.tse.pri.ioarmband.io.connection.IConnectionListener;
import org.tse.pri.ioarmband.io.message.Command;

import android.bluetooth.BluetoothAdapter;
import android.inputmethodservice.InputMethodService;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;


public class ClavierService extends InputMethodService {

	BluetoothAdapter bluetoothAdapter;
	BluetoothConnectionManager bluetoothConnectionManager;
	MyKeyboardView inputView;
	@Override
	public View onCreateInputView() {
		inputView =  (MyKeyboardView) getLayoutInflater().inflate( R.layout.clavier, null);

		return inputView;
	}

	@Override

	public void onStartInputView(EditorInfo info, boolean restarting) {
		// TODO Auto-generated method stub
		super.onStartInputView(info, restarting);
		
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
		bluetoothConnectionManager.closeConnection();
		
	}
	
	
	
	
	private IConnectionListener connectionBluetooth = new IConnectionListener() {

		@Override
		public void onConnectionClose() {
			Log.d("MainActivity","onConnectionClose");	
			inputView.changeTextView("Connexion close");
		
		}

		@Override
		public void onCommandReiceved(Command arg0) {
			Log.d("MainActivity","onCommandReiceved");
		}
	};
	
	private IBeginConnectionListener connectionBegin = new IBeginConnectionListener() {	
		@Override
		public void onConnectionBegin() {
			Log.d("MainActivity","onConnectionBegin");
			inputView.changeTextView("Connexion done");
		}
	};
		

}
