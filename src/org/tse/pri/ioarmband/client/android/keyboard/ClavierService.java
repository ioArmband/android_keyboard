package org.tse.pri.ioarmband.client.android.keyboard;

import android.inputmethodservice.InputMethodService;
import android.view.View;


public class ClavierService extends InputMethodService {

	
	@Override
	public View onCreateInputView() {
		MyKeyboardView inputView =  (MyKeyboardView) getLayoutInflater().inflate( R.layout.clavier, null);
	    

		
		return inputView;
	}

	


}
