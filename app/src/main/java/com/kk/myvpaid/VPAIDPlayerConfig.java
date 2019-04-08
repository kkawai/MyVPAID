package com.kk.myvpaid;

class VPAIDPlayerConfig {

    enum VPAIDAdState {ad_session_in_progress, ad_session_not_started, error, completed, cancelled}

    private VPAIDPlayerConfig(){}
    static boolean DO_USE_SCREEN_RESIZE_HACK = true;
    static final String HTML_PAGE = "vpaid_player.html";
    static final int SKIP_AD_IN = 8; //seconds
    static final String TAG = "VPAIDPlayer";
}
