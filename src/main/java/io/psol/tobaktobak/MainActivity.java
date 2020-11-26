package io.psol.tobaktobak;

import androidx.appcompat.app.AppCompatActivity;


import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.webkit.URLUtil;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.psol.tobaktobak.http.HttpSender;
import io.psol.tobaktobak.http.ResultHandler;
import io.psol.tobaktobak.http.UrlSender;
import static android.speech.tts.TextToSpeech.ERROR;

public class MainActivity extends AppCompatActivity {
    // 웹뷰
    private WebView webView;
    private WebSettings webSettings;
    private String ImageURL;
    private EditText txtUrl;

    // 소켓통신
    private String return_msg;
    Socket inetSocket = null;

    // TTS
    private Context mainContext;
    private TextToSpeech tts;

    // 접속한 서비스 객체
    /*
    MyService ipc_service = null;
    */
    // 서비스 접속 관리하는 객체
    /*
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // 전달 받은 바인더 클래스를 이용해 서비스 객체를 추출함
            MyService.LocalBinder binder = (MyService.LocalBinder) service;
            ipc_service = binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            ipc_service = null;
        }
    };
    */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // webView 속성
        webView = (WebView) findViewById(R.id.webView1);
        webView.setWebViewClient(new WebViewClient());
        webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportMultipleWindows(false); // 새창 띄우기 허용 여부
        webSettings.setJavaScriptCanOpenWindowsAutomatically(false); // 자바스크립트 새창 띄우기(멀티뷰) 허용 여부
        webSettings.setLoadWithOverviewMode(true); // 메타태그 허용 여부
        webSettings.setUseWideViewPort(true); // 화면 사이즈 맞추기 허용 여부
        webSettings.setSupportZoom(false); // 화면 줌 허용 여부
        webSettings.setBuiltInZoomControls(false); // 화면 확대 축소 허용 여부
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN); // 컨텐츠 사이즈 맞추기
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE); // 브라우저 캐시 허용 여부
        webSettings.setDomStorageEnabled(true); // 로컬저장소 허용 여부

        webView.setWebViewClient(new WebViewClientClass());
        registerForContextMenu(webView);

        Button button = (Button) findViewById(R.id.btnGo);
        txtUrl = (EditText) findViewById(R.id.txtURL);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                String urlString = webView.getUrl().toString();
                txtUrl.setText(urlString);
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String urlString = txtUrl.getText().toString();
                if (urlString.startsWith("http") != true) {
                    urlString = "http://" + urlString;
                }
                webView.loadUrl(urlString);
            }
        });

        webView.loadUrl("https://www.naver.com");

        // TTS 생성 및 초기화
        mainContext = this;
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != ERROR){
                    tts.setLanguage(Locale.KOREAN);
                }
            }
        });

        // 서비스 가동 관련
        /*
        Intent intent = new Intent(this, MyService.class);

        // 현재 서비스가 가동 중인지
        boolean chk = isServiceRunning("io.psol.tobaktobak.MyService");

        // 서비스 가동

        if (chk == false) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent);
            } else {
                startService(intent);
            }
        }
        // 서비스에 접속
        bindService(intent, connection, BIND_AUTO_CREATE);

         */

        // 접근성 권한
        /*
        if(!isAccessibilityPermissions()){
           serAccessibilityPermissions();
        }

         */
    }

    // TTS 메소드
    public void speakData(String data){
        if(!tts.isSpeaking()){
            tts.speak(data, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    /*================= 서비스 관련 메소드 =================*/

    // 실행 중인 서비스 탐색 메소드
    private boolean isServiceRunning(String name) {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        // 현재 실행 중인 서비스 이름 가져오기
        List<ActivityManager.RunningServiceInfo> list = manager.getRunningServices(Integer.MAX_VALUE);
        for (ActivityManager.RunningServiceInfo info : list) {
            if (info.service.getClassName().equals(name)) {
                Log.d("test", "서비스 가져오기");
                return true;
            }
        }
        return false;
    }

    /*================= 접근성 관련 메소드 =================*/

    // 접근성 권한 있는지 탐색 메소드
    private boolean isAccessibilityPermissions() {
        AccessibilityManager accessibilityManager = (AccessibilityManager) getSystemService(Context.ACCESSIBILITY_SERVICE);

        // getEnabledAccessibilityServiceList는 현재 접근성 권한을 가진 리스트 리턴
        List<AccessibilityServiceInfo> list = accessibilityManager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.DEFAULT);

        for (AccessibilityServiceInfo info : list) {

            // 접근성 권한을 가진 앱의 패키지 네임과 패키지 네임이 같으면 현재앱이 접근성 권한을 가지고 있다고 판단
            if (info.getResolveInfo().serviceInfo.packageName.equals(getApplication().getPackageName())) {
                return true;
            }
        }
        return false;
    }

    // 접근성 설정화면으로 전환
    private void serAccessibilityPermissions() {
        AlertDialog.Builder gsDialog = new AlertDialog.Builder(this);
        gsDialog.setTitle("접근성 권한 설정");
        gsDialog.setMessage("접근성 권한을 필요로 합니다.");
        gsDialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
                return;
            }
        }).create().show();
    }

    /*================= 웹뷰 관련 메소드 =================*/

    //뒤로가기 버튼 이벤트
    //웹뷰에서 뒤로가기 버튼을 누르면 뒤로가짐
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    // 페이지 이동
    private class WebViewClientClass extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.d("check URL", url);
            view.loadUrl(url);
            return true;
        }
    }

    // image long click 메뉴생성
    @Override
    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
        super.onCreateContextMenu(contextMenu, view, contextMenuInfo);

        final WebView.HitTestResult webViewHitTestResult = webView.getHitTestResult();

        // 이미지 타입만 감지
        if (webViewHitTestResult.getType() == WebView.HitTestResult.IMAGE_TYPE ||
                webViewHitTestResult.getType() == WebView.HitTestResult.SRC_ANCHOR_TYPE) {
            contextMenu.setHeaderTitle("메뉴");
            contextMenu.add(0, 1, 0, "사진 전송")
                    .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            ImageURL = webViewHitTestResult.getExtra();
                            if (URLUtil.isValidUrl(ImageURL)) {
                                // HTTP Request
                                // response 받은 TTS data 처리를 위해 Context 연결해줌
                                HttpSender sender = new UrlSender(new ResultHandler(mainContext));
                                sender.setBodyContents(ImageURL);
                                sender.send();

                                Log.e("HTTPSender", "전송완료");
                                //Toast.makeText(getApplicationContext(), "전송", Toast.LENGTH_LONG).show();
                                /* socket Request
                                TCPclient tcpThread = new TCPclient(ImageURL);
                                Thread thread = new Thread(tcpThread);
                                thread.start();
                                */

                                /*================= 이미지 다운로드 (확장기능) =================*/
                                /*
                                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(ImageURL));
                                request.allowScanningByMediaScanner();


                                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                                DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                                downloadManager.enqueue(request);

                                Toast.makeText(MainActivity.this,"다운로드 완료.",Toast.LENGTH_LONG).show();
                                 */
                            }
                            else {
                                Toast.makeText(MainActivity.this, "전송 실패.", Toast.LENGTH_LONG).show();
                            }
                            return false;
                        }
                    });
        }
    }

    /*================= Socket 관련 클래스 및 메소드 =================*/
    /*
    private class TCPclient implements Runnable {
        private static final String serverIP = "ws://13.251.164.73";
        private static final int serverPort = 9999;
        private String msg;

        public TCPclient(String _msg) {
            this.msg = _msg;
        }

        @Override
        public void run() {
            try {
                Log.d("TCP", "C: Connecting... ");
                inetSocket = new Socket(serverIP, serverPort);

                Log.d("TCP", "C: Connect! ");
                try {
                    Log.d("TCP", "C: Sending " + msg + "");
                    PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(inetSocket.getOutputStream())), true);

                    out.println(msg);
                    Log.d("TCP", "C: Sent " + msg + "");
                    Log.d("TCP", "C: Done.");

                    BufferedReader in = new BufferedReader(new InputStreamReader(inetSocket.getInputStream()));
                    Log.d("TCP", "C: in 생성");
                    return_msg = in.readLine();

                    Log.d("TCP", "C: 서버로 보낸 메세지 : " + return_msg);
                } catch (Exception e) {
                    Log.e("TCP", "C: Error1", e);
                } finally {
                    inetSocket.close();
                }
            } catch (Exception e) {
                Log.e("TCP", "C: Error2", e);
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            inetSocket.close(); //소켓을 닫는다.
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

     */
}
