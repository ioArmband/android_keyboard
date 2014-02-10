package org.tse.pri.ioarmband.client.android;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tse.pri.ioarmband.client.android.connect.BluetoothAndroidConnectionManager;
import org.tse.pri.ioarmband.client.android.keyboard.R;
import org.tse.pri.ioarmband.client.android.sms.SmsService;
import org.tse.pri.ioarmband.io.connection.manager.ServiceConnection;
import org.tse.pri.ioarmband.io.message.Command;
import org.tse.pri.ioarmband.io.message.GestureMessage;
import org.tse.pri.ioarmband.io.message.enums.GestureType;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity  implements ServiceConnection {

	Button btConnect;
	Button btDisconnect;
	Button btSend;
	Button btSmsService;
	TextView tvEtat;
	MainActivity activity;
	
	private static Logger logger = LoggerFactory.getLogger(MainActivity.class);
	
	private BluetoothAndroidConnectionManager manageBluetoothConnexion;

	private final static int REQUEST_CODE_ENABLE_BLUETOOTH = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		activity = this;
		btConnect = (Button) findViewById(R.id.bt_connect);
		btConnect.setOnClickListener(clickBtConnect);
		btDisconnect = (Button) findViewById(R.id.bt_disconnect);
		btDisconnect.setOnClickListener(clickBtDisconnect);
		btSend = (Button) findViewById(R.id.bt_send);
		btSend.setOnClickListener(clickBtSend);
		tvEtat = (TextView) findViewById(R.id.tv_etat_bluetooth);
		btSmsService =(Button) findViewById(R.id.bt_launchService);
		btSmsService.setOnClickListener(clickBtLaunchService);
		
		manageBluetoothConnexion = BluetoothAndroidConnectionManager.getInstance();
		logger.info("My Application Created");

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	 public boolean isServiceRunning(ActivityManager am,String nomService){          
	        List<RunningServiceInfo> services = am.getRunningServices(100);        
	        for(ActivityManager.RunningServiceInfo rsi:services){            
	            if(rsi.process.equals(nomService)){                
	                return true;
	            }
	        }
	        return false;
	    }
	 
	private OnClickListener clickBtLaunchService = new OnClickListener() {
		public void onClick(View v) {
			ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
			if(!isServiceRunning(activityManager,"SmsService"))
			{
				Intent serviceIntent = new Intent();
				serviceIntent.setClass(activity,SmsService.class);
				startService(serviceIntent);
			}
		}
	};
	
		
	private OnClickListener clickBtConnect = new OnClickListener() {
		public void onClick(View v) {
			//tvEtat.setText("Button Connect click"); 
			manageBluetoothConnexion.useConnection(activity);
		}
	};

	private OnClickListener clickBtDisconnect = new OnClickListener() {
		public void onClick(View v) {
 
			manageBluetoothConnexion.removeUseConnection(activity);
			
			Log.d("MainActivity","manageBluetoothConnexion.closeConnection() ");
		
		}
	};

	private OnClickListener clickBtSend = new OnClickListener() {
		public void onClick(View v) {
			
			GestureMessage msg = new GestureMessage();
			msg.setType(GestureType.TOUCH); 
			msg.setSourceName("send via keyboard android");
			BluetoothAndroidConnectionManager.getInstance().sendMessage(msg);
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
		manageBluetoothConnexion.removeUseConnection(activity);
		super.onDestroy();
		
	}

	@Override
	public void onCommandReiceved(Command command) {
		Log.d("MainActivity","onCommandReiceved");
		
		}

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
	public void onWinControl() {
		runOnUiThread(new Runnable() {
		     @Override
		     public void run() {
		    	 tvEtat.setText("Win Control");
		    }
		});
		
	}

	@Override
	public void onLoseControl() {
		runOnUiThread(new Runnable() {
		     @Override
		     public void run() {
		    	 tvEtat.setText("Lose Control");
		    }
		});
	}

	@Override
	public void onConnectionStarted() {
		runOnUiThread(new Runnable() {
		     @Override
		     public void run() {
		    	 tvEtat.setText("Connection Begin");
		    }
		});
		
	}
}
