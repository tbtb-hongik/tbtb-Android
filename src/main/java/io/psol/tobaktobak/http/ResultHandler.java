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
                // Server's response JSON data 가공
                try {
                    JSONObject result = new JSONObject((String) msg.obj);

                    // JSON parsing (for TTS)
                    String objInfoString = result.getString("Object");
                    String labelInfoString = result.getString("Label");
                    String textInfoString = result.getString("Text");

                    String TTS = "사진 속의 객체\n" + objInfoString + "\n사진 속의 디테일 객체\n" + labelInfoString;
//                    ((MainActivity)context).speakData(objInfoString);
//                    ((MainActivity)context).speakData(labelInfoString);
                    if (!textInfoString.isEmpty()) {
                        TTS += "\n사진에 글자가 포함되어 있습니다.\n" + textInfoString;
//                        ((MainActivity)context).speakData(textInfoString);
                    }
                    Log.e("result", "Text: "+ textInfoString);
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
