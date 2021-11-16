package com.example.pushnotificationdemobau;

import android.text.TextUtils;
import android.util.Log;

import com.huawei.hms.push.HmsMessageService;

public class DemoHmsMessageService extends HmsMessageService {
    private final String TAG = "DemoHmsMessageService";

    @Override
    public void onNewToken(String token) {
        Log.i(TAG, "received refresh token:" + token);

        if (!TextUtils.isEmpty(token)) {
            refreshedTokenToServer(token);
        }
    }

    private void refreshedTokenToServer(String token) {
        Log.i(TAG, "sending token to server. token:" + token);
    }

}
