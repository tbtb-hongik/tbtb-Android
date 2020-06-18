package io.psol.tobaktobak.http;

import android.os.Handler;

import com.squareup.okhttp.FormEncodingBuilder;

public class UrlSender extends HttpSender {
    //TTS 구현 후 Resulthandler에 결과값 TTS화
    public UrlSender(Handler handler){
        super(handler);
        apiName = "android";
    }

    @Override
    public void setBodyContents(Object... Params) {
        if(Params.length !=0){
            body = new FormEncodingBuilder().add("url", (String)Params[0]).build();
        } else {
            body = new FormEncodingBuilder().add("url", "null").build();
        }
    }
}
