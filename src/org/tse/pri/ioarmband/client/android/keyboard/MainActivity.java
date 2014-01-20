package org.tse.pri.ioarmband.client.android.keyboard;

import java.util.Set;

import org.tse.pri.ioarmband.client.android.connect.BluetoothUtil;
import org.tse.pri.ioarmband.client.android.connect.ConnectThread;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {

	Button btConnect;
	Button btDisconnect;
	Button btSend;
	TextView tvEtat;
	BluetoothAdapter bluetoothAdapter;
	
	private final static int REQUEST_CODE_ENABLE_BLUETOOTH = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		btConnect = (Button) findViewById(R.id.bt_connect);
		btConnect.setOnClickListener(clickBtConnect);
		btDisconnect = (Button) findViewById(R.id.bt_disconnect);
		btDisconnect.setOnClickListener(clickBtDisconnect);
		btSend = (Button) findViewById(R.id.bt_send);
		btSend.setOnClickListener(clickBtSend);
		tvEtat = (TextView) findViewById(R.id.tv_etat_bluetooth);

		
		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (bluetoothAdapter == null)
		{
			tvEtat.setText("pas de bluetooth");
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);

		return true;
	}
	
		

		
	private OnClickListener clickBtConnect = new OnClickListener() {
		public void onClick(View v) {
			tvEtat.setText("Button Connect click");
			
			if (!bluetoothAdapter.isEnabled()) {
				   Intent enableBlueTooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
				   startActivityForResult(enableBlueTooth, REQUEST_CODE_ENABLE_BLUETOOTH);
				}
			
			 
			   
			Set<BluetoothDevice> bluetoothDevices =  bluetoothAdapter.getBondedDevices();
			
			for (BluetoothDevice bluetoothDevice : bluetoothDevices) {
				boolean UuidsWithSdp = bluetoothDevice.fetchUuidsWithSdp();
				
				Log.d("MainActivity","Device : "+bluetoothDevice.getName());
				Log.d("MainActivity","UuidsWithSdp : "+UuidsWithSdp);
				
				ParcelUuid[] uuids = bluetoothDevice.getUuids();
				
				for (int i = 0; i < uuids.length; i++) {
					Log.d("MainActivity",uuids[i].toString());
					//bluetoothDevice.
					//UUID CLIENT_UUID = UUID.fromString("4c2054e4f8d33f530f79aa3b5712c799");
				
				
				}
				ConnectThread connect = new ConnectThread(bluetoothDevice);
				connect.start();
				Log.d("MainActivity"," ");
			}

		}
	};

	private OnClickListener clickBtDisconnect = new OnClickListener() {
		public void onClick(View v) {
			tvEtat.setText("Button Disconnect click");
			
		}
	};

	private OnClickListener clickBtSend = new OnClickListener() {
		public void onClick(View v) {
			tvEtat.setText("Button Send click");
		}
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	       super.onActivityResult(requestCode, resultCode, data);
	       if (requestCode != REQUEST_CODE_ENABLE_BLUETOOTH)
	           return;
	       if (resultCode == RESULT_OK) {
	    	 tvEtat.setText("Blutooth activé");// L'utilisation a activé le bluetooth
	       } else {
	          tvEtat.setText("Blutooth desactivé");// L'utilisation n'a pas activé le bluetooth
	       }    
	}
	
	
	@Override
	protected void onDestroy() {
		if(bluetoothAdapter.isDiscovering())
		{
			bluetoothAdapter.cancelDiscovery();
		}
		super.onDestroy();
		
	}
}
