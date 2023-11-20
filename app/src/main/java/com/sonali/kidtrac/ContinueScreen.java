package com.sonali.kidtrac;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.shashank.sony.fancytoastlib.FancyToast;
import com.sonali.kidtrac.beans.LocationDetailsBean;
import com.sonali.kidtrac.beans.UserDetailsBeanObject;

import java.util.HashMap;

public class ContinueScreen extends AppCompatActivity {
    Button child_btn,parent_btn;
    SharedPreferences.Editor editor;
    String userName;
    String userEmail;
    Intent nextIntent;
    int userType;
    private FirebaseDatabase fdb;
    private DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_continue_screen);
        Intent intent = getIntent();
        userName = intent.getStringExtra("user_name");
        userEmail = intent.getStringExtra("user_email");
        child_btn = findViewById(R.id.cont_child_btn);
        parent_btn = findViewById(R.id.cont_parent_btn);
        editor = getSharedPreferences("application",MODE_PRIVATE).edit();

        fdb = FirebaseDatabase.getInstance();

        child_btn.setOnClickListener(view-> runOnClick(0));
        parent_btn.setOnClickListener(view-> runOnClick(1));
    }

    public void runOnClick(int userType) {
        this.userType = userType;
        nextIntent = new Intent(ContinueScreen.this, AddChildrenScreen.class);
        UserDetailsBeanObject udbo = new UserDetailsBeanObject(userName,userEmail);
        reference = fdb.getReference(userType+"");
        reference.child(udbo.getUserId()).setValue(udbo).addOnSuccessListener(unused -> {
            editor.putString("user_name", udbo.getUserName());
            editor.putString("user_email", udbo.getUserEmail());
            editor.putString("user_id", udbo.getUserId());
            editor.putInt("user_type", userType);
            editor.apply();
            editor.commit();
            startActivity(nextIntent);
        }).addOnFailureListener(e -> FancyToast.makeText(ContinueScreen.this,"Something Went Wrong", FancyToast.LENGTH_SHORT,FancyToast.ERROR,false).show());
        if(this.userType==0) {
            LocationDetailsBean ldb = new LocationDetailsBean("0","0",System.currentTimeMillis());
            nextIntent = new Intent(ContinueScreen.this, ChildrenHomePage.class);
            reference.child(udbo.getUserId()).child("location").setValue(ldb)
                    .addOnFailureListener(e -> FancyToast.makeText(ContinueScreen.this,"Cannot set Location Details", FancyToast.LENGTH_SHORT,FancyToast.ERROR,false).show());
            HashMap<String,Object> msg = new HashMap<>();
            msg.put("message","initial");
            reference.child(udbo.getUserId()).child("message").setValue(msg);
        }
    }
}