package com.sonali.kidtrac;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.shashank.sony.fancytoastlib.FancyToast;
import com.sonali.kidtrac.beans.LocationDetailsBean;
import com.sonali.kidtrac.beans.ScheduleDetailsBean;
import com.sonali.kidtrac.databinding.ActivityAddScheduleBinding;

import java.util.HashMap;

public class AddScheduleActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityAddScheduleBinding binding;
    Marker marker;

    EditText fromET, toET;
    Button save_btn;
    LatLng latLng;
    String childId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAddScheduleBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.maps_fragment_container);
        mapFragment.getMapAsync(this);
        fromET = findViewById(R.id.from_time_et);
        toET = findViewById(R.id.to_time_et);

        childId = getIntent().getStringExtra("child_id");
        fromET.setOnClickListener(view -> new TimePickerDialog(this, (timePicker, hour, minute) -> {
            fromET.setText(hour+":"+minute);
        },0,0,true).show());
        toET.setOnClickListener(view -> new TimePickerDialog(this, (timePicker, hour, minute) -> {
            toET.setText(hour+":"+minute);
        },0,0,true).show());
        save_btn = findViewById(R.id.save_schedule_btn);
        save_btn.setOnClickListener(view -> {
            if(fromET.getText().toString().contentEquals("")
                    ||toET.getText().toString().contentEquals("")){
                FancyToast.makeText(this,"Add timings",FancyToast.LENGTH_SHORT,FancyToast.WARNING,false).show();
            } else if (latLng==null) {
                FancyToast.makeText(this,"Select A Location",FancyToast.LENGTH_SHORT,FancyToast.WARNING,false).show();
            } else {
                ScheduleDetailsBean sdb = new ScheduleDetailsBean(fromET.getText().toString(),
                        toET.getText().toString(),
                        latLng.latitude,latLng.longitude);
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("0");
                HashMap<String, Object> schedule = new HashMap<>();
                schedule.put("fromTime",sdb.getFromTime());
                schedule.put("toTime",sdb.getToTime());
                schedule.put("lat",sdb.getLat());
                schedule.put("lon",sdb.getLon());
                reference.child(childId).child("schedule").push().setValue(schedule).addOnSuccessListener(unused -> {
                    getOnBackPressedDispatcher().onBackPressed();
                    finish();
                });
            }
        });
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        LatLng lng = new LatLng(20.940920,84.803467);
        Marker temp = mMap.addMarker(new MarkerOptions().position(lng));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(8f));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(lng));
        mMap.setOnMapClickListener(latLng -> {
            this.latLng = latLng;
            if(temp.isVisible())
                temp.remove();
            if(marker!=null)
                marker.remove();
            MarkerOptions markerOptions = new MarkerOptions().position(latLng);
            marker = mMap.addMarker(markerOptions);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        });
    }
}