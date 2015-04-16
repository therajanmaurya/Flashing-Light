package com.rm.flashinglight;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.media.AudioManager;
import android.os.Build;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;


public class CallHelper {


    /**
     * Listener to detect incoming calls.
     */


    private class CallStateListener extends PhoneStateListener {

        Camera mCamera;
        private Parameters p;
        String flashMode;
        SharedPreferences sp = ctx.getSharedPreferences("MainActivity", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        MainActivity mainactivity;
        boolean voicevalue;
        SurfaceTexture mPreviewTexture;

        @SuppressLint("NewApi")
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {

            mainactivity = new MainActivity();
            voicevalue = sp.getBoolean("check", false);

            /*
            *
            * checking camera is open or not.
            * if not, so open it and initialize the camera
            *
            * */


            if (mCamera == null) {
                try {
                    mPreviewTexture = new SurfaceTexture(0);
                    mCamera = Camera.open();
                    p = mCamera.getParameters();

                    /*
                    *
                    * if Build version is greater than Kitkat(19) then set Preview Texture of new Camera2 API
                    * for making compatible Lolipop
                    *
                    * */
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {

                        mCamera.setPreviewTexture(mPreviewTexture);

                    }


                    flashMode = p.getFlashMode();
                    editor.putString("flash", flashMode);
                    editor.commit();
                } catch (RuntimeException e) {
                    Log.e("Camera Error. ", e.getMessage());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            /*
            *
            * switch statement that handle the call ringing ,call disconnect ,call end function
            *
            * */

            switch (state) {


                /*
                *
                * when call ringing
                *
                * */
                case TelephonyManager.CALL_STATE_RINGING:

                    /*
                    *
                    * getting flash mode from sharedprefrences
                    *
                    * */
                    flashMode = sp.getString("flash", flashMode);

                    /*
                    *
                    * if flash mode is null
                    *
                    * */
                    if (flashMode == null) {
                        Toast.makeText(ctx, "Flash is not present sorry",
                                Toast.LENGTH_LONG).show();


                    /*
                    *
                    * if flash torch mode in on
                    *
                    * */
                    } else if (flashMode.equals(Parameters.FLASH_MODE_TORCH)) {

                        Toast.makeText(ctx, "Flash is already on",
                                Toast.LENGTH_LONG).show();



                    /*
                    *
                    * if flash mode is off means flash is not running
                    *
                    * */
                    } else if (flashMode.equals(Parameters.FLASH_MODE_OFF)) {


                        p.setFlashMode(Parameters.FLASH_MODE_TORCH);
                        mCamera.setParameters(p);

                    /*
                    *
                    * if Build version is greater than Kitkat(19) then set Preview Texture of new Camera2 API
                    * for making compatible Lolipop
                    *
                    * */
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {

                            try {
                                mCamera.setPreviewTexture(mPreviewTexture);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }


                        mCamera.startPreview();
                        flashMode = p.getFlashMode();
                        editor.putString("flash", flashMode);
                        editor.commit();
                    }

                    /*
                    *
                    * set phone on vibrate mode  and  set the phone volume is full
                    *
                    * */
                    AudioManager am = (AudioManager) ctx.getSystemService(Context.AUDIO_SERVICE);
                    am.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                    am.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                            AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);

                    /*
                    *
                    * getting the caller name from getContactName function in MainActivity
                    *
                    * */
                    String name = mainactivity.getContactName(ctx, incomingNumber);

                    /*
                    *
                    * check contact name is coming null or not if null so
                    * put name = "unknown person"
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
                    * if caller name is not null so add name + " is calling"
                    * and put in sharedprefrences for text_to_speech class
                    *
                    * */
                    name = name + " is calling";
                    editor.putString("caller_name", name);
                    editor.commit();

                    /*
                    *
                    * check the voice call check box is checked or not.
                    * if checked so start the text_to_speech service
                    *
                    * */
                    if (voicevalue == true) {

                        mainactivity.start_text_to_speech(ctx, true);
                        Toast.makeText(ctx, name + "is calling", Toast.LENGTH_LONG).show();

                    }
                    /////////////////////
                    Toast.makeText(ctx, "Incoming: " + incomingNumber, Toast.LENGTH_LONG).show();
                    ////////////////////

                    break;









                /*
                *
                * this case will call when call is received or end without received
                *
                * */
                case TelephonyManager.CALL_STATE_OFFHOOK:

                    /*
                    *
                    *  check text_to_speech service is running or not
                    *  if running so stop it. when call will end or received
                    *
                    * */
                    if (voicevalue == true) {
                        mainactivity.start_text_to_speech(ctx, false);
                    }


                    /*
                    *
                    * checking flash mode is off or not if off ,
                    * so stop the torch preview
                    *
                    * */
                    flashMode = sp.getString("flash", flashMode);
                    if (flashMode.equals(Parameters.FLASH_MODE_OFF)) {

                        Toast.makeText(ctx, "Flash is already OFF oofhook",
                                Toast.LENGTH_LONG).show();
                        p.setFlashMode(Parameters.FLASH_MODE_OFF);
                        mCamera.setParameters(p);

                    /*
                    *
                    * if Build version is greater than Kitkat(19) then set Preview Texture of new Camera2 API
                    * for making compatible Lolipop
                    *
                    * */
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {

                            try {
                                mCamera.setPreviewTexture(mPreviewTexture);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }


                        mCamera.stopPreview();
                        flashMode = p.getFlashMode();
                        editor.putString("flash", flashMode);
                        editor.commit();

                    /*
                    *
                    * checking flash mode if flash mode torch is on,
                    * so stop the preview
                    *
                    * */
                    } else if (flashMode.equals(Parameters.FLASH_MODE_TORCH)) {

                        p.setFlashMode(Parameters.FLASH_MODE_OFF);
                        mCamera.setParameters(p);

                    /*
                    *
                    * if Build version is greater than Kitkat(19) then set Preview Texture of new Camera2 API
                    * for making compatible Lolipop
                    *
                    * */
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {

                            try {
                                mCamera.setPreviewTexture(mPreviewTexture);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }



                        mCamera.stopPreview();
                        flashMode = p.getFlashMode();
                        editor.putString("flash", flashMode);
                        editor.commit();

                    }

                    break;


                /*
                *
                * this case call many times
                * 1. when call user disconnect.
                * 2. when phone in idle state. this function runs in several times to check
                *
                * */
                case TelephonyManager.CALL_STATE_IDLE:

                    /*
                    *
                    *  check text_to_speech service is running or not
                    *
                    * */
                    if (voicevalue == true) {
                        mainactivity.start_text_to_speech(ctx, false);
                    }


                    /*
                    *
                    * checking the flash mode is off condition or not
                    * if yes ,so do nothing
                    *
                    *
                    * */
                    flashMode = sp.getString("flash", flashMode);
                    if (flashMode.equals(Parameters.FLASH_MODE_OFF)) {

                        Toast.makeText(ctx, "Flash is already OFF idle",
                                Toast.LENGTH_LONG).show();

                    /*
                    *
                    * checking the flash mode torch is on or not .
                    * is yes , so stop the flash .
                    *
                    * */
                    } else if (flashMode.equals(Parameters.FLASH_MODE_TORCH)) {

                        p.setFlashMode(Parameters.FLASH_MODE_OFF);
                        mCamera.setParameters(p);


                    /*
                    *
                    * if Build version is greater than Kitkat(19) then set Preview Texture of new Camera2 API
                    * for making compatible Lolipop
                    *
                    * */
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {

                            try {
                                mCamera.setPreviewTexture(mPreviewTexture);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }


                        mCamera.stopPreview();
                        flashMode = p.getFlashMode();
                        editor.putString("flash", flashMode);
                        editor.commit();
                        // mCamera.release();

                    }

                    break;

            }
        }




    }



    private Context ctx;
    private TelephonyManager tm;
    private CallStateListener callStateListener;



    /*
    *
    * initializing the callstatelistener call
    *
    * */
    public CallHelper(Context ctx) {
        this.ctx = ctx;

        callStateListener = new CallStateListener();


    }

    /**
     * Start calls detection.
     */
    public void start() {
        tm = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
        tm.listen(callStateListener, PhoneStateListener.LISTEN_CALL_STATE);

    }




}
