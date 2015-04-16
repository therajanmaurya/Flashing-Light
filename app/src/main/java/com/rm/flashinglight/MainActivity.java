package com.rm.flashinglight;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;


public class MainActivity extends ActionBarActivity {


    private Toolbar toolbar;
    private RadioButton start, stop;
    private boolean detectEnabled, checkbox, CameraService;
    public static String keyvalue = null;
    private int MY_DATA_CHECK_CODE = 0;
    private TextToSpeech myTTS;
    public Text_to_speech t;
    CheckBox box;
    public TextView flashtext;
    private CardView flashcard;


    ImageButton flashbutton;
    private Camera camera;
    private boolean isFlashOn;
    Parameters params;
    private SurfaceTexture mPreviewTexture;
    MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


       /*
        *
        * Making the sharedprefreces with key MainActivity and mode private
        *
        * */
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.key_MainActivity), MODE_PRIVATE);

       /*
       * Checking Camera Flash Service is available or Not
       *
       * if Available , So put the value "True" in sharedprefreces
       *
       * if Not Available , So put the Value "False" in sharedprefreces
       *
       * */
        CheckCameraService();

        /*
        *
        * Initializing the xml component
        *
        * */
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        start = (RadioButton) findViewById(R.id.radio_start);
        stop = (RadioButton) findViewById(R.id.radio_stop);
        box = (CheckBox) findViewById(R.id.checkbox_cheese);
        flashbutton = (ImageButton) findViewById(R.id.btnSwitch);
        flashtext = (TextView)findViewById(R.id.flashtext);
        flashcard = (CardView)findViewById(R.id.flashbuttoncard);

        /*
         * Switch button click event to toggle flash on/off
		 */
        flashbutton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (CameraService) {

                    if (isFlashOn) {
                        // turn off flash
                        turnOffFlash();
                    } else {
                        // turn on flash
                        turnOnFlash();
                    }

                }
            }
        });

        /*
        *
        * Clicking event of Flashing Light
        *
        *
        * */
        flashcard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (CameraService) {

                    if (isFlashOn) {
                        // turn off flash
                        turnOffFlash();
                    } else {
                        // turn on flash
                        turnOnFlash();
                    }

                }
            }
        });



        // get the camera
        getCamera();

        // displaying button image
        toggleButtonImage();


        /*
        *
        * Getting value from sharedprefreces to enable the radio button and checkbox
        * And check camera service is available or not ;
        * To Enable or Disable the Service
        *
        * */

        keyvalue = sharedPref.getString(getString(R.string.key), "");
        checkbox = sharedPref.getBoolean(getString(R.string.key_check), false);
        CameraService = sharedPref.getBoolean(getString(R.string.key_camera_service), false);
        Toast.makeText(this, "" + checkbox, Toast.LENGTH_SHORT).show();




        /*
        *
        * Enable or Disable the Checkbox for voice calling service
        *
        * */

        if (checkbox == true) {
            box.setChecked(true);
        } else if (checkbox == false) {
            box.setChecked(false);
        }

        /*
        *
        * Enable or Disable the radio button according to sharedprefreces
        *
        *
        * */

        if (CameraService) {

            /*
            *
            * if Camera Service is available
            * */
            if (keyvalue.length() == 0 || keyvalue == null) {
                start.setChecked(true);
                           /*
                           * Starting the flash service
                           *
                           * */

                start_server(false);

            }

            if (keyvalue.contentEquals("start")) {
                Log.e("where", "in start");
                Toast.makeText(this, "starting", Toast.LENGTH_SHORT).show();
                start.setChecked(true);

                            /*
                            * Starting the flash service
                            *
                            * */
                start_server(false);

            }

            if (keyvalue.contentEquals("stop")) {
                Log.e("where", "in stop");
                Toast.makeText(this, "stopping", Toast.LENGTH_SHORT).show();
                stop.setChecked(true);

                            /*
                            * Stopping the flash Service
                            *
                            * */
                start_server(true);

            }


        } else {
            start.setEnabled(false);
            stop.setEnabled(false);

            /*
            *
            * AlertDialog If Camera Flash is not Available
            * */
            AlertDialog alert = new AlertDialog.Builder(MainActivity.this)
                    .create();
            alert.setTitle("Error");
            alert.setMessage("Sorry, your device doesn't support flash light!");
            alert.setButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // closing the application
                    // finish();
                }
            });
            alert.show();

            flashtext.setText("Sorry your device does not support Camera Flash Service ");
            Toast.makeText(this, "flash is not available", Toast.LENGTH_SHORT).show();
        }







    }



    /*
    *
    * setting the checkbox click event
    *
    * */

    public void onCheckboxClicked(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();

        Log.e("check", "check");
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.key_MainActivity), MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        String flashon;
        Boolean CameraflashAvailable;
        CameraflashAvailable = sharedPref.getBoolean(getString(R.string.key_camera_service), false);
        flashon = sharedPref.getString(getString(R.string.key), "");
        // Check which checkbox was clicked
        switch (view.getId()) {
            case R.id.checkbox_cheese:
                if (checked) {

                    editor.putBoolean(getString(R.string.key_check), true);
                    editor.commit();


                    /*
                    *
                    * check the camera service ,is available or not if yes
                    * So check the flash service is using or not , if using so did not start the voice service
                    * if not using start the voice service
                    *
                    * */
                    if (CameraflashAvailable) {

                        if (flashon.contentEquals("stop")) {
                            Voice_service(true);
                        }

                    } else if (!CameraflashAvailable) {
                        Voice_service(true);
                    }

                    Toast.makeText(this, "check", Toast.LENGTH_SHORT).show();
                } else {


                    editor.putBoolean(getString(R.string.key_check), false);
                    editor.commit();

                    Toast.makeText(this, "uncheck", Toast.LENGTH_SHORT).show();


                    /*
                    *
                    * check the camera service ,is available or not if yes
                    * So check the flash service is using or not , if using so did not start the voice service
                    * if not using start the voice service
                    *
                    * */
                    if (CameraflashAvailable) {

                        if (flashon.contentEquals("stop")) {
                            Voice_service(false);
                        }

                    } else if (!CameraflashAvailable) {
                        Voice_service(false);
                    }
                }

                break;

        }
    }


    /*
    *
    * Setting the radio button click
    *
    * */

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.key_MainActivity), MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        boolean Voice_check_value;
        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.radio_start:
                if (checked) {

                /*
                *
                * witting radio button state "start" in sharedprefreces
                *
                * */
                    editor.putString(getString(R.string.key), "start");
                    editor.commit();

                /*
                * starting the flash service
                * */
                    start_server(false);


                /*
                *
                * stopping voice service
                *
                * */
                    Voice_service(false);


                }

                break;
            case R.id.radio_stop:
                if (checked) {



                /*
                *
                * witting radio button state "stop" in sharedprefreces
                *
                * */
                    editor.putString(getString(R.string.key), "stop");
                    editor.commit();

                /*
                * stopping the flash service
                * */
                    start_server(true);

                /*
                *
                * starting the voice service if voice check box is true(clicked)
                * And if check is false stop the voice service
                *
                * */
                    Voice_check_value = sharedPref.getBoolean(getString(R.string.key_check), false);
                    if (Voice_check_value) {
                        Voice_service(true);
                    } else if (!Voice_check_value) {
                        Voice_service(false);
                    }


                }

                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.about) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /*
    *
    * function for Enable or disable the flash service
    *
    * */
    private void setDetectEnabled(boolean enable) {
        detectEnabled = enable;

        Intent intent = new Intent(this, CallDetectService.class);
        if (enable) {
            // start detect service
            startService(intent);


        } else {
            // stop detect service
            stopService(intent);


        }
    }


    /*
    *
    * starting the flash service
    *
    * */
    private void start_server(boolean value) {

        detectEnabled = value;
        Context context = getApplicationContext();
        PackageManager pm = context.getPackageManager();
        if (pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {

            setDetectEnabled(!detectEnabled);
            Toast.makeText(this, "start Camera flash is Available", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Sorry Camera flash is not Available", Toast.LENGTH_SHORT).show();

        }

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == MY_DATA_CHECK_CODE) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                //the user has the necessary data - create the TTS

            } else {
                //no data - install it now
                Intent installTTSIntent = new Intent();
                installTTSIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installTTSIntent);
            }
        }
    }


    /*
    *
    *
    * starting the text to speech service class
    *
    * */
    public void start_text_to_speech(Context context, Boolean SS) {

        Intent intent = new Intent(context, Text_to_speech.class);
        if (SS) {
            //start service
            context.startService(intent);
        } else {
            //stop service
            context.stopService(intent);
        }


    }


    /*
    *
    * getting the caller name corresponding to the number saved in phone
    *
    * */

    public static String getContactName(Context context, String phoneNumber) {
        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
        Cursor cursor = cr.query(uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);
        if (cursor == null) {
            return null;
        }
        String contactName = null;
        if (cursor.moveToFirst()) {
            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
        }

        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

        return contactName;
    }

    /*
    *
    * check the camera service is avialable or not
    *
    *
    * */


    public void CheckCameraService() {

        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.key_MainActivity), MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        Context context = getApplicationContext();
        PackageManager pm = context.getPackageManager();
        if (pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {

            editor.putBoolean(getString(R.string.key_camera_service), true);
            editor.commit();

            Log.e("camera", "Available");

        } else {


            editor.putBoolean(getString(R.string.key_camera_service), false);
            editor.commit();

        }

    }


    /*
    *
    * starting the voice call service
    *
    * */
    public void Voice_service(Boolean state) {

        Intent intent = new Intent(this, voice_call_Service.class);
        if (state) {
            startService(intent);
        } else {
            stopService(intent);
        }
    }

    /*
	 * Get the camera
	 */
    private void getCamera() {
        if (camera == null) {
            try {
                mPreviewTexture = new SurfaceTexture(0);
                camera = Camera.open();
                params = camera.getParameters();
            } catch (RuntimeException e) {
                Log.e("Camera Error", e.getMessage());
            }
        }
    }

    /*
     * Turning On flash
     */
    private void turnOnFlash() {
        if (!isFlashOn) {
            if (camera == null || params == null) {
                return;
            }
            // play sound
            playSound();

            params = camera.getParameters();
            params.setFlashMode(Parameters.FLASH_MODE_TORCH);
            camera.setParameters(params);

            /*
                    *
                    * if Build version is greater than Kitkat(19) then set Preview Texture of new Camera2 API
                    * for making compatible Lolipop
                    *
                    * */
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {

                try {
                    camera.setPreviewTexture(mPreviewTexture);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            camera.startPreview();
            isFlashOn = true;

            // changing button/switch image
            toggleButtonImage();
        }

    }

    /*
     * Turning Off flash
     */
    private void turnOffFlash() {
        if (isFlashOn) {
            if (camera == null || params == null) {
                return;
            }
            // play sound
            playSound();

            params = camera.getParameters();
            params.setFlashMode(Parameters.FLASH_MODE_OFF);
            camera.setParameters(params);

            /*
                    *
                    * if Build version is greater than Kitkat(19) then set Preview Texture of new Camera2 API
                    * for making compatible Lolipop
                    *
                    * */
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {

                try {
                    camera.setPreviewTexture(mPreviewTexture);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            camera.stopPreview();
            isFlashOn = false;

            // changing button/switch image
            toggleButtonImage();
        }
    }

    /*
     * Playing sound
     * will play button toggle sound on flash on / off
     * */
    private void playSound() {
        if (isFlashOn) {
            mp = MediaPlayer.create(MainActivity.this, R.raw.light_switch_off);
        } else {
            mp = MediaPlayer.create(MainActivity.this, R.raw.light_switch_on);
        }
        mp.setOnCompletionListener(new OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                // TODO Auto-generated method stub
                mp.release();
            }
        });
        mp.start();
    }

    /*
     * Toggle switch button images
     * changing image states to on / off
     * */
    private void toggleButtonImage() {
        if (isFlashOn) {
            flashbutton.setImageResource(R.drawable.btn_switch_on);
        } else {
            flashbutton.setImageResource(R.drawable.btn_switch_off);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();

        // on pause turn off the flash
        turnOffFlash();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();

//        // on resume turn on the flash
//        if (hasFlash)
//            turnOnFlash();
    }

    @Override
    protected void onStart() {
        super.onStart();

        // on starting the app get the camera params
        getCamera();
    }

    @Override
    protected void onStop() {
        super.onStop();

        // on stop release the camera
        if (camera != null) {
            camera.release();
            camera = null;
        }
    }


}
