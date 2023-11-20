package com.sonali.kidtrac;

import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class Holder extends RecyclerView.ViewHolder {
    TextView nameTextView, idTextView;
    RelativeLayout relativeLayout;
    public Holder(@NonNull View itemView) {
        super(itemView);
        nameTextView = itemView.findViewById(R.id.child_name_tv);
        idTextView = itemView.findViewById(R.id.child_id_tv);
        relativeLayout = itemView.findViewById(R.id.main_component_relative_layout);
    }
}
