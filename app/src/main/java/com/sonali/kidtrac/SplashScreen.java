package com.sonali.kidtrac;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if(getIntent().getExtras()!=null) {
            String childID = getIntent().getStringExtra("child_id");
            if(childID!=null)
            {
                Intent childIntent = new Intent(this, IndivisualChildManagerScreen.class);
                childIntent.putExtra("child_id",childID);
                new Handler().postDelayed(()->{
                    startActivity(new Intent(this, ParentHomePage.class));
                    startActivity(childIntent);
                    finish();
                },2000);
            } else {
                tryAnotherPage();
            }
        } else {
            tryAnotherPage();
        }
    }

    private void tryAnotherPage() {
        SharedPreferences sharedPreferences = getSharedPreferences("application", MODE_PRIVATE);
        String userId = sharedPreferences.getString("user_id", "");
        int userType = sharedPreferences.getInt("user_type", 0);
        new Handler().postDelayed(() -> {
            if (userId.contentEquals(""))
                startActivity(new Intent(this, GetStartedScreen.class));
            else if (userType == 1)
                startActivity(new Intent(this, ParentHomePage.class));
            else
                startActivity(new Intent(this, ChildrenHomePage.class));
            finish();
        }, 2000);
    }

}