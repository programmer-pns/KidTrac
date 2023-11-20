package com.sonali.kidtrac;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LogInScreen extends AppCompatActivity {
    TextInputEditText nameET, emailET;
    TextInputLayout nameLayout, emailLayout;
    Button btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in_screen);

        nameET = findViewById(R.id.name_et);
        emailET = findViewById(R.id.email_et);
        nameLayout = findViewById(R.id.name_layout);
        emailLayout = findViewById(R.id.email_layout);
        btn = findViewById(R.id.get_otp_btn);
        btn.setOnClickListener(view->{
            if(nameET.getText().toString().contentEquals(""))
                nameLayout.setError("Name cannot be empty");
            else if (emailET.getText().toString().contentEquals(""))
                emailLayout.setError("Email cannot be empty");
            else if (!emailET.getText().toString().matches("^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$"))
                emailLayout.setError("Not a valid email");
            else{
                //store data to shared extras and move to next screen
                Intent intent = new Intent(this, ContinueScreen.class);
                intent.putExtra("user_name",nameET.getText().toString());
                intent.putExtra("user_email",emailET.getText().toString());
                startActivity(intent);
                finish();
            }
        });
    }
}