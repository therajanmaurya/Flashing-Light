package com.rm.flashinglight;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity {


    private Toolbar toolbar;
    private RadioButton start, stop;
    private boolean detectEnabled, checkbox, CameraService;
    public static String keyvalue = null;
    private int MY_DATA_CHECK_CODE = 0;
    private TextToSpeech myTTS;
    public Text_to_speech t;
    CheckBox box;


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
       * Checking Camera Service is available or Not
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
        if (id == R.id.action_settings) {
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

}
