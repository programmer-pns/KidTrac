package com.sonali.kidtrac;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class GetStartedScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_started_screen);
        Button btn = findViewById(R.id.get_started_btn);
        btn.setOnClickListener(view -> {
            startActivity(new Intent(this, LogInScreen.class));
            finish();
        });
    }
}