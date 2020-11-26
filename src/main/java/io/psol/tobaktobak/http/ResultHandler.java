package io.psol.tobaktobak.http;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import io.psol.tobaktobak.MainActivity;

public class ResultHandler extends Handler {
    private Context context;

    public ResultHandler(Context context){
        this.context = context;
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what){
            case 0:
                try {
//                    String TTS = (String) msg.obj;
//                    ((MainActivity)context).speakData(TTS);
                    JSONObject result = new JSONObject((String) msg.obj);
                    String objInfoString = result.getString("Object");
                    String labelInfoString = result.getString("Label");
                    String textInfoString = result.getString("Text");

                    String TTS = "객체 " + objInfoString + "\n라벨 " + labelInfoString;
//                    ((MainActivity)context).speakData(objInfoString);
//                    ((MainActivity)context).speakData(labelInfoString);
                    if (textInfoString != null) {
                        TTS += "\n글자 " + textInfoString;
//                        ((MainActivity)context).speakData(textInfoString);
                    }
                    ((MainActivity)context).speakData(TTS);
                    Toast.makeText(context, TTS, Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case -1:

                break;
        }
    }
}
