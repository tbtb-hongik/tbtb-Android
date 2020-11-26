package io.psol.tobaktobak.http;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import io.psol.tobaktobak.MainActivity;

public abstract class HttpSender {
    private static final String URL = "http://13.251.164.73:5555/";

//    private static final String URL = "http://172.17.23.49:5555/";

    protected String apiName;
    protected RequestBody body;

    private Context context;

    protected Handler handler;

    public HttpSender(Handler handler) {
        this.handler = handler;
    }

    public abstract void setBodyContents(Object... Params);

    public void send() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                OkHttpClient client = new OkHttpClient();
                client.setConnectTimeout(10, TimeUnit.SECONDS);
                Request request = new Request.Builder().url(URL + apiName).post(body).build();
                Message msg = handler.obtainMessage();
                try {
                    Response response = client.newCall(request).execute();

                    // server로부터 받은 TTS data
                    msg.obj = response.body().string();

                    Log.e("HTTPSender", "Server Response : " + msg.obj);
                    msg.what = 0;
                } catch (IOException e) {
                    Log.e("HTTPSender", "서버 연결 실패" + e);
                    msg.what = -1;
                }
                msg.sendToTarget();
                return null;
            }
        }.execute();
    }
}
