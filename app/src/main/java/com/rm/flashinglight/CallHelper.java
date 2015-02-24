package com.rm.flashinglight;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;


public class CallHelper {


    /**
     * Listener to detect incoming calls.
     */


    private class CallStateListener extends PhoneStateListener {

        Camera mCamera;
        private Parameters p;
        String flashMode;
        SharedPreferences sp = ctx.getSharedPreferences("flash", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        MainActivity mainactivity;


        @SuppressLint("NewApi")
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {

            mainactivity = new MainActivity();

            if (mCamera == null) {
                try {
                    mCamera = Camera.open();
                    p = mCamera.getParameters();
                    flashMode = p.getFlashMode();
                    editor.putString("flash", flashMode);
                    editor.commit();
                } catch (RuntimeException e) {
                    Log.e("Camera Error. ", e.getMessage());
                }
            }
            // mCamera = Camera.open();
            // Parameters p = mCamera.getParameters();

            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:

                    flashMode = sp.getString("flash", flashMode);
                    if (flashMode == null) {
                        Toast.makeText(ctx, "Flash is not present sorry",
                                Toast.LENGTH_LONG).show();

                    } else if (flashMode.equals(Parameters.FLASH_MODE_TORCH)) {

                        Toast.makeText(ctx, "Flash is already on",
                                Toast.LENGTH_LONG).show();


                    } else if (flashMode.equals(Parameters.FLASH_MODE_OFF)) {


                        p.setFlashMode(Parameters.FLASH_MODE_TORCH);
                        mCamera.setParameters(p);
                        mCamera.startPreview();
                        flashMode = p.getFlashMode();
                        editor.putString("flash", flashMode);
                        editor.commit();
                    }

                    // get contact name
                    String name = mainactivity.getContactName(ctx,incomingNumber);

                    // check text_to_speech is running or not
                    // if not , then start the service
                    Toast.makeText(ctx,name,Toast.LENGTH_LONG).show();
                    Log.e("name",name);
                    if (name.length() == 0 || name.isEmpty()) {
                        name = "unknown person is calling";
                        editor.putString("caller_name", name);
                        editor.commit();
                        mainactivity.start_voicecall(ctx,true);
                    } else {
                        name = name + " is calling";
                        editor.putString("caller_name", name);
                        editor.commit();
                        mainactivity.start_voicecall(ctx,true);
                    }


                    Toast.makeText(ctx, "Incoming: " + incomingNumber,
                            Toast.LENGTH_LONG).show();
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:

                    // check text_to_speech service is running or not
//                    if (mainactivity.isMyServiceRunning(Text_to_speech.class)) {
                        mainactivity.start_voicecall(ctx,false);
//                    }

                    flashMode = sp.getString("flash", flashMode);
                    if (flashMode.equals(Parameters.FLASH_MODE_OFF)) {

                        Toast.makeText(ctx, "Flash is already OFF oofhook",
                                Toast.LENGTH_LONG).show();
                        p.setFlashMode(Parameters.FLASH_MODE_OFF);
                        mCamera.setParameters(p);
                        mCamera.stopPreview();
                        flashMode = p.getFlashMode();
                        editor.putString("flash", flashMode);
                        editor.commit();

                    } else if (flashMode.equals(Parameters.FLASH_MODE_TORCH)) {

                        p.setFlashMode(Parameters.FLASH_MODE_OFF);
                        mCamera.setParameters(p);
                        mCamera.stopPreview();
                        flashMode = p.getFlashMode();
                        editor.putString("flash", flashMode);
                        editor.commit();

                    }

                    break;

                case TelephonyManager.CALL_STATE_IDLE:

                    // check text_to_speech service is running or not
//                    if (mainactivity.isMyServiceRunning(Text_to_speech.class)) {
                       // mainactivity.start_voicecall(false);
//                    }

                    flashMode = sp.getString("flash", flashMode);
                    if (flashMode.equals(Parameters.FLASH_MODE_OFF)) {

                        Toast.makeText(ctx, "Flash is already OFF idle",
                                Toast.LENGTH_LONG).show();
                        //p.setFlashMode(Parameters.FLASH_MODE_OFF);
//					mCamera.setParameters(p);
//					mCamera.stopPreview();
//					flashMode = p.getFlashMode();
//					editor.putString("flash", flashMode);
//					editor.commit();

                    } else if (flashMode.equals(Parameters.FLASH_MODE_TORCH)) {

                        p.setFlashMode(Parameters.FLASH_MODE_OFF);
                        mCamera.setParameters(p);
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

    /**
     * Broadcast receiver to detect the outgoing calls.
     */
    public class OutgoingReceiver extends BroadcastReceiver {
        public OutgoingReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            String number = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);

            Toast.makeText(ctx, "Outgoing: " + number, Toast.LENGTH_LONG)
                    .show();
        }

    }

    private Context ctx;
    private TelephonyManager tm;
    private CallStateListener callStateListener;

    private OutgoingReceiver outgoingReceiver;

    public CallHelper(Context ctx) {
        this.ctx = ctx;

        callStateListener = new CallStateListener();
        outgoingReceiver = new OutgoingReceiver();

    }

    /**
     * Start calls detection.
     */
    public void start() {
        tm = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
        tm.listen(callStateListener, PhoneStateListener.LISTEN_CALL_STATE);

        IntentFilter intentFilter = new IntentFilter(
                Intent.ACTION_NEW_OUTGOING_CALL);
        ctx.registerReceiver(outgoingReceiver, intentFilter);
    }

    /**
     * Stop calls detection.
     */
    public void stop() {
        tm.listen(callStateListener, PhoneStateListener.LISTEN_NONE);
        ctx.unregisterReceiver(outgoingReceiver);
    }


}
