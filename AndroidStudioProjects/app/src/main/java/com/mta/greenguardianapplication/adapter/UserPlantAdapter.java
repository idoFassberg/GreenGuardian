package com.mta.greenguardianapplication.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.mta.greenguardianapplication.R;
import com.mta.greenguardianapplication.model.Plant;
import com.mta.greenguardianapplication.model.UserPlant;

import de.hdodenhof.circleimageview.CircleImageView;

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
        holder.bind(model);
    }



    class UserPlantHolder extends RecyclerView.ViewHolder {
        TextView nickName, plantType, optimalHumidity;
        CircleImageView imageView;

        public UserPlantHolder(@NonNull View itemView) {
            super(itemView);
            nickName = itemView.findViewById(R.id.form_userPlant_textFieldNickName);
            plantType = itemView.findViewById(R.id.form_userPlant_textFieldPlantType);
            optimalHumidity = itemView.findViewById(R.id.form_userPlant_textFieldOptimalHumidity);
            imageView = itemView.findViewById(R.id.form_userPlant_imageView);
        }

        void bind(UserPlant userPlant) {
            nickName.setText(userPlant.getNickName());
            optimalHumidity.setText(String.valueOf(userPlant.getOptimalHumidity()));
            plantType.setText(String.valueOf(userPlant.getPlantType()));
            //imageView.setTag(userPlant.getPictureUrl());
            Glide.with(imageView.getContext())
                    .load(userPlant.getPictureUrl())
                    .apply(new RequestOptions().circleCrop())
                    .into(imageView);
        }
    }
}
