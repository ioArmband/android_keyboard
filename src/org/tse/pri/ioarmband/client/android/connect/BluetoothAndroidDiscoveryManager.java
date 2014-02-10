package org.tse.pri.ioarmband.client.android.connect;

import java.util.Set;

import org.tse.pri.ioarmband.io.connection.manager.DiscoveryManager;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.ParcelUuid;
import android.util.Log;

public class BluetoothAndroidDiscoveryManager extends DiscoveryManager{
	
	public void startdiscoveryDevice()
	{ 
		if(!BluetoothAndroidConnectionManager.getInstance().isConnected())
		{
			BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
			if (bluetoothAdapter == null)
			{
				//TODO:tvEtat.setText("pas de bluetooth");
			}
	
			Set<BluetoothDevice> bluetoothDevices =  bluetoothAdapter.getBondedDevices();
			
			
			
			for (BluetoothDevice bluetoothDevice : bluetoothDevices) {
				
				Log.d("MainActivity","Device : "+bluetoothDevice.getName());
				Log.d("MainActivity","Etat : "+bluetoothDevice.getBondState());
				
				ParcelUuid[] uuids = bluetoothDevice.getUuids();
				
				for (int i = 0; i < uuids.length; i++) {
					Log.d("MainActivity",uuids[i].toString());
					//TODO: gestion multi conexion
					if(uuids[i].toString().equals(BluetoothAndroidDiscoveryManager.CLIENT_UUID.toString())){
						AndroidConnectThread connect = new AndroidConnectThread(bluetoothDevice);
						connect.start();
						return;
					}
				}
				Log.d("MainActivity"," ");
			}
		}
	}
	
	
}
