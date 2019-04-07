package com.kk.myvpaid;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import java.io.IOException;
import java.io.InputStream;

import static com.kk.myvpaid.MainActivity.HTML_PAGE;

class Utils {

    static class MyWebChromeClientCustomPoster extends WebChromeClient {
        @Override
        public Bitmap getDefaultVideoPoster() {
            return Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888);
        }
    }

    static View.OnTouchListener getDisabledTouchListener() {
        return new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        };
    }

    static String getTestVastVPAIDXML(Context context) {
        String s = null;
        try {
            InputStream is = context.getResources().openRawResource(R.raw.vast_vpaid);
            s = getStringFromIs(is);
            is.close();
        }catch (Exception e){}
        return s;
    }

    private static String getStringFromIs(InputStream is) throws IOException {
        final StringBuilder out = new StringBuilder();
        byte[] b = new byte[4096];
        for (int n; (n = is.read(b)) != -1; ) {
            out.append(new String(b, 0, n));
        }
        return out.toString();
    }

    static DisplayMetrics getDisplayMetrics(Activity activity) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        //int width = displayMetrics.widthPixels;
        //int height = displayMetrics.heightPixels;
        //Log.d(TAG,"width: " + width + " height: " + height + " density: " + displayMetrics.density);
        return displayMetrics;
    }

    static String orientation(Configuration configuration) {
        if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            return "landscape";
        } else if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            return "portrait";
        }
        return "unknown orientation";
    }

    static void loadWebView(WebView webView) {
        webView.setOnTouchListener(Utils.getDisabledTouchListener());
        webView.setBackgroundColor(Color.BLACK);
        webView.setWebViewClient(new MyWebviewClient());
        webView.setWebChromeClient(new Utils.MyWebChromeClientCustomPoster());
        webView.getSettings().setAllowUniversalAccessFromFileURLs(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setHorizontalScrollBarEnabled(false);
        webView.setVerticalScrollBarEnabled(false);
        String basePath = "file:android_asset/" + HTML_PAGE;
        webView.loadUrl(basePath);
    }

}
