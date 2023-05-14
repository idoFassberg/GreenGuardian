package com.mta.greenguardianapplication.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.mta.greenguardianapplication.R;
import com.mta.greenguardianapplication.model.Plant;

import java.util.List;

public class PlantAdapter extends FirebaseRecyclerAdapter<Plant, PlantAdapter.PlantHolder> {

    public PlantAdapter(@NonNull FirebaseRecyclerOptions<Plant> options) {
        super(options);
    }

    @NonNull
    @Override
    public PlantHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.plant_list_item, parent, false);
        return new PlantHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull PlantHolder holder, int position, @NonNull Plant model) {
        holder.bind(model);
    }

    static class PlantHolder extends RecyclerView.ViewHolder {
        ImageView pictureView;
        TextView typeView, humidityView;

        PlantHolder(View itemView) {
            super(itemView);
            pictureView = itemView.findViewById(R.id.plant_image);
            typeView = itemView.findViewById(R.id.plant_type);
            humidityView = itemView.findViewById(R.id.plant_humidity);
        }

        void bind(Plant plant) {
            typeView.setText(plant.getType());
            humidityView.setText(String.valueOf(plant.getRecommendedHumidity()));
            Glide.with(pictureView.getContext())
                    .load(plant.getPictureUrl())
                    .apply(new RequestOptions().circleCrop())
                    .into(pictureView);
        }
    }
}
