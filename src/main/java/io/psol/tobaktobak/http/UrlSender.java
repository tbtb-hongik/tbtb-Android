package io.psol.tobaktobak.http;

import android.os.Handler;
import android.util.Log;

import com.squareup.okhttp.FormEncodingBuilder;

import java.util.HashMap;

public class UrlSender extends HttpSender {
    // Sender (server로 URL send)
    public UrlSender(Handler handler){
        super(handler);
        apiName = "android";
    }

    @Override
    public void setBodyContents(Object... Params) {

        if(Params.length !=0){
            body = new FormEncodingBuilder().add("os", "android").add("url", (String) Params[0]).build();
        } else {
            body = new FormEncodingBuilder().add("os", "android").add("url", "null").build();
        }
    }
}
