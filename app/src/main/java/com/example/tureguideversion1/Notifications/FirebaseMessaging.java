package com.example.tureguideversion1.Notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
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
import com.example.tureguideversion1.Activities.EventDetails;
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

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
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
        String sex = remoteMessage.getData().get("userSex");
        Bitmap bitmap = null;
        //Log.d(TAG, "sendOreoNotification: "+remoteMessage.getData().get("userImage"));
        if(remoteMessage.getData().get("userImage").trim().length() != 0) {
            bitmap = getCroppedBitmap(getBitmapFromURL(remoteMessage.getData().get("userImage")));
        }else {
            if(sex.equals("male")) {
                bitmap = getCroppedBitmap(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.man));
            }else if(sex.equals("female")){
                bitmap = getCroppedBitmap(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.woman));
            }
        }
        DatabaseReference eventRef = FirebaseDatabase.getInstance().getReference().child("event").child(eventID);
        Bitmap finalBitmap = bitmap;
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
                                defaultSound, icon, finalBitmap);

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
                                defaultSound, icon, finalBitmap);

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
                                defaultSound, icon, finalBitmap);

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
                                defaultSound, icon, finalBitmap);

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

    public Bitmap getBitmapFromURL(String strURL) {
        try {
            URL url = new URL(strURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Bitmap getCroppedBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        //Bitmap _bmp = Bitmap.createScaledBitmap(output, 60, 60, false);
        //return _bmp;
        return output;
    }

    private void sendNotification(RemoteMessage remoteMessage) {

        String eventID = remoteMessage.getData().get("eventID");
        String icon = remoteMessage.getData().get("icon");
        String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("body");
        String userID = remoteMessage.getData().get("userID");
        String from = remoteMessage.getData().get("fromActivity");
        String sex = remoteMessage.getData().get("userSex");
        Bitmap bitmap = null;
        if(remoteMessage.getData().get("userImage").trim().length() != 0) {
            bitmap = getCroppedBitmap(getBitmapFromURL(remoteMessage.getData().get("userImage")));
        }else {
            if(sex.equals("male")) {
                bitmap = getCroppedBitmap(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.man));
            }else if(sex.equals("female")){
                bitmap = getCroppedBitmap(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.woman));
            }
        }
        //Log.d(TAG, "sendNotification: userID = "+userID);
        DatabaseReference eventRef = FirebaseDatabase.getInstance().getReference().child("event").child(eventID);
        Bitmap finalBitmap = bitmap;
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
                                .setLargeIcon(finalBitmap)
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
                                .setLargeIcon(finalBitmap)
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
                    else if(from.matches("EventDetails")){
                        Intent jintent = new Intent(getApplicationContext(), EventDetails.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("userID",userID);
                        bundle.putString("eventId",eventID);
                        jintent.putExtras(bundle);
                        jintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), j, jintent, PendingIntent.FLAG_ONE_SHOT);
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(),"Events")
                                .setSmallIcon(Integer.parseInt(icon))
                                .setLargeIcon(finalBitmap)
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
                                .setLargeIcon(finalBitmap)
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
                                .setLargeIcon(finalBitmap)
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
