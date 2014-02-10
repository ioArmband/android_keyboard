package org.tse.pri.ioarmband.client.android.connect;

import java.io.IOException;

import org.tse.pri.ioarmband.io.connection.StreamedConnection;
import org.tse.pri.ioarmband.io.connection.manager.ConnectionManager;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.util.Log;


public class BluetoothAndroidConnectionManager extends ConnectionManager{


	private static BluetoothAndroidConnectionManager instance = null;

	private BluetoothSocket bluetoothSocket;
	
	private BluetoothAndroidConnectionManager()
	{
		super();
		
	}

	public static synchronized BluetoothAndroidConnectionManager getInstance() {
		if(instance == null)
		{
			instance = new BluetoothAndroidConnectionManager();
		}
		return instance;
	}
	
	
	@Override
	public void LauchDiscovery() {

		BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBluetoothAdapter == null) {
		    // Device does not support Bluetooth
		}
		if (mBluetoothAdapter.isEnabled()) {
			BluetoothAndroidDiscoveryManager androidDiscoveryManager = new BluetoothAndroidDiscoveryManager();
			androidDiscoveryManager.startdiscoveryDevice();
		}
		
	}

	
	@Override
	public void closeConnectionComplementary() {

		Log.d("BluetoothConnectionManager", "BluetoothConnectionManager close");

		try {
			if (bluetoothSocket != null) {
				bluetoothSocket.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		bluetoothSocket = null;

	}

	@Override
	public void newConnectionComplementary(Object bluetoothSocket) {
		Log.d("BluetoothConnectionManager", "newConnectionComplementary");
		this.bluetoothSocket = (BluetoothSocket) bluetoothSocket;
		
		  try {
			streamConnection = new StreamedConnection(this.bluetoothSocket.getInputStream(), this.bluetoothSocket.getOutputStream());
			streamConnection.addConnectionListener(connection);
			
		  } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/*

	private static BluetoothConnectionManager instance = null;
	private StreamedConnection streamConnection;

	private BluetoothSocket bluetoothSocket;
	private List<ServiceConnection> serviceConnections;
	

	
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
	}*/
}
