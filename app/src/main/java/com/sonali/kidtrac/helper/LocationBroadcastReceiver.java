package com.sonali.kidtrac.helper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.location.LocationResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.sonali.kidtrac.beans.LocationDetailsBean;
import com.sonali.kidtrac.beans.ScheduleDetailsBean;

import org.json.JSONObject;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LocationBroadcastReceiver extends BroadcastReceiver {
    public static final String ACTION = "com.sonali.kidtrac.UPDATE_LOCATION";
    private static final double EARTH_RADIUS = 6371000; // Earth radius in meters
    DatabaseReference reference;
    Intent intent;
    Context context;
    LocationDetailsBean ldb;
    public LocationBroadcastReceiver(){
        if(reference == null){
            reference = FirebaseDatabase.getInstance().getReference("0");
        }
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        this.intent = intent;
        this.context = context;
        if(intent!=null) {
            final String action = intent.getAction();
            if (action.equals(ACTION)) {
                LocationResult result = LocationResult.extractResult(intent);
                if(result!=null){
                    SharedPreferences sharedPreferences = context.getSharedPreferences("application", context.MODE_PRIVATE);
                    String user_id=sharedPreferences.getString("user_id", "");
                    String lat = Double.toString(result.getLastLocation().getLatitude());
                    String lon = Double.toString(result.getLastLocation().getLongitude());
                    ldb = new LocationDetailsBean(lat, lon, System.currentTimeMillis());
                    HashMap<String, Object> update = new HashMap<>();
                    update.put("location", ldb);
                    findSchedules();
                    reference.child(user_id).updateChildren(update);
                }
            }
        }
    }
    private boolean checkChildInBoundOrNot(double referenceLat, double referenceLon, double lat, double lon) {
        double distance = calculateDistance(referenceLat, referenceLon, lat, lon);
        if (distance <= Helper.RADIUS) return true;
        else return false;
    }

    private void findSchedules() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("application",Context.MODE_PRIVATE);
        Set<String> scheduleKeys = sharedPreferences.getStringSet("schedule_keys",new HashSet<>());
        Gson gson = new Gson();
        LocalDateTime ldt = LocalDateTime.now();
        int currHour = ldt.getHour();
        int currMin = ldt.getMinute();
        for(String key:scheduleKeys){
            ScheduleDetailsBean sdb = gson.fromJson(sharedPreferences.getString(key,""),ScheduleDetailsBean.class);
            String fromTime = sdb.getFromTime();
            String toTime = sdb.getToTime();
            String currentTime = currHour+":"+currMin;

            if(isBetween(fromTime,currentTime,toTime)){
                //checkLocation
                if(ldb!=null) {
                    if(!checkChildInBoundOrNot(
                            sdb.getLat(), sdb.getLon(),
                            Double.valueOf(ldb.getLatitude()),
                            Double.valueOf(ldb.getLongitude()))){
                        //send Message to parent
                        String userID = sharedPreferences.getString("user_id","");
                        reference.child(userID).child("parents")
                                .get().addOnSuccessListener(dataSnapshot -> {
                                    if(dataSnapshot.exists()){
                                        ArrayList<String> parentsList = new ArrayList<>();
                                        dataSnapshot.getChildren().forEach(s->parentsList.add(s.getValue(String.class)));
                                        sendNotificationToParents(parentsList,userID);
                                    }
                                });
                    }
                }
                break;
            }
        }
    }

    private void sendNotificationToParents(ArrayList<String> parentsList, String childID) {
        String userName = context.getSharedPreferences("application",Context.MODE_PRIVATE).getString("user_name","Your child");
        String message = userName+" is not at the scheduled place. Tap to view Location";
        //find the FCM tokens of parents and send notification
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("1");
        parentsList.forEach(s-> databaseReference.child(s).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                try {
                    JSONObject mainObject = new JSONObject();

                    JSONObject notificationObject = new JSONObject();
                    notificationObject.put("title", userName);
                    notificationObject.put("body", message);

                    JSONObject dataObject = new JSONObject();
                    dataObject.put("child_id", childID);

                    mainObject.put("notification",notificationObject);
                    mainObject.put("data",dataObject);
                    mainObject.put("to",task.getResult().child("fcmToken").getValue().toString());
                    callApi(mainObject);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }));
    }
    private void callApi(JSONObject jsonObject){
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        String url = "https://fcm.googleapis.com/fcm/send";
        RequestBody requestBody = RequestBody.create(mediaType,jsonObject.toString());
        Request request = new Request.Builder()
                .url(url).post(requestBody)
                .addHeader("Authorization","Bearer AAAAegHcON4:APA91bEyGOVsTeYOEOZJQ1ErYEoMzMY4VsVofmutd8j7qsrKan3MExeuz9csB-jN6zQAQmXtk2FNxSO_eocGM3div1U9V5jM612bmqdQwGwTgFv5X6Y-FHRMbb8CfS1qSnlbpnYKmKeG")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("MESSAGE","FAILURE");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful())
                    Log.e("SUCCESSFUL","SUCCESSFUL");
                else
                    Log.e("MESSAGE","FAILURE2");
            }
        });
    }

    public static boolean isBetween(String time1, String time2, String time3) {
        int t1 = convertToMinutes(time1);
        int t2 = convertToMinutes(time2);
        int t3 = convertToMinutes(time3);

        return (t1 <= t2 && t2 <= t3) || (t3 <= t2 && t2 <= t1);
    }
    private static int convertToMinutes(String time) {
        String[] parts = time.split(":");
        int hours = Integer.parseInt(parts[0]);
        int minutes = Integer.parseInt(parts[1]);
        return hours * 60 + minutes;
    }
    public static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS * c;
    }
}
