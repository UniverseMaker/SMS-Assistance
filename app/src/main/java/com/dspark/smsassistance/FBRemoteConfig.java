package com.dspark.smsassistance;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.dspark.smsassistance.utility.UniqueID;

import static android.content.Context.MODE_PRIVATE;

public class FBRemoteConfig {
    Context context;
    FirebaseRemoteConfig mFirebaseRemoteConfig = null;
    long cacheExpiration = 1200; //캐쉬초... 기본은 12시간 (60*60*12)

    public FBRemoteConfig(Context _context){
        context = _context;
    }

    public void initialize() {
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                // Debug일 때 Developer Mode를 enable 하여 캐쉬 설정을 변경한다.
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build();

        mFirebaseRemoteConfig.setConfigSettings(configSettings);
        // 로컬 기본값을 저장한 xml을 설정한다.
        mFirebaseRemoteConfig.setDefaults(R.xml.remote_config_defaults);

        // 기본 캐쉬 만료시간은 12시간이다. Developer Mode 여부에 따라 fetch()에 적설한 캐시 만료시간을 넘긴다.
        cacheExpiration = 1200; // 1 hour in seconds.
        // If your app is using developer mode, cacheExpiration is set to 0, so each fetch will
        // retrieve values from the service.
        if (mFirebaseRemoteConfig.getInfo().getConfigSettings().isDeveloperModeEnabled()) {
            cacheExpiration = 0;
        }

        /*
        mFirebaseRemoteConfig.fetch(cacheExpiration)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            //Toast.makeText(MainActivity.this, "Fetch Succeeded", Toast.LENGTH_SHORT).show();

                            // After config data is successfully fetched, it must be activated before newly fetched
                            // values are returned.
                            mFirebaseRemoteConfig.activateFetched();

                        } else {
                            //Toast.makeText(MainActivity.this, "Fetch Failed", Toast.LENGTH_SHORT).show();
                        }
                        //displayWelcomeMessage();
                    }
                });
        */
    }

    /*
    public static void initialize(Context context) {
        if (!FirebaseApp.getApps(context).isEmpty()) return;
        FirebaseApp.initializeApp(context, FirebaseOptions, fromResource(context));
    }
    */

