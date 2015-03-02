package com.rm.flashinglight;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.widget.Toast;

/**
 * Created by rajanmaurya on 3/3/15.
 */
public class Voice_helper {


    /*
   *
   * class that Handles all call events
   *
   * */
    private class Incommigcall extends PhoneStateListener {

        SharedPreferences sp = ctx.getSharedPreferences("MainActivity", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        boolean voicevalue;
        MainActivity mainactivity;

        @SuppressLint("NewApi")
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {


            mainactivity = new MainActivity();
            voicevalue = sp.getBoolean("check", false);

            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:


                    /*
                    *
                    * set phone on vibrate mode and set volume full
                    *
                    * */
                    AudioManager am = (AudioManager) ctx.getSystemService(Context.AUDIO_SERVICE);
                    am.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                    am.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                            AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);


                    /*
                    *
                    * getting the caller name from calling number from MainActivity class
                    *
                    * */
                    String name = mainactivity.getContactName(ctx, incomingNumber);


                    /*
                    *
                    * checking the caller name is saved or not
                    * if not then set name = "unknown person is calling"
                    *
                    * */
                    if (name == null) {
                        name = "unknown person";
                    }

                    /////////////////////
                    Toast.makeText(ctx, name + voicevalue, Toast.LENGTH_LONG).show();
                    ////////////////////

                    /*
                    *
                    * setting the value of caller name and
                    * put the value of caller name in sharedprefreces
                    *
                    * */
                    name = name + " is calling";
                    editor.putString("caller_name", name);
                    editor.commit();

                    /*
                    *
                    * checking the voice calling check box is checked or not
                    *
                    * */
                    if (voicevalue == true) {


                        /*
                        *
                        * starting the text to speech service
                        *
                        * */
                        mainactivity.start_text_to_speech(ctx, true);
                        Toast.makeText(ctx, name + "is calling", Toast.LENGTH_LONG).show();

                    }


                    break;


                case TelephonyManager.CALL_STATE_OFFHOOK:

                    /*
                    *
                    *stopping the text to speech service on call end or cancel or received
                    *
                    * */
                    if (voicevalue == true) {
                        mainactivity.start_text_to_speech(ctx, false);
                    }

                    break;


                case TelephonyManager.CALL_STATE_IDLE:

                    /*
                    *
                    *stopping the text to speech service when phone in idle state
                    *
                    * */
                    if (voicevalue == true) {
                        mainactivity.start_text_to_speech(ctx, false);
                    }

                    break;

            }

        }
    }

    private Context ctx;
    private TelephonyManager tm;
    private Incommigcall income;

    public Voice_helper(Context context) {
        this.ctx = context;
        income = new Incommigcall();

    }

    /*
    *
    * start function initialize the class Incommingcall that handle the all call events
    *
    * */
    public void start() {
        tm = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
        tm.listen(income, PhoneStateListener.LISTEN_CALL_STATE);

    }


}
