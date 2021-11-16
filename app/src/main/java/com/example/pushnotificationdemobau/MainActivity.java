package com.example.pushnotificationdemobau;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import com.huawei.agconnect.appmessaging.AGConnectAppMessaging;
import com.huawei.agconnect.config.AGConnectServicesConfig;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.aaid.HmsInstanceId;
import com.huawei.hms.aaid.entity.AAIDResult;
import com.huawei.hms.common.ApiException;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "MainActivity";
    private AGConnectAppMessaging appMessaging;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //region Huawei App Messaging Initialization
        appMessaging = AGConnectAppMessaging.getInstance();
        //endregion

        //region Huawei App Messaging set flag for forcibly requesting message data
        //Set the flag for forcibly requesting message data from the AppGallery Connect server
        // so that data can be obtained in real time during testing.
        appMessaging.setForceFetch();
        //endregion

        //region Huawei App Messaging get AAID
        getAAID();
        //endregion

        //region Huawei App Messaging add custom view to app messaging
        Button addCustomViewBtn = findViewById(R.id.display_app_message_btn);
        addCustomViewBtn.setOnClickListener(view -> {
            CustomView customView = new CustomView(MainActivity.this);
            appMessaging.addCustomView(customView);
        });
        //endregion

        //region Huawei App Messaging remove custom view from app messaging
        Button removeCustomViewBtn = findViewById(R.id.dismiss_app_message_btn);
        removeCustomViewBtn.setOnClickListener(view -> appMessaging.removeCustomView());
        //endregion

        //region Start our custom service for Android Notification
        Button startMyServiceBtn = findViewById(R.id.my_android_notification_btn);
        startMyServiceBtn.setOnClickListener(v -> startMyService());
        //endregion

        //region Get push token for HMS Push Kit
        getToken(); // HMS Push Kit
        //endregion
    }

    private void startMyService() {
        Intent intent = new Intent(this, MyForegroundService.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction("start");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }
    }

    private void getToken() {
        // Create a thread.
        new Thread() {
            @Override
            public void run() {
                try {
                    // Obtain the app ID from the agconnect-service.json file.
                    String appId = AGConnectServicesConfig.fromContext(MainActivity.this).getString("client/app_id");
                    // Set tokenScope to HCM.
                    String tokenScope = "HCM";
                    String token = HmsInstanceId.getInstance(MainActivity.this).getToken(appId, tokenScope);
                    Log.i(TAG, "get token: " + token);

                    // Check whether the token is empty.
                    if (!TextUtils.isEmpty(token)) {
                        sendRegTokenToServer(token);
                    }
                } catch (ApiException e) {
                    Log.e(TAG, "get token failed, " + e);
                }
            }
        }.start();
    }

    private void sendRegTokenToServer(String token) {
        Log.i(TAG, "sending token to server. token:" + token);
    }

    //Deletes push token if needed
    private void deleteToken() {
        // Create a thread.
        new Thread() {
            @Override
            public void run() {
                try {
                    // Obtain the app ID from the agconnect-service.json file.
                    String appId = AGConnectServicesConfig.fromContext(MainActivity.this).getString("client/app_id");

                    // Set tokenScope to HCM.
                    String tokenScope = "HCM";
                    // Delete the token.
                    HmsInstanceId.getInstance(MainActivity.this).deleteToken(appId, tokenScope);
                    Log.i(TAG, "token deleted successfully");
                } catch (ApiException e) {
                    Log.e(TAG, "deleteToken failed." + e);
                }
            }
        }.start();
    }

    //Obtain the device AAID in asynchronous mode for App Messaging
    private void getAAID() {
        HmsInstanceId inst  = HmsInstanceId.getInstance(this);
        Task<AAIDResult> idResult = inst.getAAID();
        idResult.addOnSuccessListener(aaidResult -> {
            String aaid = aaidResult.getId();
            Log.d(TAG, "getAAID success:" + aaid);

        }).addOnFailureListener(e -> Log.d(TAG, "getAAID failure:" + e));

    }

}