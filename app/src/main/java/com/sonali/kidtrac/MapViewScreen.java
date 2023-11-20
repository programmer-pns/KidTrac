package com.sonali.kidtrac;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sonali.kidtrac.beans.LocationDetailsBean;

import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MapViewScreen extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap map;
    String parentId;
    String childId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_view_screen);
        childId = getIntent().getStringExtra("child_id");
        parentId = getSharedPreferences("application", Context.MODE_PRIVATE).getString("user_id","");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.maps_fragment_container);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("0");
        reference.child(childId).child("location").get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                if(task.getResult().exists()){
                    DataSnapshot snap = task.getResult();
                    LocationDetailsBean ldb = snap.getValue(LocationDetailsBean.class);
                    Log.e("TAG",ldb.getLatitude()+"");
                    LatLng location = new LatLng(Double.valueOf(ldb.getLatitude()),Double.valueOf(ldb.getLongitude()));
                    map.addMarker(new MarkerOptions().position(location).title("Updated at: "+new Date(ldb.getTime())));
                    map.moveCamera(CameraUpdateFactory.newLatLng(location));
                    map.moveCamera(CameraUpdateFactory.zoomTo(12f));
                }
            }
        });
    }
}