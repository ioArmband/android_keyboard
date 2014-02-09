package org.tse.pri.ioarmband.client.android.connect;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.tse.pri.ioarmband.io.connection.IConnectionListener;
import org.tse.pri.ioarmband.io.connection.StreamedConnection;

import android.bluetooth.BluetoothSocket;

public class BluetoothConnectionManager {

	private static BluetoothConnectionManager instance = null;

	private StreamedConnection streamConnection;

	private IConnectionListener listener;

	private BluetoothSocket bluetoothSocket;
	
	
	private BluetoothConnectionManager()
	{
		super();
		streamConnection = null;
		
	}
	
	
	
	public static synchronized BluetoothConnectionManager getInstance() {
		if(instance == null)
		{
			instance = new BluetoothConnectionManager();
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
	public void addBeginConnectionListener(IBeginConnectionListener beginlistener)
	{
		this.beginlistener.add(beginlistener);		
		
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
	

	
	  

}
