package com.mta.greenguardianapplication.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.mta.greenguardianapplication.R;
import com.mta.greenguardianapplication.model.UserPlant;

import java.util.List;

public class UserPlantAdapter extends FirebaseRecyclerAdapter<UserPlant,UserPlantAdapter.UserPlantHolder> {

    public UserPlantAdapter(@NonNull FirebaseRecyclerOptions<UserPlant> options) {
        super(options);
    }

    @NonNull
    @Override
    public UserPlantHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.single_item_plant_row,parent,false);
        return new UserPlantHolder(view);
    }


    @Override
    protected void onBindViewHolder(@NonNull UserPlantHolder holder, int position, @NonNull UserPlant model) {
        holder.nickName.setText(model.getNickName());
        holder.plantName.setText(model.getPlantName());
        holder.userID.setText(model.getUserId());
    }



    class UserPlantHolder extends RecyclerView.ViewHolder {
        TextView nickName, plantName, userID;

        public UserPlantHolder(@NonNull View itemView) {
            super(itemView);
            nickName = itemView.findViewById(R.id.form_textFieldNickName1);
            plantName = itemView.findViewById(R.id.form_textFieldPlantName1);
            userID = itemView.findViewById(R.id.form_textFieldUserId1);
        }
    }


}
