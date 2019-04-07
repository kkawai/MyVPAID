package com.kk.myvpaid;

import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class NoVideojsActivity extends AppCompatActivity {

    static final String TAG = "MyVPAID";
    static final String HTML_PAGE = "no_videojs.html";

    enum AdState {ad_session_in_progress, ad_session_not_started, error, completed, cancelled}

    private AdState adState = AdState.ad_session_not_started;

    private WebView webView;
    private WebViewClient webViewClient = new MyWebviewClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        webView = findViewById(R.id.webview);
        webView.setBackgroundColor(Color.BLACK);
        webView.setWebViewClient(webViewClient);
        webView.setWebChromeClient(new Utils.MyWebChromeClientCustomPoster());
        webView.getSettings().setAllowUniversalAccessFromFileURLs(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setHorizontalScrollBarEnabled(false);
        webView.setVerticalScrollBarEnabled(false);
        String basePath = "file:android_asset/"+HTML_PAGE;
        webView.loadUrl(basePath);
    }
}
