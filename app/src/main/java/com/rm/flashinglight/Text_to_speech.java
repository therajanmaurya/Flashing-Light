package com.rm.flashinglight;

import android.content.Intent;
import android.speech.tts.TextToSpeech;

/**
 * Created by rajanmaurya on 23/2/15.
 */
public class Text_to_speech  {

    private TextToSpeech myTTS;
    private int MY_DATA_CHECK_CODE = 0;

    public void Voice_start(){


    }

    public void Set_Voice_Engine(){

        Intent checkTTSIntent = new Intent();
        checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
       // startActivityForResult(checkTTSIntent, MY_DATA_CHECK_CODE);

    }

}
