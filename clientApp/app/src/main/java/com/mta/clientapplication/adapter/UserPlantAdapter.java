package com.mta.clientapplication.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mta.clientapplication.R;
import com.mta.clientapplication.model.UserPlant;

import java.util.List;

public class UserPlantAdapter extends RecyclerView.Adapter<UserPlantHolder> {
    private List<UserPlant> userPlantList;

    public UserPlantAdapter(List<UserPlant> plantList) {
        this.userPlantList = plantList;
    }

    @NonNull
    @Override
    public UserPlantHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_userplant_item, parent, false);
        return new UserPlantHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserPlantHolder holder, int position) {
        UserPlant plant = userPlantList.get(position);
        holder.nickName.setText(plant.getNickName());
        holder.plantName.setText(plant.getPlantName());
        holder.date.setText(plant.getCreationDateStr());
    }

    @Override
    public int getItemCount() {
        return userPlantList.size();
    }
}
