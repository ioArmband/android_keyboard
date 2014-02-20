package org.ioarmband.client.android;

import java.util.List;

import org.ioarmband.android.connection.aidl.IRemoteCommunicationService;
import org.ioarmband.android.connection.aidl.RemoteCommunicationServiceListener;
import org.ioarmband.android.connection.message.impl.android.MessageContainer;
import org.ioarmband.android.connection.message.impl.android.TextMessageAppMessageAndroid;
import org.ioarmband.client.android.keyboard.R;
import org.ioarmband.client.android.sms.SmsService;
import org.ioarmband.net.message.enums.GestureType;
import org.ioarmband.net.message.impl.GestureMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

public class MainActivity extends Activity{

	Button btConnect;
	Button btDisconnect;
	Button btSend;
	Switch switchLaunchService;
	TextView tvEtat;
	MainActivity activity;
	
	Intent serviceIntent ;
	
	private static Logger logger = LoggerFactory.getLogger(MainActivity.class);
	

	private final static int REQUEST_CODE_ENABLE_BLUETOOTH = 0;
	
	protected IRemoteCommunicationService sCommunicationService;
	
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
		
		
		switchLaunchService = (Switch) findViewById(R.id.switch_launchService);
		switchLaunchService.setOnClickListener(clickBtLaunchService);
		
	
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
			
			 
			if(switchLaunchService.isChecked())
			{
					serviceIntent = new Intent();
					serviceIntent.setClass(activity, SmsService.class);
					startService(serviceIntent);
			}
			else
			{
					stopService(serviceIntent);
			}
		}
	};
	
		
	private OnClickListener clickBtConnect = new OnClickListener() {
		public void onClick(View v) {
			//tvEtat.setText("Button Connect click"); 
			initConnection();
		}
	};

	private OnClickListener clickBtDisconnect = new OnClickListener() {
		public void onClick(View v) {
			Log.d("MainActivity", "manageBluetoothConnexion.closeConnection() ");

			try {
				sCommunicationService.unUseConnection(serviceOutConnection);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	};

	private OnClickListener clickBtSend = new OnClickListener() {
		public void onClick(View v) {
			
			GestureMessage msg = new GestureMessage();
			msg.setType(GestureType.TOUCH); 
			msg.setSourceName("send via keyboard android");
		
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
		try {
			sCommunicationService.unUseConnection(serviceOutConnection);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		super.onDestroy();
		
	}

	
	public void initConnection()
	{
		Log.d("MainActivity", "initConnection");
		
		 
		if(serviceConnection != null)
		{ 
			Log.d("MainActivity", "initConnection"+ IRemoteCommunicationService.class.getName());
		 Intent it = new Intent();
          it.setAction("com.remote.service.CALCULATOR");
          //binding to remote service
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
			Log.d("MainActivity", "serviceOutConnection onConnectionStarted");
			runOnUiThread(new Runnable() {
			     @Override
			     public void run() {
			    	 tvEtat.setText("Connection Begin");
			    }
			});
			
		}

		@Override
		public void onWinControl() throws RemoteException {
			Log.d("MainActivity", "serviceOutConnection onWinControl");
			runOnUiThread(new Runnable() {
			     @Override
			     public void run() {
			    	 tvEtat.setText("Connection WinControl");
			    }
			});
			
		}

		@Override
		public void onLoseControl() throws RemoteException {
			Log.d("MainActivity", "serviceOutConnection onLoseControl");
			runOnUiThread(new Runnable() {
			     @Override
			     public void run() {
			    	 tvEtat.setText("Connection LoseControl");
			    }
			});
			
		}

		@Override
		public void onConnectionClose() throws RemoteException {
			Log.d("MainActivity", "serviceOutConnection onConnectionClose");
			runOnUiThread(new Runnable() {
			     @Override
			     public void run() {
			    	 tvEtat.setText("Connection close");
			    }
			});
			
			
		}

		@Override
		public void onCommandReiceved(MessageContainer command)
				throws RemoteException {
			Log.d("MainActivity", "serviceOutConnection onCommandReiceved");
			
			if(TextMessageAppMessageAndroid.class.getName().equals(command.getClazz()))
			{
				TextMessageAppMessageAndroid message =(TextMessageAppMessageAndroid) command.getMessageAndroid();
				Log.d("MainActivity", "TextMessageAppMessageAndroid  name = "+message.getMessage());
			}
		}
		
	
	String clazz = "MainActivity";
		
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
		
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/*
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
		
	}*/
}
