package com.rm.flashinglight;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity {


    private Toolbar toolbar;
    private RadioButton start, stop;
    private boolean detectEnabled;
    public static String keyvalue= null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        start = (RadioButton) findViewById(R.id.radio_start);
        stop = (RadioButton) findViewById(R.id.radio_stop);

        Context context = getApplicationContext();

        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        // getting value from sharedprefreces to enable the radio button
        keyvalue = sharedPref.getString(getString(R.string.key),"");


//        Log.e("value of keyvalue",keyvalue);
//        Toast.makeText(this, keyvalue, Toast.LENGTH_SHORT).show();

        if (keyvalue.length() == 0 || keyvalue == null) {
            start.setChecked(true);
            //starting the services if father camera is available

            start_server(false);

        }

        if (keyvalue.contentEquals("start") ) {
            Log.e("where","in start");
            Toast.makeText(this, "starting" ,Toast.LENGTH_SHORT).show();
            start.setChecked(true);
            //starting the services if father camera is available

            start_server(false);

        }
        if (keyvalue.contentEquals("stop")  ) {
            Log.e("where", "in stop");
            Toast.makeText(this, "stoping" ,Toast.LENGTH_SHORT).show();
            int n = 43;
            stop.setChecked(true);
            //stopping the services if father camera is available
            start_server(true);

        }
    }


    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.radio_start:
                if (checked)

                editor.putString(getString(R.string.key), "start");
                editor.commit();
                //witting value "start" in sharedprefreces
                start_server(false);

                //Toast.makeText(this, "start flashing light", Toast.LENGTH_SHORT).show();

                break;
            case R.id.radio_stop:
                if (checked)

                //Toast.makeText(this, "stop flashing light", Toast.LENGTH_SHORT).show();

                editor.putString(getString(R.string.key), "stop");
                editor.commit();
                //witting value "stop" in sharedprefreces
                start_server(true);


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


    private void setDetectEnabled(boolean enable) {
        detectEnabled = enable;

        Intent intent = new Intent(this, CallDetectService.class);
        if (enable) {
            // start detect service
            startService(intent);
            Toast.makeText(this,"server start",Toast.LENGTH_SHORT).show();


        } else {
            // stop detect service
            stopService(intent);
            Toast.makeText(this,"server stop",Toast.LENGTH_SHORT).show();

        }
    }

    private void start_server(boolean value){

        detectEnabled = value;
        Context context = getApplicationContext();
        PackageManager pm = context.getPackageManager();
        if (pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {

            setDetectEnabled(!detectEnabled);
            Toast.makeText(this, "start Camera flash is Available", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "start Sorry Camera flash is not Available", Toast.LENGTH_SHORT).show();

        }

    }



}
