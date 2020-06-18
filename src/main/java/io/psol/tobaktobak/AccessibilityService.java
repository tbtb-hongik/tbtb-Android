package io.psol.tobaktobak;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.view.menu.MenuView;

public class AccessibilityService extends android.accessibilityservice.AccessibilityService {

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        // 접근성 이해를 위한 log
        Log.e("접근성", "Catch Event Package Name : " + event.getPackageName());
        Log.e("접근성", "Catch Event TEXT : " + event.getText());
        Log.e("접근성", "Catch Event ContentDescription : " + event.getContentDescription());
        Log.e("접근성", "Catch Event getSource : " + event.getSource());

        // Chrome 기반으로 이미지 클릭시 이미지 정보 빼오기
        if (event.getSource() != null) {
            if (event.getSource().getClassName().toString().equals("android.widget.Image")) {
                Log.e("접근성", "궁금쓰 : " + event.getSource().getClassName());
                Rect rect = new Rect();
                event.getSource().getBoundsInScreen(rect);
                Log.e("접근성", "궁금쓰 : " + rect.toString() + rect.centerX() + rect.centerY());
                Log.e("접근성", "궁금쓰 : " + event.getItemCount());
                AccessibilityNodeInfo node = event.getSource();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        Log.e("접근성", "궁금..! : " + event.getSource().getText().toString());
                    }
                }


            }
        }
        Log.e("접근성", ": " + event.getItemCount());
        //Log.e("접근성", "궁금쓰 : " + event.getSource().getClassName());
        //Log.e("접근성", "궁금쓰 : " + );
        Log.e("접근성", "=========================================================================");


    }

    // 접근성 열어줄 항목 처리(일단 지금은 이미지 클릭에 대한 부분만)
    @Override
    public void onServiceConnected() {
        AccessibilityServiceInfo info = new AccessibilityServiceInfo();

        info.eventTypes = AccessibilityEvent.TYPE_VIEW_CLICKED;
        info.feedbackType = AccessibilityServiceInfo.DEFAULT | AccessibilityServiceInfo.FEEDBACK_HAPTIC;
        info.notificationTimeout = 100;

        this.setServiceInfo(info);
    }

    @Override
    public void onInterrupt() {
        Log.e("접근성", "OnInterrupt");
    }

    // 이미지 좌표 get함수
    public int getX(AccessibilityEvent event) {
        Rect rect = new Rect();
        event.getSource().getBoundsInScreen(rect);
        return rect.centerX();
    }

    public int getY(AccessibilityEvent event) {
        Rect rect = new Rect();
        event.getSource().getBoundsInScreen(rect);
        return rect.centerY();
    }
}

