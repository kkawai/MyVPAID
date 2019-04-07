package com.kk.myvpaid;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import static com.kk.myvpaid.MainActivity.TAG;
import static com.kk.myvpaid.MainActivity.HTML_PAGE;

class MyWebviewClient extends WebViewClient {

    private void openBrowser(Context context, String url) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com"));
        context.startActivity(browserIntent);
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        Log.d(TAG,"shouldOverrideUrlLoading ");
        openBrowser(view.getContext(), url);
        return true;
    }

    @TargetApi(21)
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        Log.d(TAG,"shouldOverrideUrlLoading ");
        openBrowser(view.getContext(), request.getUrl().toString());
        return true;
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        Log.d(TAG,"onPageStarted " + url);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        Log.d(TAG,"onPageFinished " + url);
        if (url.endsWith(HTML_PAGE)) {
            view.loadUrl("javascript:play()");
        }
    }

    @Override
    public void onLoadResource(WebView view, String url) {
        //Log.d(TAG,"onLoadResource ");//url too noisy
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
}
