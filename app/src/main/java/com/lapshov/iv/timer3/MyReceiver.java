package com.lapshov.iv.timer3;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.Calendar;
import java.util.Locale;

public class MyReceiver extends BroadcastReceiver implements
        TextToSpeech.OnInitListener {
    private TextToSpeech tts;

    @Override
    public void onReceive(Context context, Intent intent) {
        tts = new TextToSpeech(context, this);
    }

    @Override
    public void onInit(int status) {

        if (status == TextToSpeech.SUCCESS) {

            int result = tts.setLanguage(Locale.UK);

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            } else {
                speakOut(String.format("Current time is %d hours and %d minutes", Calendar.getInstance().get(Calendar.HOUR), Calendar.getInstance().get(Calendar.MINUTE)));
            }

        } else {
            Log.e("TTS", "Initilization Failed!");
        }

    }

    private void speakOut(String text) {
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }
}