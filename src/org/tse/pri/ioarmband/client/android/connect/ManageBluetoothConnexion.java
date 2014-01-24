package org.tse.pri.ioarmband.client.android.connect;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;

import org.tse.pri.ioarmband.io.connection.IConnectionListener;
import org.tse.pri.ioarmband.io.connection.StreamedConnection;

import android.bluetooth.BluetoothSocket;

public class ManageBluetoothConnexion {

	public static UUID CLIENT_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

	private static ManageBluetoothConnexion instance = null;

	private StreamedConnection streamConnection;

	private IConnectionListener listener;

	private BluetoothSocket bluetoothSocket;
	
	
	private ManageBluetoothConnexion()
	{
		super();
		streamConnection = null;
	}
	
	
	
	public static synchronized ManageBluetoothConnexion getInstance() {
		if(instance == null)
		{
			instance = new ManageBluetoothConnexion();
		}
		return instance;
	}
	
	public StreamedConnection getStreamConnection() {
		return streamConnection;
	}


	public void setStreamConnection(StreamedConnection streamConnection) {
		this.streamConnection = streamConnection;
	}
	
	public void newConnection(BluetoothSocket bluetoothSocket)
	{
		this.bluetoothSocket = bluetoothSocket;
		
		  try {
			streamConnection = new StreamedConnection(bluetoothSocket.getInputStream(), bluetoothSocket.getOutputStream());
			streamConnection.addConnectionListener(listener);
			dispatchBeginConnection();
			
			
		  } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	Set<IBeginConnectionListener> beginlistener = new HashSet<IBeginConnectionListener>();
	private void dispatchBeginConnection(){
		for (IBeginConnectionListener listener : beginlistener) {
			listener.onConnectionBegin();
		}
	}
	
	public void closeConnection()
	{
		if (streamConnection != null) {
			streamConnection.close();
			streamConnection = null;

			try {
				if (bluetoothSocket != null) {
					bluetoothSocket.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

			bluetoothSocket = null;
		}
	}
	  
	public void addConnectionListener(IConnectionListener listener)
	{
		this.listener = listener;		
		
	}
	

		public void addBeginConnectionListener(IBeginConnectionListener beginlistener)
		{
			this.beginlistener.add(beginlistener);		
			
		} 
	  

}