    public void onFirebaseConfigLoad_defaultWork()
    {
        try {

            //final FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
            mFirebaseRemoteConfig.fetch(cacheExpiration)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                try {
                                    //Toast.makeText(MainActivity.this, "Fetch Succeeded", Toast.LENGTH_SHORT).show();

                                    // After config data is successfully fetched, it must be activated before newly fetched
                                    // values are returned.
                                    mFirebaseRemoteConfig.activateFetched();
                                    checkVersion(getConfigValue("version_code"), getConfigValue("version_name"));
                                    loadNotice();
                                }catch (Exception e){

                                }
                            } else {
                                //Toast.makeText(MainActivity.this, "Fetch Failed", Toast.LENGTH_SHORT).show();
                                try{
                                    checkVersion(getConfigValue("version_code"), getConfigValue("version_name"));
                                    loadNotice();
                                }catch (Exception e){

                                }
                            }
                            //displayWelcomeMessage();
                        }
                    });
        }catch (Exception e){

        }
    }

    public void onFirebaseConfigLoad_createInstance()
    {
        try {

            //final FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
            mFirebaseRemoteConfig.fetch(cacheExpiration)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                try {
                                    //Toast.makeText(MainActivity.this, "Fetch Succeeded", Toast.LENGTH_SHORT).show();

                                    // After config data is successfully fetched, it must be activated before newly fetched
                                    // values are returned.
                                    mFirebaseRemoteConfig.activateFetched();

                                    agreementWork();

                                    //((FBInstanceIDService)context).sendRegistrationToServer();
                                    new Thread() {
                                        public void run() {
                                            sendRegistrationToServer();

                                            Bundle bun = new Bundle();
                                            bun.putString("data", "");
                                            Message msg = handler.obtainMessage();
                                            msg.setData(bun);
                                            handler.sendMessage(msg);
                                        }
                                    }.start();
                                }catch (Exception e){

                                }
                            } else {
                                //Toast.makeText(MainActivity.this, "Fetch Failed", Toast.LENGTH_SHORT).show();
                            }
                            //displayWelcomeMessage();
                        }
                    });
        }catch (Exception e){

        }
    }

    public void onFirebaseConfigLoad()
    {
        long cacheExpiration = 0; //캐쉬초... 기본은 12시간 (60*60*12)
        final FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        mFirebaseRemoteConfig.fetch(cacheExpiration)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            //Toast.makeText(MainActivity.this, "Fetch Succeeded", Toast.LENGTH_SHORT).show();

                            // After config data is successfully fetched, it must be activated before newly fetched
                            // values are returned.
                            mFirebaseRemoteConfig.activateFetched();
                        } else {
                            //Toast.makeText(MainActivity.this, "Fetch Failed", Toast.LENGTH_SHORT).show();
                        }
                        //displayWelcomeMessage();
                    }
                });
    }

    public String getConfigValue(String key) {
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        return mFirebaseRemoteConfig.getString(key);
    }

    public static void checkGooglePlayServices(MainActivity ma) {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int status = googleApiAvailability.isGooglePlayServicesAvailable(ma);

        if (status != ConnectionResult.SUCCESS) {
            Dialog dialog = googleApiAvailability.getErrorDialog(ma, status, -1);
            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {

                }
            });
            dialog.show();

            googleApiAvailability.showErrorNotification(ma, status);
        }
    }

    public void loadNotice(){
        try{
            TextView tv = (TextView)((Activity)context).findViewById(R.id.txtDashboardNotice);
            tv.setText(((MainActivity)context).mFBRemoteConfig.getConfigValue("notice_dashboard").replace("<br>", "\n"));
        }catch (Exception e){

        }
    }

    public void checkVersion(String marketVersion_code, String marketVersion_name){
        try {
            TextView tv_version_current = (TextView) ((Activity)context).findViewById(R.id.txtDashboardVersionCurrent);
            TextView tv_version_new = (TextView) ((Activity)context).findViewById(R.id.txtDashboardVersionNew);
            tv_version_current.setText("현재버전: " + BuildConfig.VERSION_NAME);
            tv_version_new.setText("최신버전: " + marketVersion_name);
            tv_version_new.setTextColor(Color.parseColor("#00FF00"));

            if (marketVersion_code.indexOf("-1") == -1 & BuildConfig.VERSION_CODE < Integer.parseInt(marketVersion_code)
                    & ((MainActivity)context).noticeUpdate == 0) {
                ((MainActivity)context).noticeUpdate++;
                tv_version_new.setTextColor(Color.parseColor("#FF0000"));
                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(context);
                dlgAlert.setTitle("업데이트를 확인하세요!");
                dlgAlert.setMessage("구글 마켓스토어에 최신버전의 앱이 존재합니다! 업데이트를 하지 않고 사용할 경우 오류가 발생할 가능성이 있으니 꼭 업데이트를 진행해 주시기 바랍니다!");
                dlgAlert.setPositiveButton("나중에보기",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //dismiss the dialog
                            }
                        });
                dlgAlert.setNeutralButton("즉시업데이트",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                final String appPackageName = context.getPackageName(); // getPackageName() from Context or Activity object
                                try {
                                    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                                } catch (android.content.ActivityNotFoundException anfe) {
                                    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                                }
                            }
                        });
                dlgAlert.setCancelable(true);
                dlgAlert.create().show();
            }
        }catch (Exception e){

        }

    }

    public void agreementWork(){
        try{
            SharedPreferences pref = context.getSharedPreferences("SMSASSISTANCE", MODE_PRIVATE);
            String pp = pref.getString("privacy_policy", "");

            if(pp.isEmpty() == true) {
                LayoutInflater inflater = LayoutInflater.from((MainActivity) context);
                View view = inflater.inflate(R.layout.content_scrollview, null);
                TextView textview=(TextView)view.findViewById(R.id.textmsg);
                textview.setText(getConfigValue("privacy_policy").replace("<br>", "\n"));

                AlertDialog.Builder dlgAlert = new AlertDialog.Builder((MainActivity) context);
                dlgAlert.setCancelable(false);
                dlgAlert.setTitle("개인정보보호정책 동의서");
                //dlgAlert.setMessage("구글 마켓스토어에 최신버전의 앱이 존재합니다! 업데이트를 하지 않고 사용할 경우 오류가 발생할 가능성이 있으니 꼭 업데이트를 진행해 주시기 바랍니다!");
                dlgAlert.setView(view);
                dlgAlert.setPositiveButton("동의",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                SharedPreferences pref = context.getSharedPreferences("SMSASSISTANCE", MODE_PRIVATE);
                                SharedPreferences.Editor editor = pref.edit();
                                editor.putString("privacy_policy", "agree");
                                editor.commit();

                                new Thread() {
                                    public void run() {
                                        sendLog(context.getPackageName() + " Android Privacy Policy Agreed");

                                        Bundle bun = new Bundle();
                                        bun.putString("data", "");
                                        Message msg = handler.obtainMessage();
                                        msg.setData(bun);
                                        handler.sendMessage(msg);
                                    }
                                }.start();
                            }
                        });
                dlgAlert.setNegativeButton("거부",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                ((MainActivity) context).finish();
                            }
                        });
                //dlgAlert.create().show();
                Dialog dlg = dlgAlert.create();
                dlg.setCanceledOnTouchOutside(false);
                dlg.show();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    // Server에 생성된 토큰을 등록하기 위해, 생성후 바로 보낼 때 활용하는 메소드
    public void sendRegistrationToServer() {
        // TODO: Implement this method to send token to your app server.
        //Log.d(TAG, "new token: " + token);
        try {
            // 현재 시간을 msec으로 구한다.
            long now = System.currentTimeMillis();
            // 현재 시간을 저장 한다.
            Date date = new Date(now);
            // 시간 포맷으로 만든다.
            SimpleDateFormat sdfNow = new SimpleDateFormat("yyyyMMdd");
            String strNow = sdfNow.format(date);

            SharedPreferences pref = context.getSharedPreferences("SMSASSISTANCE", MODE_PRIVATE);
            String tokeno = pref.getString("token", "");
            String token = FirebaseInstanceId.getInstance().getToken();

            if(tokeno.equals(strNow) == true)
                return;

            // HttpURLConnection 을 사용하여 보내는 방법
            HttpURLConnection connection;

            URL url = new URL("https://" + getConfigValue("server_host") + getConfigValue("url_instance_register"));
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true); //서버에 데이터 보낼 때, Post의 경우 꼭 사용
            connection.setDoInput(true); //서버에서 데이터 가져올 때
            connection.setRequestMethod("POST"); // POST방식을

            UniqueID uid = new UniqueID(context);
            String id = uid.getUniqueID();

            StringBuffer buffer = new StringBuffer();
            buffer = buffer.append("phone").append("=").append(id.replace("+",""));
            buffer = buffer.append("&");
            buffer = buffer.append("app").append("=").append(context.getPackageName());
            buffer = buffer.append("&");
            buffer = buffer.append("platform").append("=").append("Android");
            buffer = buffer.append("&");
            buffer = buffer.append("token").append("=").append(token);
            buffer = buffer.append("&");
            buffer = buffer.append("data").append("=");
            OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
            wr.write(buffer.toString());
            wr.flush(); // 서버에 작성
            wr.close(); // 객체를 닫음

            // 서버에서 값을 받아오지 않더라도 작성해야함
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuffer sb = new StringBuffer();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            connection.disconnect();

            SharedPreferences.Editor editor = pref.edit();
            editor.putString("token", strNow);
            editor.commit();

        }catch (Exception e) {
            e.printStackTrace();
        }

    }

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            Bundle bun = msg.getData();
            String marketVersion = bun.getString("data");

        }
    };

    public void sendLog(String data) {
        // TODO: Implement this method to send token to your app server.
        //Log.d(TAG, "new token: " + token);
        try {
            // HttpURLConnection 을 사용하여 보내는 방법
            HttpURLConnection connection;

            URL url = new URL("https://" + getConfigValue("server_host") + getConfigValue("url_log_submit"));
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true); //서버에 데이터 보낼 때, Post의 경우 꼭 사용
            connection.setDoInput(true); //서버에서 데이터 가져올 때
            connection.setRequestMethod("POST"); // POST방식을

            UniqueID uid = new UniqueID(context);
            String id = uid.getUniqueID();

            StringBuffer buffer = new StringBuffer();
            buffer = buffer.append("uid").append("=").append(id.replace("+",""));
            buffer = buffer.append("&");
            buffer = buffer.append("data").append("=").append(data);
            OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
            wr.write(buffer.toString());
            wr.flush(); // 서버에 작성
            wr.close(); // 객체를 닫음

            // 서버에서 값을 받아오지 않더라도 작성해야함
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuffer sb = new StringBuffer();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            connection.disconnect();

        }catch (Exception e) {
            e.printStackTrace();
        }

    }
}