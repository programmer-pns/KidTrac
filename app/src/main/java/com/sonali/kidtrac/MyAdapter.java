package com.sonali.kidtrac;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<Holder> {
    Context context;
    List<ChildrenDetailsBean> childrenDetailsBeanList;
    public MyAdapter(Context context, List<ChildrenDetailsBean> list) {
        this.context = context;
        this.childrenDetailsBeanList = list;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(context).inflate(R.layout.child_profile_component,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.nameTextView.setText(childrenDetailsBeanList.get(position).getChildName());
        holder.idTextView.setText(childrenDetailsBeanList.get(position).getChildId());
        holder.relativeLayout.setOnClickListener(view -> {
            Intent intent = new Intent(context, IndivisualChildManagerScreen.class);
            intent.putExtra("child_id",childrenDetailsBeanList.get(position).getChildId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return childrenDetailsBeanList.size();
    }
}
