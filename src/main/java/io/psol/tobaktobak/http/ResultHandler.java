package io.psol.tobaktobak.http;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import androidx.annotation.NonNull;

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
                String TTS = (String) msg.obj;
                ((MainActivity)context).speakData(TTS);
                break;
            case -1:

                break;
        }
    }
}
