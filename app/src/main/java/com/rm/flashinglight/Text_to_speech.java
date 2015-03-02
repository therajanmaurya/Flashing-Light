package com.rm.flashinglight;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.Locale;

/**
 * Created by rajanmaurya on 23/2/15.
 */


/*
*
* this class Handle the all text to speech events
*
*
* */
public class Text_to_speech extends Service implements TextToSpeech.OnInitListener {

    private String str;
    private TextToSpeech mTts;
    private static final String TAG = "TTSService";
    Context context;
    Handler mHandler = new Handler();


    /*
    *
    * onStartCommand is the service function that act as a main function
    * so when class called it runs first
    *
    * */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        /*
        *
        * this function convert the text to speech
        *
        * */
        sayHello(str);

        Log.v(TAG, "onstart_service");

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {

        /*
        *
        *making sharedprefrences object with MainActivity key
        *
        * */
        SharedPreferences sp = getSharedPreferences(getString(R.string.key_MainActivity), MODE_PRIVATE);

        /*
        *
        * initializing the TTF
        * And setting the speed of SpeechRate
        *
        * */
        mTts = new TextToSpeech(this, this );
        mTts.setSpeechRate(0.75f);
        Log.v(TAG, "oncreate_service");

        /*
        *
        * getting the value from sharedprefrences
        *
        * */
        str = sp.getString("caller_name", "");

        super.onCreate();
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        if (mTts != null) {
            mTts.stop();
            mTts.shutdown();
        }
        super.onDestroy();
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    /*
    *
    *
    * this is the initializer that initialize the TTF and set the language
    *
    *
    * */
    @Override
    public void onInit(int status) {


        if (status == TextToSpeech.SUCCESS) {
            int result = mTts.setLanguage(Locale.US);
            if (result == TextToSpeech.LANG_MISSING_DATA ||
                    result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.v(TAG, "Language is not available.");
            } else {

                    /*
                    *
                    * do nothing
                    *
                    * */

            }
        } else {
            Log.v(TAG, "Could not initialize TextToSpeech.");
        }

    }


    /*
    *
    * this is the main text to speech function that speak the "user name" + is calling
    * with a 5 second defence using thread
    *
    * */
    private void sayHello(String str) {

        final String str1 = str;

        Log.v(TAG, "voice");
        new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                while (true) {
                    try {


                        /*
                        *
                        * time to delay to run again same function
                        *
                        * */
                        Thread.sleep(5000);

                        mHandler.post(new Runnable() {

                            @Override
                            public void run() {
                                // TODO Auto-generated method stub
                                // Write your code here to update the UI.

                                /*
                                *
                                * this function is translator
                                *
                                * */
                                mTts.speak( str1, TextToSpeech.QUEUE_FLUSH , null);


                            }
                        });
                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                }
            }
        }).start();

    }
}




