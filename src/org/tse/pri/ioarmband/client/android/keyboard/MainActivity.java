package org.tse.pri.ioarmband.client.android.keyboard;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tse.pri.ioarmband.client.android.connect.ConnectThread;
import org.tse.pri.ioarmband.client.android.connect.IBeginConnectionListener;
import org.tse.pri.ioarmband.client.android.connect.ManageBluetoothConnexion;
import org.tse.pri.ioarmband.io.connection.IConnectionListener;
import org.tse.pri.ioarmband.io.message.Command;
import org.tse.pri.ioarmband.io.message.GestureMessage;
import org.tse.pri.ioarmband.io.message.enums.GestureType;

import android.app.Activity;
import android.bluetooth.BluetoothA2dp;
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
	private static Logger logger = LoggerFactory.getLogger(MainActivity.class);
	
	private ManageBluetoothConnexion manageBluetoothConnexion;

	 
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
		
		manageBluetoothConnexion = ManageBluetoothConnexion.getInstance();
		
		manageBluetoothConnexion.addConnectionListener(connectionBluetooth);
		manageBluetoothConnexion.addBeginConnectionListener(connectionBegin);
		
	
		logger.info("My Application Created");
       
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);

		return true;
	}
	
		
	private IConnectionListener connectionBluetooth = new IConnectionListener() {

		@Override
		public void onConnectionClose() {
			Log.d("MainActivity","onConnectionClose");
			tvEtat.setText("Connexion close");
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
			
			runOnUiThread(new Runnable() {
			     @Override
			     public void run() {
			    	 tvEtat.setText("Connexion done");
			    }
			});
			
			
			
			
		}
	};
		
	private OnClickListener clickBtConnect = new OnClickListener() {
		public void onClick(View v) {
			//tvEtat.setText("Button Connect click");
			
			if (!bluetoothAdapter.isEnabled()) {
				   Intent enableBlueTooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
				   startActivityForResult(enableBlueTooth, REQUEST_CODE_ENABLE_BLUETOOTH);
				}
			
			 
			bluetoothAdapter.startDiscovery();
			
			
			Set<BluetoothDevice> bluetoothDevices =  bluetoothAdapter.getBondedDevices();
			
			for (BluetoothDevice bluetoothDevice : bluetoothDevices) {
				
				Log.d("MainActivity","Device : "+bluetoothDevice.getName());
				Log.d("MainActivity","Etat : "+bluetoothDevice.getBondState());
				
				ParcelUuid[] uuids = bluetoothDevice.getUuids();
				
				for (int i = 0; i < uuids.length; i++) {
					Log.d("MainActivity",uuids[i].toString());
					//TODO: gestion multi conexion
					if(uuids[i].toString().equals(ManageBluetoothConnexion.CLIENT_UUID.toString())){
						ConnectThread connect = new ConnectThread(bluetoothDevice);
						connect.start();
						
					}
				}
				Log.d("MainActivity"," ");
			}

		}
	};

	private OnClickListener clickBtDisconnect = new OnClickListener() {
		public void onClick(View v) {
			//tvEtat.setText("Button Disconnect click");
		
			manageBluetoothConnexion.closeConnection();
			
			Log.d("MainActivity","manageBluetoothConnexion.closeConnection() ");
		
		}
	};

	private OnClickListener clickBtSend = new OnClickListener() {
		public void onClick(View v) {
			
			
			GestureMessage msg = new GestureMessage();
			msg.setType(GestureType.TOUCH);
			msg.setSourceName("send via keyboard android");
			manageBluetoothConnexion.getStreamConnection().sendCommand(new Command(msg));
			
			
			
			
			
			
			
		}
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	       super.onActivityResult(requestCode, resultCode, data);
	       if (requestCode != REQUEST_CODE_ENABLE_BLUETOOTH)
	           return;
	       if (resultCode == RESULT_OK) {
	    	 tvEtat.setText("Blutooth activ�");// L'utilisation a activ� le bluetooth
	       } else {
	          tvEtat.setText("Blutooth desactiv�");// L'utilisation n'a pas activ� le bluetooth
	       }    
	}
	
	

	
	@Override
	protected void onDestroy() {
		if(bluetoothAdapter.isDiscovering())
		{
			bluetoothAdapter.cancelDiscovery();
		}
		manageBluetoothConnexion.closeConnection();
		super.onDestroy();
		
	}
}
