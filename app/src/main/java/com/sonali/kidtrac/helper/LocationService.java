package com.sonali.kidtrac.helper;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
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


public class LocationService extends Service {
    public static String mNotificationTitle;
    public static String mNotificationSubTitle;
    private final String CHANNELID = "MyChannelId";
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest request;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        super.onStartCommand(intent, flags, startId);
        mNotificationTitle = "KidTrac by Sonali";
        mNotificationSubTitle = "Updating Location";

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
        startForeground(4507, notification.build());
    }

    @Override
    public void onCreate() {
        sharedPreferences = getSharedPreferences("application",MODE_PRIVATE);
        editor = sharedPreferences.edit();
        updateLocation();
    }
    private void updateLocation() {
        buildLocationRequest();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(request, getPendingIntent());
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("0");
        reference.child(sharedPreferences.getString("user_id","")).child("schedule")
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        ScheduleDetailsBean sdb = snapshot.getValue(ScheduleDetailsBean.class);
                        Gson gson = new Gson();
                        String str = gson.toJson(sdb);
                        editor.putString(sdb.getFromTime(),str);
                        Set<String> schedulesFrom = sharedPreferences.getStringSet("schedule_keys",new LinkedHashSet<>());
                        schedulesFrom.add(sdb.getFromTime());
                        editor.putStringSet("schedule_keys",schedulesFrom).apply();
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
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
    private PendingIntent getPendingIntent() {
        Intent intent = new Intent(this, LocationBroadcastReceiver.class);
        intent.setAction(LocationBroadcastReceiver.ACTION);
        return PendingIntent.getBroadcast(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void buildLocationRequest() {
        request = new LocationRequest();
        request.setSmallestDisplacement(10f);
        request.setFastestInterval(3000);
        request.setInterval(5000);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }
}