package com.rm.flashinglight;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.Toast;

import java.util.Locale;

/**
 * Created by rajanmaurya on 23/2/15.
 */
public class Text_to_speech extends Service implements TextToSpeech.OnInitListener {

    private String str;
    private TextToSpeech mTts;
    private static final String TAG = "TTSService";
    Context context;





    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Toast.makeText(this,"onstart",Toast.LENGTH_SHORT).show();
        sayHello(str);

        Log.v(TAG, "onstart_service");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {

        SharedPreferences sp = getSharedPreferences("flash",MODE_PRIVATE);
//        SharedPreferences.Editor editor = sp.edit();
        mTts = new TextToSpeech(this,
                this  // OnInitListener
        );

        mTts.setSpeechRate(0.75f);
        Log.v(TAG, "oncreate_service");
        str =  sp.getString("caller_name", "");
        //str = "rajan is calling you sir please take the call";
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

    @Override
    public void onInit(int status) {

        Toast.makeText(this,"init", Toast.LENGTH_SHORT).show();
        Log.v(TAG, "oninit");
        if (status == TextToSpeech.SUCCESS) {
            int result = mTts.setLanguage(Locale.US);
            if (result == TextToSpeech.LANG_MISSING_DATA ||
                    result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.v(TAG, "Language is not available.");
            } else {

                sayHello(str);

            }
        } else {
            Log.v(TAG, "Could not initialize TextToSpeech.");
        }

    }

    private void sayHello(String str) {
        mTts.speak(str,
                TextToSpeech.QUEUE_FLUSH,
                null);
    }


}

