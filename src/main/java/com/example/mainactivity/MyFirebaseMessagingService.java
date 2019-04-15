package com.example.mainactivity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import androidx.core.app.NotificationCompat;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "FirebaseMessagingServce";
    private static final String CHAT_NOTIFICATIONS="ChatMessage";
    private static final String CHANNEL_ID="Chats";
    private static int x=1;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        String notificationTitle = null, notificationBody = null;

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            notificationTitle = remoteMessage.getNotification().getTitle();
            notificationBody = remoteMessage.getNotification().getBody();
        }
        String nt1=notificationTitle.substring(0,notificationTitle.indexOf(" "));
        String nt2=notificationTitle.substring(notificationTitle.indexOf(" ")+1);
        String fa=FirebaseAuth.getInstance().getCurrentUser().getUid();
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
        //if(!nt2.equals(fa))
        sendNotification(nt1,notificationBody );
    }


    private void sendNotification(String notificationTitle, String notificationBody) {
        Intent intent = new Intent(this, FirestoreChatActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder groupBuilder =
                new NotificationCompat.Builder(this,CHANNEL_ID)
                        .setContentTitle("Bunk It!")
                        .setContentText("You have new messages")
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setGroupSummary(true)
                        .setGroup(CHAT_NOTIFICATIONS)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText("You have new messages"))
                        .setContentIntent(pendingIntent);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this,CHANNEL_ID)
                .setAutoCancel(true)   //Automatically delete the notification
                .setSmallIcon(R.mipmap.ic_launcher) //Notification icon
                .setContentIntent(pendingIntent)
                .setContentTitle(notificationTitle)
                .setContentText(notificationBody)
                .setGroup(CHAT_NOTIFICATIONS)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(notificationBody))
                .setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_SUMMARY)
                .setSound(defaultSoundUri)
                .setDefaults(NotificationCompat.DEFAULT_LIGHTS );


        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel mChannel;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mChannel = new NotificationChannel(CHANNEL_ID, "Chat123", NotificationManager.IMPORTANCE_HIGH);
            mChannel.setLightColor(Color.GRAY);
            mChannel.enableLights(true);
            mChannel.setDescription("Chat Forth!");
            AudioAttributes audioAttributes = new AudioAttributes.Builder()

                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();
            mChannel.setSound(defaultSoundUri, audioAttributes);

            if (notificationManager != null) {
                notificationManager.createNotificationChannel( mChannel );
            }
        }
        notificationManager.notify(0, groupBuilder.build());
        notificationManager.notify(x++, notificationBuilder.build());
    }
}