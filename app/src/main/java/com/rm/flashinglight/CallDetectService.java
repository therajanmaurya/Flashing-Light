package com.rm.flashinglight;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;


/**
 * Created by rajanmaurya on 3/3/15.
 */


/*
*
*
* class that runs in background to handle the call events
*
* */
public class CallDetectService extends Service {
	private CallHelper callHelper;
 
    public CallDetectService() {
    }


    /*
    *
    * onStartCommand is the service function that act as a main function
    * so when class called it runs first
    *
    * */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		callHelper = new CallHelper(this);
		
		int res = super.onStartCommand(intent, flags, startId);
		callHelper.start();
		 
		return res;
	}
 

	@Override
    public IBinder onBind(Intent intent) {
		// not supporting binding
    	return null;
    }
	
	
}
