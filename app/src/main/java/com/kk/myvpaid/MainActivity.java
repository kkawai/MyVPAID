package com.kk.myvpaid;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    static final String TAG = "MyVPAID";
    static final String HTML_PAGE = "vpaid_player.html";
    enum AdState {ad_session_in_progress, ad_session_not_started, error, completed, cancelled}
    private Handler handler = new Handler(Looper.myLooper());
    private AdState adState = AdState.ad_session_not_started;
    private WebView webView;
    private TextView skipButton;
    private ImageView muteButton;
    private CountDownTimer skipCountdown;
    private static final int SKIP_AD_IN = 8;

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
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                startTimer();
            }
        });
    }

    @JavascriptInterface
    public void onAdCompleted() {
        if (adState == AdState.ad_session_in_progress)
            adState = AdState.completed;
        Log.d(TAG,"onAdCompleted. adState: " + adState.name());
        finishSession();
    }

    @JavascriptInterface
    public void onAdError() {
        if (adState != AdState.completed)
            adState = AdState.error;
        Log.d(TAG,"onAdError. adState: " + adState.name());
        finishSession();
    }

    @JavascriptInterface
    public void onAdCancelled() {
        if (adState != AdState.completed) {
            adState = AdState.cancelled;
        }
        Log.d(TAG,"onAdCancelled. adState: " + adState.name());
        finishSession();
    }

    private void finishSession() {
        if (skipCountdown != null)
            skipCountdown.cancel();
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

    private void addView(View view) {
        FrameLayout frameLayout = findViewById(R.id.main_frame);
        frameLayout.addView(view);
    }

    private int muteButtonRes = R.drawable.choc_volume_up_large_white_18dp; //default sound on

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (skipCountdown != null)
            skipCountdown.cancel();
    }



    private void addMuteButton() {
        muteButton = (ImageView) LayoutInflater.from(this).inflate(R.layout.choc_mute_button, null, false);

        @SuppressLint("RtlHardcoded") FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.TOP | Gravity.LEFT);
        muteButton.setLayoutParams(layoutParams);
        addView(muteButton);
        muteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                muteSound(muteButtonRes == R.drawable.choc_volume_up_large_white_18dp);
            }
        });
        muteSound(false); //todo true if inview
        muteButton.bringToFront();
    }

    private void muteSound(boolean doMute) {
        if (doMute) {
            webView.loadUrl("javascript:mute()");
            muteButtonRes = R.drawable.choc_volume_off_large_white_18dp;
        } else {
            webView.loadUrl("javascript:unMute()");
            muteButtonRes = R.drawable.choc_volume_up_large_white_18dp;
        }
        muteButton.setImageResource(muteButtonRes);
    }

    private void addSkipButton() {
        skipButton = (TextView) LayoutInflater.from(this).inflate(R.layout.choc_skip_button, null, false);

        @SuppressLint("RtlHardcoded")
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.TOP | Gravity.RIGHT);
        skipButton.setLayoutParams(layoutParams);
        addView(skipButton);
        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAdCancelled();
            }
        });
        skipButton.bringToFront();
    }

    private void startTimer() {
        addMuteButton();
        addSkipButton();
        skipCountdown = new CountDownTimer(1000 * SKIP_AD_IN, 500) {
            @Override
            public void onTick(final long millisUntilFinished) {
                skipButton.setVisibility(View.VISIBLE);
                int seconds = (int)millisUntilFinished/1000;
                if (seconds == 0)
                    skipButton.setText("         ");
                else
                    skipButton.setText("Skip Ad in " + seconds + " Sec");
            }

            @Override
            public void onFinish() {
                skipButton.setText("Skip");
                skipButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        skipAd();
                    }
                });
            }
        };
        skipCountdown.start();
    }

    private void skipAd() {
        onAdCompleted();
        onAdCancelled();
    }
}
