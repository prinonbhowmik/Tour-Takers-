package com.example.tureguideversion1.Notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.tureguideversion1.Activities.CommentsBox;
import com.example.tureguideversion1.Activities.MainActivity;
import com.example.tureguideversion1.Activities.ReplyBox;
import com.example.tureguideversion1.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.ArrayList;
import java.util.HashMap;

public class FirebaseMessaging extends FirebaseMessagingService {
public static final String TAG = "FirebaseMessaging";
    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        //Log.d(TAG, "onNewToken: updated token "+s);
        SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
        editor.putString("newToken", s);
        editor.apply();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null){
            updateToken(s);
            Toast.makeText(getApplicationContext(),"Your token updated",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        //Log.d(TAG, "Message data payload: " + remoteMessage.getData());

        PowerManager pm = (PowerManager)getApplicationContext().getSystemService(Context.POWER_SERVICE);
        boolean isScreenOn = pm.isInteractive();
        //Log.e(TAG, "isScreenOn "+isScreenOn);
        if(!isScreenOn) {
            //Log.d(TAG, "wakelock: access");
            PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK |PowerManager.ACQUIRE_CAUSES_WAKEUP |PowerManager.ON_AFTER_RELEASE,"TourTakers: wakeLock");
            wl.acquire(10000);
            PowerManager.WakeLock wl_cpu = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"TourTakers:CpuLock");
            wl_cpu.acquire(10000);
        }


        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }
        //Log.d(TAG, "onMessageReceived: called");
        String sented = remoteMessage.getData().get("sented");
        //Log.d(TAG, "onMessageReceived: sented = "+sented);
        String user = remoteMessage.getData().get("userID");
        //Log.d(TAG, "onMessageReceived: userID = "+user);
        SharedPreferences preferences = getSharedPreferences("PREFS", MODE_PRIVATE);
        String currentUser = preferences.getString("currentuser", "none");
        //Log.d(TAG, "onMessageReceived: currentUser = "+currentUser);
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser != null && sented.equals(firebaseUser.getUid())){
            if (!currentUser.equals(user) && currentUser.equals("none")) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    sendOreoNotification(remoteMessage);
                    //Log.d(TAG, "onMessageReceived: send oreo");
                } else {
                    sendNotification(remoteMessage);
                    //Log.d(TAG, "onMessageReceived: send normal");
                }
            }
        }
    }

    private void updateToken(String refreshToken) {
        ArrayList<String> eventIDs = new ArrayList<>();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference userActivity = FirebaseDatabase.getInstance().getReference().child("userActivities").child(user.getUid()).child("events");
        userActivity.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        HashMap<String,Object> data = (HashMap<String, Object>) childSnapshot.getValue();
                        eventIDs.add((String) data.get("eventID"));
                    }
                    for (int i = 0; i < eventIDs.size(); i++) {
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("eventCommentsTokens");
                        Token token1 = new Token(refreshToken);
                        ref.child(eventIDs.get(i)).child(user.getUid()).setValue(token1);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendOreoNotification(RemoteMessage remoteMessage){
        String userID = remoteMessage.getData().get("userID");
        String icon = remoteMessage.getData().get("icon");
        String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("body");
        String eventID = remoteMessage.getData().get("eventID");
        String from = remoteMessage.getData().get("fromActivity");

        DatabaseReference eventRef = FirebaseDatabase.getInstance().getReference().child("event").child(eventID);
        eventRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    HashMap<String,Object> hashMap = (HashMap<String, Object>) dataSnapshot.getValue();
                    String eventPlace = (String) hashMap.get("place");
                    RemoteMessage.Notification notification = remoteMessage.getNotification();
                    int j = Integer.parseInt(userID.replaceAll("[\\D]", ""));
                    if(from.matches("CommentBox")) {
                        Intent intent = new Intent(getApplicationContext(), CommentsBox.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("eventId", eventID);
                        intent.putExtras(bundle);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), j, intent, PendingIntent.FLAG_ONE_SHOT);
                        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

                        OreoNotification oreoNotification = new OreoNotification(getApplicationContext());
                        Notification.Builder builder = oreoNotification.getOreoNotification(title + ": " + eventPlace, body, pendingIntent,
                                defaultSound, icon);

                        int i = 0;
                        if (j > 0) {
                            i = j;
                        }

                        oreoNotification.getManager().notify(i, builder.build());
                    }else if(from.matches("ReplyBox")) {
                        Intent intent = new Intent(getApplicationContext(), ReplyBox.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("eventId", eventID);
                        bundle.putString("commentId",remoteMessage.getData().get("commentID"));
                        intent.putExtras(bundle);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), j, intent, PendingIntent.FLAG_ONE_SHOT);
                        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

                        OreoNotification oreoNotification = new OreoNotification(getApplicationContext());
                        Notification.Builder builder = oreoNotification.getOreoNotification(title + ": " + eventPlace, body, pendingIntent,
                                defaultSound, icon);

                        int i = 0;
                        if (j > 0) {
                            i = j;
                        }

                        oreoNotification.getManager().notify(i, builder.build());
                    }
                }else {
                    RemoteMessage.Notification notification = remoteMessage.getNotification();
                    int j = Integer.parseInt(userID.replaceAll("[\\D]", ""));
                    if(from.matches("CommentBox")) {
                        Intent intent = new Intent(getApplicationContext(), CommentsBox.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("eventId", eventID);
                        intent.putExtras(bundle);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), j, intent, PendingIntent.FLAG_ONE_SHOT);
                        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

                        OreoNotification oreoNotification = new OreoNotification(getApplicationContext());
                        Notification.Builder builder = oreoNotification.getOreoNotification(title, body, pendingIntent,
                                defaultSound, icon);

                        int i = 0;
                        if (j > 0) {
                            i = j;
                        }

                        oreoNotification.getManager().notify(i, builder.build());
                    }else if(from.matches("ReplyBox")) {
                        Intent intent = new Intent(getApplicationContext(), ReplyBox.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("eventId", eventID);
                        bundle.putString("commentId",remoteMessage.getData().get("commentID"));
                        intent.putExtras(bundle);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), j, intent, PendingIntent.FLAG_ONE_SHOT);
                        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

                        OreoNotification oreoNotification = new OreoNotification(getApplicationContext());
                        Notification.Builder builder = oreoNotification.getOreoNotification(title, body, pendingIntent,
                                defaultSound, icon);

                        int i = 0;
                        if (j > 0) {
                            i = j;
                        }

                        oreoNotification.getManager().notify(i, builder.build());
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void sendNotification(RemoteMessage remoteMessage) {

        String eventID = remoteMessage.getData().get("eventID");
        String icon = remoteMessage.getData().get("icon");
        String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("body");
        String userID = remoteMessage.getData().get("userID");
        String from = remoteMessage.getData().get("fromActivity");

        //Log.d(TAG, "sendNotification: userID = "+userID);
        DatabaseReference eventRef = FirebaseDatabase.getInstance().getReference().child("event").child(eventID);
        eventRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    HashMap<String,Object> hashMap = (HashMap<String, Object>) dataSnapshot.getValue();
                    String eventPlace = (String) hashMap.get("place");
                    RemoteMessage.Notification notification = remoteMessage.getNotification();
                    int j = Integer.parseInt(userID.replaceAll("[\\D]", ""));
                    if(from.matches("CommentBox")) {
                        Intent intent = new Intent(getApplicationContext(), CommentsBox.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("eventId", eventID);
                        intent.putExtras(bundle);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), j, intent, PendingIntent.FLAG_ONE_SHOT);
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(),"Events")
                                .setSmallIcon(Integer.parseInt(icon))
                                .setContentTitle(title+": "+eventPlace)
                                .setContentText(body)
                                .setPriority(NotificationCompat.PRIORITY_HIGH)
                                .setAutoCancel(true)
                                .setSound(Uri.parse("android.resource://" + getApplicationContext().getPackageName() + "/" + R.raw.swiftly))
                                .setContentIntent(pendingIntent);
                        NotificationManager noti = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

                        int i = 0;
                        if (j > 0){
                            i = j;
                        }

                        noti.notify(i, builder.build());
                    }else if(from.matches("ReplyBox")){
                        Intent intent = new Intent(getApplicationContext(), ReplyBox.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("eventId", eventID);
                        bundle.putString("commentId",remoteMessage.getData().get("commentID"));
                        intent.putExtras(bundle);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), j, intent, PendingIntent.FLAG_ONE_SHOT);
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(),"Events")
                                .setSmallIcon(Integer.parseInt(icon))
                                .setContentTitle(title+": "+eventPlace)
                                .setContentText(body)
                                .setPriority(NotificationCompat.PRIORITY_HIGH)
                                .setAutoCancel(true)
                                .setSound(Uri.parse("android.resource://" + getApplicationContext().getPackageName() + "/" + R.raw.swiftly))
                                .setContentIntent(pendingIntent);
                        NotificationManager noti = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

                        int i = 0;
                        if (j > 0){
                            i = j;
                        }

                        noti.notify(i, builder.build());
                    }
                }else {
                    RemoteMessage.Notification notification = remoteMessage.getNotification();
                    int j = Integer.parseInt(userID.replaceAll("[\\D]", ""));
                    if(from.matches("CommentBox")) {
                        Intent intent = new Intent(getApplicationContext(), CommentsBox.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("eventId", eventID);
                        intent.putExtras(bundle);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), j, intent, PendingIntent.FLAG_ONE_SHOT);
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(),"Events")
                                .setSmallIcon(Integer.parseInt(icon))
                                .setContentTitle(title)
                                .setContentText(body)
                                .setPriority(NotificationCompat.PRIORITY_HIGH)
                                .setAutoCancel(true)
                                .setSound(Uri.parse("android.resource://" + getApplicationContext().getPackageName() + "/" + R.raw.swiftly))
                                .setContentIntent(pendingIntent);
                        NotificationManager noti = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                        int i = 0;
                        if (j > 0){
                            i = j;
                        }

                        noti.notify(i, builder.build());
                    }else if(from.matches("ReplyBox")){
                        Intent intent = new Intent(getApplicationContext(), ReplyBox.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("eventId", eventID);
                        bundle.putString("commentId",remoteMessage.getData().get("commentID"));
                        intent.putExtras(bundle);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), j, intent, PendingIntent.FLAG_ONE_SHOT);
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(),"Events")
                                .setSmallIcon(Integer.parseInt(icon))
                                .setContentTitle(title)
                                .setContentText(body)
                                .setPriority(NotificationCompat.PRIORITY_HIGH)
                                .setAutoCancel(true)
                                .setSound(Uri.parse("android.resource://" + getApplicationContext().getPackageName() + "/" + R.raw.swiftly))
                                .setContentIntent(pendingIntent);
                        NotificationManager noti = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                        int i = 0;
                        if (j > 0){
                            i = j;
                        }

                        noti.notify(i, builder.build());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
