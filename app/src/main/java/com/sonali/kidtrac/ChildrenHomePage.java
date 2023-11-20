package com.sonali.kidtrac;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.messaging.FirebaseMessaging;
import com.sonali.kidtrac.helper.LocationService;

import java.time.LocalDateTime;

public class ChildrenHomePage extends AppCompatActivity {

    TextView wishTV, nameTV, idTV;
    Button button;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_children_home_page);

        wishTV = findViewById(R.id.child_wish_text_view);
        nameTV = findViewById(R.id.child_name_text_view);
        idTV = findViewById(R.id.child_id_text_view);
        wishTV.setText(getWishMessage());
        sharedPreferences = getSharedPreferences("application", MODE_PRIVATE);
        nameTV.setText(sharedPreferences.getString("user_name", "USER"));
        idTV.setText(sharedPreferences.getString("user_id", "ERROR: RE-INSTALL THE APP"));
        button = findViewById(R.id.btn_refresh);
        Intent intent = new Intent(ChildrenHomePage.this, LocationService.class);
        intent.putExtra("user_id", sharedPreferences.getString("user_id", ""));
        intent.putExtra("NotificationTitle", "KidTrac");
        intent.putExtra("NotificationSubtitle", "Updating Location");
        button.setOnClickListener(view -> startForegroundService(new Intent(this, LocationService.class)));
        button.callOnClick();
        getAllPermissions();
    }

    public void getAllPermissions() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                ActivityCompat.requestPermissions(this,
                        new String[]{
                                android.Manifest.permission.POST_NOTIFICATIONS,
                                android.Manifest.permission.ACCESS_FINE_LOCATION,
                                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                                android.Manifest.permission.READ_PHONE_STATE,
                                android.Manifest.permission.WAKE_LOCK
                        },
                        1001);
            }
        } catch (Exception e) {

        }
    }

    private String getWishMessage() {
        String message = "Good ";
        LocalDateTime dateTime = LocalDateTime.now();
        int hour = dateTime.getHour();
        if (hour >= 6 && hour < 12)
            message += "Morning!";
        else if (hour >= 12 && hour < 15)
            message += "Noon!";
        else if (hour >= 15 && hour < 18)
            message += "AfterNoon!";
        else if (hour >= 18 && hour < 22)
            message += "Evening!";
        else
            message += "Night! ";
        return message;
    }

}