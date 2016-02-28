package com.breg.scavengerhunt;

import android.annotation.TargetApi;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;


public class Speech extends AppCompatActivity {

    public static int TTS_DATA_CHECK = 1;
    public static int VOICE_RECOGNITION = 2;
    private TextToSpeech tts, ttsagain;
    private boolean ttsIsInit, ttsagainIsInit = false;
    public String demotext = "This is a test of the text-to-speech engine in Android.";
    EditText textInput;



    public void testSpeechRec(View v) {

        try{
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Just speak normally into your phone");
            intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 10);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.ENGLISH);
            startActivityForResult(intent, VOICE_RECOGNITION);
        } catch (ActivityNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    public void testTTS(View v) {

            demotext = "this is a test";


        Intent intent = new Intent(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(intent, TTS_DATA_CHECK);
    }

    @SuppressWarnings("deprecation")
    private void ttsUnder20(String text) {
        HashMap<String, String> map = new HashMap<>();
        map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "MessageId");
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, map);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void ttsGreater21(String text) {
        String utteranceId=this.hashCode() + "";
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == TTS_DATA_CHECK) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {

                Log.i("SpeechDemo", "## INFO 03: CHECK_VOICE_DATA_PASS");
                tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        if (tts.isLanguageAvailable(Locale.US) >= 0) {
                            tts.setLanguage(Locale.US);
                            tts.setPitch(0.8f);
                            tts.setSpeechRate(1.1f);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                ttsGreater21(demotext);
                            } else {
                                ttsUnder20(demotext);
                            }
                        }

                    }
                });

            } else {

                Log.i("SpeechDemo", "## INFO 04: CHECK_VOICE_DATA_FAILED, resultCode = " + resultCode);
                Intent installVoice = new Intent(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installVoice);
            }

        } else if (requestCode == VOICE_RECOGNITION) {
            Log.i("SpeechDemo", "## INFO 02: RequestCode VOICE_RECOGNITION = " + requestCode);
            if (resultCode == RESULT_OK) {
                List<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                float[] confidence = data.getFloatArrayExtra(RecognizerIntent.EXTRA_CONFIDENCE_SCORES);
                for (int i = 0; i < results.size(); i++) {
                    final String result = results.get(i);
                    Log.i("SpeechDemo", "## INFO 05: Result: " + result + " (confidence: " + confidence[i] + ")");
                }

            } else {
                Log.i("SpeechDemo", "## ERROR 01: Unexpected RequestCode = " + requestCode);

            }
        }



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
}