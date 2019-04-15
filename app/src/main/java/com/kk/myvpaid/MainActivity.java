package com.kk.myvpaid;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.kk.myvpaid.VPAIDPlayerConfig.VPAIDAdState;

public class MainActivity extends Activity implements VPAIDPlayer.VPAIDAdStateListener {
    static final String TAG = "MyVPAID";
    private VPAIDPlayer vpaidPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (Build.VERSION.SDK_INT >= 19)
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        else
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        if (VPAIDPlayerConfig.DO_USE_SCREEN_RESIZE_HACK)
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(vpaidPlayer = new VPAIDPlayer(this));
        vpaidPlayer.setVPAIDAdStateListener(this, onAdClickListener);
        vpaidPlayer.setInitiallyMuted(true);
        vpaidPlayer.setClearWebViewCacheWhenDestroyed(false);
        vpaidPlayer.setVastXMLContents(null); //null - internal test vast xml will be used
        vpaidPlayer.startVPAIDAd();
    }

    private View.OnClickListener onAdClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Toast.makeText(MainActivity.this, "ad clicked!", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public void onVPAIDAdStateChanged(VPAIDAdState adState) {
        Log.d(TAG,"onVPAIDAdStateChanged. adState: " + adState.name());
        switch (adState) {
            case ad_session_in_progress:
                if (VPAIDPlayerConfig.DO_USE_SCREEN_RESIZE_HACK) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
                }
                break;
            case completed:
                finishVPAIDAdSession();
                break;
            case cancelled:
                finishVPAIDAdSession();
                break;
            case error:
                finishVPAIDAdSession();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        vpaidPlayer.resume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        vpaidPlayer.destroy();
    }

    private void finishVPAIDAdSession() {
        finish();
    }
}
