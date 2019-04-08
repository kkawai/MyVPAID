package com.kk.myvpaid;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class NoVideojsActivity extends AppCompatActivity {

    static final String TAG = "MyVPAID";

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        webView = findViewById(R.id.webview);
        webView.setBackgroundColor(Color.BLACK);
        webView.setWebChromeClient(new VPAIDPlayerUtils.MyWebChromeClientCustomPoster());
        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setAllowUniversalAccessFromFileURLs(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setHorizontalScrollBarEnabled(false);
        webView.setVerticalScrollBarEnabled(false);
        String basePath = "file:android_asset/no_videojs.html";
        webView.loadUrl(basePath);
    }
}
