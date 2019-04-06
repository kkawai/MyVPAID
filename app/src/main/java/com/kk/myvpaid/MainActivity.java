package com.kk.myvpaid;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
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

    public static final String TAG = "MyVPAID";

    WebView webView;
    WebViewClient webViewClient = new WebViewClient() {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            Log.d(TAG,"shouldOverrideUrlLoading ");
            return super.shouldOverrideUrlLoading(view, request);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            Log.d(TAG,"onPageStarted " + url);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            Log.d(TAG,"onPageFinished " + url);
            if (url.endsWith("sample.html")) {
                view.loadUrl("javascript:play()");
            }
        }

        @Override
        public void onLoadResource(WebView view, String url) {
            Log.d(TAG,"onLoadResource " + url);
        }

        @Override
        public void onPageCommitVisible(WebView view, String url) {
            Log.d(TAG,"onPageCommitVisible " + url);
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            Log.d(TAG,"onReceivedError " + error.toString());
        }

        @Override
        public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
            Log.d(TAG,"onReceivedHttpError " + errorResponse);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        webView = findViewById(R.id.webview);
        webView.setBackgroundColor(Color.BLACK);
        webView.setWebViewClient(webViewClient);
        webView.setWebChromeClient(new WebChromeClientCustomPoster());
        webView.getSettings().setAllowUniversalAccessFromFileURLs(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setHorizontalScrollBarEnabled(false);
        webView.setVerticalScrollBarEnabled(false);
        String basePath = "file:android_asset/sample.html";
        webView.loadUrl(basePath);
        webView.addJavascriptInterface(this, "AndroidInterface");

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;
        Log.d(TAG,"width: " + width + " height: " + height + " density: " + displayMetrics.density);
    }

    @JavascriptInterface
    public void log(String message) {
        Log.d("MyJS", message);
    }

    @JavascriptInterface
    public String getVastXML() {
        return getVastVpaidXml();
    }

    private String getVastVpaidXml() {
        String s = null;
        try {
            InputStream is = getResources().openRawResource(R.raw.vast_vpaid);
            s = getStringFromIs(is);
            is.close();
        }catch (Exception e){}
        return s;
    }

    static String getStringFromIs(InputStream is) throws IOException {
        final StringBuilder out = new StringBuilder();

        byte[] b = new byte[4096];
        for (int n; (n = is.read(b)) != -1; ) {
            out.append(new String(b, 0, n));
        }

        return out.toString();
    }

    private class WebChromeClientCustomPoster extends WebChromeClient {

        @Override
        public Bitmap getDefaultVideoPoster() {
            return null;
        }
    }
}
