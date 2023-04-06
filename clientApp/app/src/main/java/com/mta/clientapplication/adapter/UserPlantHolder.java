package com.mta.clientapplication.adapter;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mta.clientapplication.R;

public class UserPlantHolder extends RecyclerView.ViewHolder {
    TextView nickName, plantName, date;

    public UserPlantHolder(@NonNull View itemView) {
        super(itemView);
        nickName = itemView.findViewById(R.id.userPlantListItem_nickname);
        plantName = itemView.findViewById(R.id.userPlantListItem_plantName);
        date = itemView.findViewById(R.id.userPlantListItem_creationDate);
    }
}
