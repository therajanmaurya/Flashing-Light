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
public class voice_call_Service extends Service {

    private Voice_helper voice_help;

    public voice_call_Service(){

    }

    /*
   *
   * onStartCommand is the service function that act as a main function
   * so when class called it runs first
   *
   * */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        voice_help = new Voice_helper(this);
        voice_help.start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
