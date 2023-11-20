package com.sonali.kidtrac;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.SharedMemory;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.sonali.kidtrac.helper.ParentService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class ParentHomePage extends AppCompatActivity {

    TextView addChildBtn, wishTextView ;
    LottieAnimationView animationView;
    SharedPreferences sharedPreferences;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_home_page);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        addChildBtn = findViewById(R.id.add_child_btn);
        addChildBtn.setOnClickListener(view -> startActivity(new Intent(this,AddChildrenScreen.class)));

        animationView = findViewById(R.id.lottie_animation);
        sharedPreferences = getSharedPreferences("application", Context.MODE_PRIVATE);
        String userName = sharedPreferences.getString("user_name","USER");
        wishTextView = findViewById(R.id.wish_text_view);
        wishTextView.setText(getWishMessage()+userName+" !");
        userID = sharedPreferences.getString("user_id","");
        loadRecyclerView();
        getAllPermissions();
        getFCMToken();
    }
    public void getAllPermissions(){
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
        }catch (Exception e){

        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        loadRecyclerView();
    }

    private void loadRecyclerView() {
        List<ChildrenDetailsBean> list = new ArrayList<>();
        Set<String> childrenIdKeySet = sharedPreferences.getStringSet("children_id_key_set",new LinkedHashSet<>());

        childrenIdKeySet.forEach(s->{
            ChildrenDetailsBean bean = new ChildrenDetailsBean();
            bean.setChildId(s);
            bean.setChildName(sharedPreferences.getString(s+"_key",""));
            list.add(bean);
        });

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        MyAdapter myAdapter = new MyAdapter(this, list);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(myAdapter);
    }
    private String getWishMessage(){
        String message = "Good ";
        LocalDateTime dateTime = LocalDateTime.now();
        int hour = dateTime.getHour();
        if(hour>=6&&hour<12)
            message+="Morning,";
        else if (hour>=12&&hour<15)
            message+="Noon, ";
        else if (hour>=15&&hour<18)
            message+="AfterNoon, ";
        else if (hour>=18&&hour<22)
            message+="Evening, ";
        else
            message+="Night, ";
        return message;
    }
    private void getFCMToken(){
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(token->{
            Map<String, Object> fcmToken = new HashMap<>();
            fcmToken.put("fcmToken",token);
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("1");
            reference.child(userID).updateChildren(fcmToken);
            Log.e("TOKEN",token);
        });
    }
}