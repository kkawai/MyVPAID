package com.kk.myvpaid;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    static final String TAG = "MyVPAID";
    static final String HTML_PAGE = "vpaid_player.html";

    enum AdState {ad_session_in_progress, ad_session_not_started, error, completed, cancelled}

    private Handler handler = new Handler(Looper.myLooper());

    private AdState adState = AdState.ad_session_not_started;

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_main);
        webView = findViewById(R.id.webview);
        Utils.loadWebView(webView);
        webView.addJavascriptInterface(this, "AndroidInterface");
    }

    @JavascriptInterface
    public void onAdStarted() {
        adState = AdState.ad_session_in_progress;
        Log.d(TAG,"onAdStarted. adState: " + adState.name());
        handler.post(new Runnable() {
            @Override
            public void run() {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
            }
        });
    }

    @JavascriptInterface
    public void onAdCompleted() {
        if (adState == AdState.ad_session_in_progress)
            adState = AdState.completed;
        Log.d(TAG,"onAdCompleted. adState: " + adState.name());
    }

    @JavascriptInterface
    public void onAdError() {
        if (adState != AdState.completed)
            adState = AdState.error;
        Log.d(TAG,"onAdError. adState: " + adState.name());
    }

    @JavascriptInterface
    public void onAdCancelled() {
        if (adState != AdState.completed) {
            adState = AdState.cancelled;
        }
        Log.d(TAG,"onAdCancelled. adState: " + adState.name());
        finish();
    }

    @JavascriptInterface
    public void log(String message) {
        Log.d("MyJS", message);
    }

    @JavascriptInterface
    public String getVastXML() {
        return Utils.getTestVastVPAIDXML(this); //todo change later
    }
}
