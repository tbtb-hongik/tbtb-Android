package io.psol.tobaktobak;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import androidx.core.app.NotificationCompat;

public class MyService extends Service implements ClipboardManager.OnPrimaryClipChangedListener {
    // 클립보드 매니저 객체
    ClipboardManager clipboardManager;
    public ClipData data;

    @Override
    public void onCreate() {
        super.onCreate();

        // 클립보드 생성
        clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        clipboardManager.addPrimaryClipChangedListener(this);

        Log.d("클립보드", "매니저 생성");
    }

    IBinder binder = new LocalBinder();

    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return binder;
    }

    // 서비스 실행되면 처리해야하는 부분
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // 안드로이드 버전 호환 안되면 사용 못함
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

            NotificationChannel channel = new NotificationChannel("service", "Service", NotificationManager.IMPORTANCE_HIGH);
            channel.enableLights(true);
//            channel.setLightColor(Color.RED);
            channel.enableVibration(true);

            manager.createNotificationChannel(channel);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "service");

            builder.setSmallIcon(android.R.drawable.ic_menu_search);
            builder.setContentTitle("또박또박");
            builder.setContentText("실행중");
            builder.setAutoCancel(true);

            Notification notification = builder.build();

            startForeground(10, notification);
        }

        // 별도 스레드 만들어줘서 비동기처리
        ThreadClass thread = new ThreadClass();
        thread.start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        clipboardManager.removePrimaryClipChangedListener(this);
        Log.d("클립보드", "매니저 종료");
    }

    // 서비스 수행
    @Override
    public void onPrimaryClipChanged() {
        // 복사 할 때마다 복사된 데이터 추출
        if (clipboardManager != null && clipboardManager.getPrimaryClip() != null) {
            data = clipboardManager.getPrimaryClip();
            Log.d("클립보드", "클립보드 : " + data.toString());
            // 한번의 복사로 복수 데이터를 넣었을 수 있으므로, 모든 데이터를 가져온다.
            int dataCount = data.getItemCount();
            for (int i = 0; i < dataCount; i++) {
                Log.d("클립보드", "clip data - item : " + data.getItemAt(i).coerceToText(this));
            }
        } else {
            Log.d("클립보드", "No Manager or No Clip data");
        }
    }


    class ThreadClass extends Thread {
        @Override
        public void run() {

        }
    }

    public class LocalBinder extends Binder {

        public MyService getService() {
            return MyService.this;
        }
    }

    // 메인에서 데이터 가져와서 쓰려고 만든 함수 (확장기능)
    public ClipData getData() {
        return data;
    }
}


