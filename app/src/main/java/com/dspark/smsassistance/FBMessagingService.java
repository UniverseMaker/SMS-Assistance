package com.dspark.smsassistance;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsManager;
import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class FBMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
    private static final String TAG = FBMessagingService.class.getSimpleName();

    // 메시지 수신
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.i(TAG, "onMessageReceived");

        try {
            Map<String, String> data = remoteMessage.getData();
            String title = data.get("title");
            String content = data.get("content");
            String argent = data.get("argent");
            String sound = "";
            if (data.containsKey("sound") == true)
                sound = data.get("sound");
            //String body = remoteMessage.getNotification().getBody();

            //sendNotification(title, content, sound);
            if(!argent.equals("true")) {
                processData(title, content);
                sendNotification("SMS Assistance", "데이터 바이패스 요청이 도착했습니다", sound);
            }
            else{
                argentData(title, content);
            }
        }catch (Exception e){

        }
    }

    private void argentData(String title, String contents){
        SmsManager smsManager = SmsManager.getDefault();
        if(contents.length() > 70)
            contents = contents.substring(0, 70);
        smsManager.sendTextMessage(title, null, contents, null, null);
    }

    private void processData(String title, String contents){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentDateandTime = sdf.format(new Date());
        DBHelper dbHelper = new DBHelper(this, "SMSASSISTANCE", null , 1);
        dbHelper.insertRecord(title, contents, currentDateandTime);
    }

    private void sendNotification(String title, String message, String sound) {
        try {
            Log.i("smsassistance", "sendNotification");
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                    PendingIntent.FLAG_CANCEL_CURRENT);

            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                    //.setLargeIcon(BitmapFactory.decodeResource(getResources(), android.R.drawable.ic_dialog_info))
                    .setSmallIcon(R.mipmap.ic_mail1)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setStyle(new NotificationCompat.BigTextStyle().setBigContentTitle(title).bigText(message));

            //SharedPreferences prefs = getSharedPreferences("MINISLOTTERY", MODE_PRIVATE);
            //String result = prefs.getString("SET_1", "0"); //키값, 디폴트값

            if (sound.isEmpty() == false)
                notificationBuilder.setSound(defaultSoundUri).setDefaults(Notification.DEFAULT_VIBRATE);

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                String channelID = "notify_001";
                String channelName = "ChannelName";
                NotificationChannel channel = new NotificationChannel(channelID,
                        channelName,
                        NotificationManager.IMPORTANCE_DEFAULT);
                notificationManager.createNotificationChannel(channel);
                notificationBuilder.setChannelId(channelID);
            }

            Log.i("lottery", "sendNotification-notify");
            notificationManager.notify((int) System.currentTimeMillis() /* ID of notification */, notificationBuilder.build());
        }catch (Exception e){

        }
    }
}