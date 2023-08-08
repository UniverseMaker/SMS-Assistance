package com.dspark.smsassistance;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessaging;

public class FBInstanceIDService extends FirebaseInstanceIdService {
    private static final String TAG = FBInstanceIDService.class.getSimpleName();
    //FBRemoteConfig mFBRemoteConfig;
    String token = null;

    // 토큰 재생성
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        FirebaseMessaging.getInstance().subscribeToTopic("all");
        token = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "token = " + token);

        //mFBRemoteConfig = new FBRemoteConfig(this);
        //sendRegistrationToServer(token);
    }
}