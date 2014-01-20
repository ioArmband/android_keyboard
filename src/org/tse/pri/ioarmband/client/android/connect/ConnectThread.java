package org.tse.pri.ioarmband.client.android.connect;

import java.io.IOException;

import org.tse.pri.ioarmband.io.connection.StreamedConnection;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

public class ConnectThread extends Thread {
	   private final BluetoothSocket mmSocket;
	   private final BluetoothDevice mmDevice;
	   private BluetoothAdapter mBluetoothAdapter;
	 
	   
	   
	   public ConnectThread(BluetoothDevice device) {
	       BluetoothSocket tmp = null;
	       mmDevice = device;
	       mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	       
	       try {
	           tmp = device.createRfcommSocketToServiceRecord(BluetoothUtil.CLIENT_UUID);
	       } catch (IOException e) { }
	       mmSocket = tmp;
	   }
	   @Override
	   public void run() {
	       mBluetoothAdapter.cancelDiscovery();
	       try {
	    	   
	           mmSocket.connect();
	           Log.d("ConnectThread","connect to device "+mmDevice.getName());
	           
	           StreamedConnection streamConnection;
	           
	       } catch (IOException connectException) {
	           try {
	               mmSocket.close();
	               Log.d("ConnectThread","close device "+mmDevice.getName());
	           } catch (IOException closeException) { }
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