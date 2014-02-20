package org.ioarmband.client.android.sms;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.ioarmband.android.connection.ImageEncoderAndroid;
import org.ioarmband.net.message.impl.TextMessageAppMessage;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;
import android.provider.MediaStore;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;

public class SmsReceiver extends BroadcastReceiver{

	final SmsManager sms = SmsManager.getDefault();
	
	private static List<TextMessageAppMessage> messages = new ArrayList<TextMessageAppMessage>();
	
	public synchronized int sizeMessage()
	{
		return messages.size();
	}
	
	public synchronized TextMessageAppMessage getAndRemoveFirstMessages() {
		if(messages.size()>0)
		{
			TextMessageAppMessage msg = messages.get(0);
			messages.remove(0);
			return msg;
		}
		return null;
	}
	
	public synchronized void addMessages(TextMessageAppMessage message) {
		this.messages.add(message);
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		// Retrieves a map of extended data from the intent.
		Log.d("SmsReceiver","onReceive");
		
		
		
		final Bundle bundle = intent.getExtras();
		 
		try {
		     
		    if (bundle != null) {
		         
		        final Object[] pdusObj = (Object[]) bundle.get("pdus");
		         
		        for (int i = 0; i < pdusObj.length; i++) {
		             
		            SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
		            String phoneNumber = currentMessage.getDisplayOriginatingAddress();
		            
		            Uri uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
		            
		          		
		            String nameContact = retrieveContactName(context,uri);
		           Bitmap photoContact = retrieveContactPhoto(context, uri);
		           String imageEncoded =  null;
		           if(photoContact == null)
		           {
		        	   Log.d("SmsReceiver", "photoContact == null");
		           }
		           else
		           {
		        	   Log.d("SmsReceiver", "photoContact != null");
		        	   imageEncoded= ImageEncoderAndroid.encodeTobase64(photoContact);
		           }
		           if(imageEncoded == null )
		           {
		        	   Log.d("SmsReceiver", "imageEncoded == null"); 
		           }
		           else
		           {
		        	   Log.d("SmsReceiver", "imageEncoded != null");
		           }
		           
		            
		            String senderNum = phoneNumber;
		            String message = currentMessage.getDisplayMessageBody();
		 
		            
		            
		            Log.i("SmsReceiver", "nameContact: "+ nameContact +"senderNum: "+ senderNum + "; message: " + message);
		            
		             if(nameContact == null)
		            	 nameContact = senderNum;
		             
		            TextMessageAppMessage msg = new TextMessageAppMessage("SMS",nameContact, message,imageEncoded);
		           
		            	 
		            addMessages(msg);
		           
		            
		             
		        } // end for loop
		        dispatchSmsReceived();
		      } // bundle is null
		 
		} catch (Exception e) {
		    Log.e("SmsReceiver", "Exception smsReceiver" +e);
		     
		}
		
	}
	
	 private  String retrieveContactName(Context context,Uri uri) {
		  Log.d("SmsReceiver", "retrieveContactName" );
		 Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
         String contactName = null;
         if (cursor.moveToFirst()) {
             contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));                
         }
         cursor.close();
         return contactName;
	 }
	
	private Bitmap retrieveContactPhoto(Context context,Uri uri) {
		  Log.d("SmsReceiver", "retrieveContactPhoto" );
	        Bitmap photo = null;
	 
	      
	        	 Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
	             String contactImage = null;
	             if (cursor.moveToFirst()) {
	        	   contactImage  = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_URI));      
	             }
	             if(contactImage != null)
		         {
		             cursor.close();

		            try {
		            	
		            	photo = MediaStore.Images.Media
						 .getBitmap(context.getContentResolver(),
						   Uri.parse(contactImage));
						
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	             }
	        return photo;
	    }
	
	
	Set<SmsRecevierListener> recevierListeners = new HashSet<SmsRecevierListener>();
	private void dispatchSmsReceived(){
		Log.e("SmsReceiver", "dispatchSmsReceived");
		for (SmsRecevierListener listener : recevierListeners) {
			listener.onSmsReceived();
		}
	}
	public void addSmsRecevierListener(SmsRecevierListener recevierListener)
	{
		this.recevierListeners.add(recevierListener);		
		
	} 
	


}
