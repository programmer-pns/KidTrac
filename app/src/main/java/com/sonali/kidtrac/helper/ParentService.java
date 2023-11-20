package com.sonali.kidtrac.helper;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.sonali.kidtrac.R;
import com.sonali.kidtrac.beans.ScheduleDetailsBean;

import java.util.LinkedHashSet;
import java.util.Set;

public class ParentService extends Service {


    public static String mNotificationTitle;
    public static String mNotificationSubTitle;
    private String CHANNELID = "MyChannelId";
    int ID=4507;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        mNotificationTitle = "KidTrac by Sonali";
        mNotificationSubTitle = "Listening to Server";
        showNotification();
        return START_REDELIVER_INTENT;
    }

    private void showNotification() {
        NotificationChannel channel = new NotificationChannel(
                CHANNELID,
                CHANNELID,
                NotificationManager.IMPORTANCE_LOW
        );
        getSystemService(NotificationManager.class).createNotificationChannel(channel);
        Notification.Builder notification = new Notification.Builder(this, CHANNELID)
                .setContentTitle(mNotificationTitle)
                .setTicker(mNotificationTitle)
                .setContentText(mNotificationSubTitle)
                .setSmallIcon(R.mipmap.ic_launcher);
        startForeground(ID, notification.build());
    }

    @Override
    public void onCreate() {
        sharedPreferences = getSharedPreferences("application",MODE_PRIVATE);
        getNotificationFromServer();
    }
    private void getNotificationFromServer() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("0");
        reference.child(sharedPreferences.getString("user_id","")).child("message")
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        Log.e("CHILD LISTNER","ADDED");
                        CHANNELID = "ServerChannel";
                        ID = 4545;
                        mNotificationSubTitle = snapshot.getValue().toString().split("$")[0]+" is out of his scheduled place.";
                        showNotification();
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        Log.e("CHILD LISTNER","CHANGED");
                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}
