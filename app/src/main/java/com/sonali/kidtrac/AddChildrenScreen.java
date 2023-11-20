package com.sonali.kidtrac;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AddChildrenScreen extends AppCompatActivity {
    private TextInputEditText name_et,id_et;
    private TextInputLayout name_layout, id_layout;
    private Button addChildrenButton;
    private SharedPreferences sharedPreferences;
    private boolean isAdded;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_children_screen);
        id_et = findViewById(R.id.child_id_et);
        name_et = findViewById(R.id.child_name_et);

        id_layout = findViewById(R.id.child_id_layout);
        name_layout = findViewById(R.id.child_name_layout);

        sharedPreferences  = getSharedPreferences("application",MODE_PRIVATE);

        addChildrenButton = findViewById(R.id.btn_add_children);
        addChildrenButton.setOnClickListener(view->{
            if(id_et.getText().toString().contentEquals(""))
            {
                id_layout.setError("ID can not be empty");
            } else if (name_et.getText().toString().contentEquals("")) {
                name_layout.setError("Name can not be empty");
            } else if(id_et.getText().length()!=12) {
                id_layout.setError("Not a valid id");
            } else {
                Set<String> childrenIdKeySet = sharedPreferences.getStringSet("children_id_key_set",new LinkedHashSet<>());
                if(childrenIdKeySet.stream().filter(s -> s.contentEquals(sharedPreferences.getString("user_id","USER_ID"))).count()!=0){
                    FancyToast.makeText(AddChildrenScreen.this,"Child already exists",FancyToast.LENGTH_SHORT,FancyToast.WARNING,false).show();
                    return;
                }
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("0");
                reference.child(id_et.getText().toString()).get().addOnCompleteListener(result -> {
                    if(result!=null){
                        if (result.getResult().exists()){

                            FancyToast.makeText(AddChildrenScreen.this,"Children Added Successfully",FancyToast.LENGTH_SHORT,FancyToast.SUCCESS,false).show();

                            childrenIdKeySet.add(id_et.getText().toString());
                            Set<String> childSet = sharedPreferences.getStringSet("children_id_key_set",new LinkedHashSet<>());

                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putStringSet("children_id_key_set", childrenIdKeySet);
                            editor.putString(id_et.getText().toString()+"_key", name_et.getText().toString());
                            editor.apply();
                            editor.commit();
                            //add parent to child's list
                            reference.child(id_et.getText().toString()).child("parents").push().setValue(sharedPreferences.getString("user_id",null)).addOnSuccessListener(unused -> {
                                if(childSet.size()!=0)
                                    getOnBackPressedDispatcher().onBackPressed();
                                else
                                    startActivity(new Intent(AddChildrenScreen.this,ParentHomePage.class));
                                finish();
                            }).addOnFailureListener(unused -> FancyToast.makeText(AddChildrenScreen.this,"Something went wrong",FancyToast.LENGTH_SHORT,FancyToast.ERROR,false).show());
                        }else{
                            FancyToast.makeText(AddChildrenScreen.this,"No child found with this ID",FancyToast.LENGTH_SHORT,FancyToast.ERROR,false).show();
                        }
                    }
                });
            }
        });
    }
}