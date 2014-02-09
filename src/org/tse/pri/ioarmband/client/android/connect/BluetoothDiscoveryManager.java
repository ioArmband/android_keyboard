package org.tse.pri.ioarmband.client.android.connect;

import java.util.Set;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.ParcelUuid;
import android.util.Log;

public class BluetoothDiscoveryManager {

	public static UUID CLIENT_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
	
	
	
	public static void startdiscoveryDevice()
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
				if(uuids[i].toString().equals(BluetoothDiscoveryManager.CLIENT_UUID.toString())){
					ConnectThread connect = new ConnectThread(bluetoothDevice);
					connect.start();
					return;
				}
			}
			Log.d("MainActivity"," ");
		}
	}
	
	
}
