package com.kk.myvpaid;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.kk.myvpaid.VPAIDPlayerConfig.VPAIDAdState;

class VPAIDPlayer extends FrameLayout {

    interface VPAIDAdStateListener {
        void onVPAIDAdStateChanged(VPAIDAdState adState);
    }

    private VPAIDAdState adState = VPAIDAdState.ad_session_not_started;
    private WebView webView;
    private TextView skipButton;
    private ImageView muteButton;
    private CountDownTimer skipCountdown;
    private Handler handler = new Handler(Looper.getMainLooper());
    private VPAIDAdStateListener listener;
    private boolean alreadyReportedFinished;
    private OnClickListener onAdClickedListener;
    private boolean isInitiallyMuted, doClearWebViewCacheWhenDestroyed;
    private String vastXMLContents;
    private JavaScriptInterface javaScriptInterface = new JavaScriptInterface();

    public VPAIDPlayer(@NonNull Context context) {
        super(context);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
              LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        setLayoutParams(params);
        setBackgroundColor(Color.BLACK);//could be kept in config
    }

    VPAIDAdState getAdState() {
        return adState;
    }

    void setInitiallyMuted(boolean isInitiallyMuted) {
        this.isInitiallyMuted = isInitiallyMuted;
    }

    void setVastXMLContents(String vastXMLContents) {
        this.vastXMLContents = vastXMLContents;
    }

    void setVPAIDAdStateListener(VPAIDAdStateListener listener, OnClickListener onAdClickedListener) {
        this.listener = listener;
        this.onAdClickedListener = onAdClickedListener;
    }

    void setClearWebViewCacheWhenDestroyed(boolean doClearWebViewCacheWhenDestroyed) {
        this.doClearWebViewCacheWhenDestroyed = doClearWebViewCacheWhenDestroyed;
    }

    /**
     * Starts the vpaid ad.  Before calling, consider calling:
     *
     * setInitiallyMuted
     * setVastXMLContents
     * setVPAIDAdStateListener
     * setClearWebViewCacheWhenDestroyed
     */
    void startVPAIDAd() {
        webView = new WebView(getContext());
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
              LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, Gravity.CENTER);
        webView.setLayoutParams(params);
        addView(webView);
        VPAIDPlayerUtils.loadWebView(webView, onAdClickedListener);
        webView.addJavascriptInterface(javaScriptInterface, "AndroidInterface");
    }

    void resume() {
        if (adState == VPAIDAdState.ad_session_in_progress) {
            try {
                webView.loadUrl("javascript:play()");
            }catch (Throwable t) {
                VPAIDPlayerUtils.log("resume failed", t);
            }
        }
    }

    private void onSessionFinished(VPAIDAdState finishState) {
        if (!alreadyReportedFinished) {
            alreadyReportedFinished = true;
            VPAIDPlayerUtils.log("ad finished.  state: " + finishState.name());
            finishSession();
            adState = finishState;
            listener.onVPAIDAdStateChanged(finishState);
        }
    }

    class JavaScriptInterface {

        @JavascriptInterface
        public void onAdStarted() {
            adState = VPAIDAdState.ad_session_in_progress;
            VPAIDPlayerUtils.log("onAdStarted. adState: " + adState.name());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    addOverlayButtons();
                }
            });
            listener.onVPAIDAdStateChanged(VPAIDAdState.ad_session_in_progress);
        }

        @JavascriptInterface
        public void onAdCompleted() {
            onSessionFinished(VPAIDAdState.completed);
        }

        @JavascriptInterface
        public void onAdError() {
            onSessionFinished(VPAIDAdState.error);
        }

        @JavascriptInterface
        public void onAdCancelled() {
            onSessionFinished(VPAIDAdState.cancelled);
        }

        @JavascriptInterface
        public String getVastXML() {
            if (vastXMLContents != null)
                return vastXMLContents;
            return VPAIDPlayerUtils.getTestVastVPAIDXML(getContext());
        }
    }

    private void finishSession() {
        if (skipCountdown != null)
            skipCountdown.cancel();
    }

    private int muteButtonRes = R.drawable.choc_volume_up_large_white_18dp; //default sound on

    //can be called by the parent activity
    void destroy() {
        try {
            finishSession();
            if (doClearWebViewCacheWhenDestroyed) {
                webView.clearHistory();
                webView.clearCache(true);
            }
        }catch (Throwable t) {
            VPAIDPlayerUtils.log("destroy failed", t);
        }
    }

    private void addMuteButton() {
        muteButton = (ImageView) LayoutInflater.from(getContext()).inflate(R.layout.choc_mute_button, null, false);

        @SuppressLint("RtlHardcoded") FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.TOP | Gravity.LEFT);
        muteButton.setLayoutParams(layoutParams);
        addView(muteButton);
        muteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                muteSound(muteButtonRes == R.drawable.choc_volume_up_large_white_18dp);
            }
        });
        muteSound(isInitiallyMuted);
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
        skipButton = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.choc_skip_button, null, false);

        @SuppressLint("RtlHardcoded")
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.TOP | Gravity.RIGHT);
        skipButton.setLayoutParams(layoutParams);
        addView(skipButton);
        skipButton.bringToFront();
        skipCountdown = new CountDownTimer(1000 * VPAIDPlayerConfig.SKIP_AD_IN, 500) {
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
                        javaScriptInterface.onAdCancelled();
                    }
                });
            }
        };
        skipCountdown.start();
    }

    private void addOverlayButtons() {
        addMuteButton();
        addSkipButton();
    }

}
