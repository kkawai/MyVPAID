package com.kk.myvpaid;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

class VPAIDWebViewClient extends WebViewClient {

    private View.OnClickListener onClickListener;

    VPAIDWebViewClient(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    private void openBrowser(Context context, String url) {
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        }catch (Throwable t) {
            VPAIDPlayerUtils.log("openBrowser to url failed: " + url, t);
        }
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        VPAIDPlayerUtils.log("shouldOverrideUrlLoading");
        openBrowser(view.getContext(), url);
        onClickListener.onClick(view);
        return true;
    }

    @TargetApi(21)
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        VPAIDPlayerUtils.log("shouldOverrideUrlLoading v21");
        openBrowser(view.getContext(), request.getUrl().toString());
        onClickListener.onClick(view);
        return true;
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        VPAIDPlayerUtils.log("onPageStarted " + url);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        VPAIDPlayerUtils.log("onPageFinished " + url);
        if (url.endsWith(VPAIDPlayerConfig.HTML_PAGE)) {
            view.loadUrl("javascript:play()");
        }
    }

    @Override
    public void onLoadResource(WebView view, String url) {
        //VPAIDPlayerUtils.log("onLoadResource ");//url too noisy
    }

    @Override
    public void onPageCommitVisible(WebView view, String url) {
        VPAIDPlayerUtils.log("onPageCommitVisible " + url);
    }

    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        VPAIDPlayerUtils.log("onReceivedError " + error.toString());
    }

    @Override
    public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
        VPAIDPlayerUtils.log("onReceivedHttpError " + errorResponse);
    }
}
