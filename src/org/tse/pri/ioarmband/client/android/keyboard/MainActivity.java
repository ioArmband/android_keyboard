package org.tse.pri.ioarmband.client.android.keyboard;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.tse.pri.ioarmband.client.android.connect.BluetoothConnectionManager;
import org.tse.pri.ioarmband.client.android.connect.BluetoothDiscoveryManager;
import org.tse.pri.ioarmband.client.android.connect.IBeginConnectionListener;
import org.tse.pri.ioarmband.io.connection.IConnectionListener;
import org.tse.pri.ioarmband.io.connection.StreamedConnection;
import org.tse.pri.ioarmband.io.message.Command;
import org.tse.pri.ioarmband.io.message.GestureMessage;
import org.tse.pri.ioarmband.io.message.enums.GestureType;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
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
	
	
	private static Logger logger = LoggerFactory.getLogger(MainActivity.class);
	
	private BluetoothConnectionManager manageBluetoothConnexion;

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
		
		manageBluetoothConnexion = BluetoothConnectionManager.getInstance();
		
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
			runOnUiThread(new Runnable() {
			     @Override
			     public void run() {
			    	 tvEtat.setText("Connexion close");
			    }
			});	
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
			
			BluetoothDiscoveryManager.startdiscoveryDevice();

		}
	};

	private OnClickListener clickBtDisconnect = new OnClickListener() {
		public void onClick(View v) {
 
			manageBluetoothConnexion.closeConnection();
			
			Log.d("MainActivity","manageBluetoothConnexion.closeConnection() ");
		
		}
	};

	private OnClickListener clickBtSend = new OnClickListener() {
		public void onClick(View v) {
			
			GestureMessage msg = new GestureMessage();
			msg.setType(GestureType.TOUCH); 
			msg.setSourceName("send via keyboard android");
			StreamedConnection streamedConnection = manageBluetoothConnexion.getStreamConnection();
			
			if(streamedConnection != null)
			{
				streamedConnection.sendCommand(new Command(msg));
			}
		}
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	       super.onActivityResult(requestCode, resultCode, data);
	       if (requestCode != REQUEST_CODE_ENABLE_BLUETOOTH)
	           return;
	       if (resultCode == RESULT_OK) {
	    	 tvEtat.setText("Bluetooth activé");// L'utilisation a activé le bluetooth
	       } else {
	          tvEtat.setText("Bluetooth desactivé");// L'utilisation n'a pas activé le bluetooth
	       }    
	}

	
	@Override
	protected void onDestroy() {
		manageBluetoothConnexion.closeConnection();
		super.onDestroy();
		
	}
}
