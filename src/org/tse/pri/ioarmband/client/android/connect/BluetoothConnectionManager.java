package org.tse.pri.ioarmband.client.android.connect;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.tse.pri.ioarmband.io.connection.IConnectionListener;
import org.tse.pri.ioarmband.io.connection.StreamedConnection;
import org.tse.pri.ioarmband.io.message.Command;
import org.tse.pri.ioarmband.io.message.Message;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

public class BluetoothConnectionManager {

	private static BluetoothConnectionManager instance = null;
	private StreamedConnection streamConnection;

	private BluetoothSocket bluetoothSocket;
	private List<ServiceConnection> serviceConnections;
	
	private BluetoothConnectionManager()
	{
		super();
		streamConnection = null;
		serviceConnections = new ArrayList<ServiceConnection>();
	}
	
	public boolean isServiceConnectionExist(ServiceConnection serviceConnection)
	{
		for (ServiceConnection serviceConnectionTemp : serviceConnections) {
			if(serviceConnection.equals(serviceConnectionTemp))
			{
				return true;
			}
		}
		return false;
	}
	
	public void useConnection(ServiceConnection serviceConnection)
	{	
		
		if(!serviceConnection.equals(getCurrentServiceConnection()))
		{
			if(isServiceConnectionExist(serviceConnection))
			{
				serviceConnections.remove(serviceConnection);
			}
				dispatchLoseConnector();
				serviceConnections.add(serviceConnection);

			if(serviceConnections.size() == 1)
			{
				BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
				if (bluetoothAdapter.isEnabled()) {
					BluetoothDiscoveryManager.startdiscoveryDevice();
				}
			}
		}
	}
	
	private ServiceConnection getCurrentServiceConnection()
	{
		if (serviceConnections.size() > 0)
			return serviceConnections.get(serviceConnections.size() - 1);
		else
			return null;
		
	}
	public void removeUseConnection(Object obj)
	{
		serviceConnections.remove(obj);
		if(serviceConnections.size() == 0)
		{
			closeConnection();
		}
		dispatchWinConnector();
	}
	
	
	public static synchronized BluetoothConnectionManager getInstance() {
		if(instance == null)
		{
			instance = new BluetoothConnectionManager();
		}
		return instance;
	}
	

	public boolean isConnected() {
		return streamConnection != null;
	}
	
	public boolean isControlConnected(ServiceConnection serviceConnection) {
		return serviceConnection.equals(getCurrentServiceConnection());
}

	
	public void newConnection(BluetoothSocket bluetoothSocket)
	{
	
		this.bluetoothSocket = bluetoothSocket;
		
		  try {
			streamConnection = new StreamedConnection(bluetoothSocket.getInputStream(), bluetoothSocket.getOutputStream());
			streamConnection.addConnectionListener(connectionBluetooth);
			dispatchBeginConnection();
			
			
		  } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void closeConnection()
	{
		Log.d("BluetoothConnectionManager", "BluetoothConnectionManager close");
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
	
	public void sendMessage(Message message)
	{
		if(streamConnection != null)
		{
			streamConnection.sendCommand(new Command(message));
		}
	}
	
	
	private IConnectionListener connectionBluetooth = new IConnectionListener() {

		@Override
		public void onConnectionClose() {
			Log.d("ClavierService","onConnectionClose");	
			for (ServiceConnection serviceConnection : serviceConnections) {
				serviceConnection.onConnectionClose();
			}
		}

		@Override
		public void onCommandReiceved(Command command) {
			if(serviceConnections.size()>0)
			{
				ServiceConnection currentserviceConnection = getCurrentServiceConnection();
				currentserviceConnection.onCommandReiceved(command);
			}
		}
	};
	
	private void dispatchBeginConnection(){
		for (ServiceConnection serviceConnection : serviceConnections) {
			serviceConnection.onConnectionBegin();
		}
	}
	private void dispatchWinConnector(){
		if(serviceConnections.size()>0)
		{
			ServiceConnection currentserviceConnection = getCurrentServiceConnection();
			currentserviceConnection.onWinControl();
		}
	}
	private void dispatchLoseConnector(){
		for (ServiceConnection serviceConnection : serviceConnections) {
			serviceConnection.onLoseControl();
		}
	}
}
