package org.tse.pri.ioarmband.client.android.connect;

import java.io.IOException;

import org.tse.pri.ioarmband.io.connection.manager.ConnectThread;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

public class AndroidConnectThread extends ConnectThread {
	   private final BluetoothSocket mmSocket;
	   private final BluetoothDevice mmDevice;
	   private BluetoothAdapter mBluetoothAdapter;
	   
	   public AndroidConnectThread(BluetoothDevice device) {
	       BluetoothSocket tmp = null;
	       mmDevice = device;
	       mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	         
	       try {
	           tmp = device.createRfcommSocketToServiceRecord(BluetoothAndroidDiscoveryManager.CLIENT_UUID);
	       } catch (IOException e) {
               Log.d("ConnectThread",e.toString());
	       }
	       Log.d("ConnectThread","mmSocket "+tmp.toString());
	       mmSocket = tmp; 
	   }
	   @Override
	   public void run() {
		   Log.d("AndroidConnectThread","run Thread");
		   if(mBluetoothAdapter.isDiscovering())
			   mBluetoothAdapter.cancelDiscovery();
	       try {
	    	   
	           mmSocket.connect();
	           Log.d("ConnectThread","connect to device "+mmDevice.getName());
	           BluetoothAndroidConnectionManager.getInstance().newConnection(mmSocket);
	         
	           
	       } catch (IOException connectException) {
	           try {
	               mmSocket.close();
	               Log.d("ConnectThread","close device "+mmDevice.getName());
	               Log.d("ConnectThread",connectException.toString());
	           } catch (IOException closeException) {
	        	   Log.d("ConnectThread","close device "+mmDevice.getName());
	               Log.d("ConnectThread",closeException.toString());
	           }
	           return;
	       }
	        //manageConnectedSocket(mmSocket);
	   }
	   public void cancel() {
	       try {
	           mmSocket.close();
	           Log.d("ConnectThread","close device "+mmDevice.getName());
	       } catch (IOException e) { }
	   }

	}