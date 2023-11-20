package com.sonali.kidtrac;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class IndivisualChildManagerScreen extends AppCompatActivity {

    Button viewLocationBtn;
    TextView addScheduleBtn;
    String childId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_indivisual_child_manager_screen);
        viewLocationBtn = findViewById(R.id.view_location_btn);
        addScheduleBtn = findViewById(R.id.add_schedule_btn);
        childId = getIntent().getStringExtra("child_id");
        viewLocationBtn.setOnClickListener(view -> {
            Intent intent = new Intent(this, MapViewScreen.class);
            intent.putExtra("child_id",childId);
            startActivity(intent);
        });
        addScheduleBtn.setOnClickListener(view -> {
            Intent intent = new Intent(this, AddScheduleActivity.class);
            intent.putExtra("child_id",childId);
            startActivity(intent);
        });
    }
}