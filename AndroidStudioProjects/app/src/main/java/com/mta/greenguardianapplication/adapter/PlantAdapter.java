package com.mta.greenguardianapplication.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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
import com.mta.greenguardianapplication.AddUserPlantForm;

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
        //holder.pictureView.setTag(model.getPictureUrl());
        holder.bind(model);
    }

    class PlantHolder extends RecyclerView.ViewHolder {
        ImageView pictureView;
        TextView typeView, humidityView;
        ImageButton addButton;

        PlantHolder(View itemView) {
            super(itemView);
            pictureView = itemView.findViewById(R.id.plant_image);
            typeView = itemView.findViewById(R.id.plant_type);
            humidityView = itemView.findViewById(R.id.optimal_humidity);
            addButton = itemView.findViewById(R.id.add_button);
        }

        void bind(Plant plant) {
            typeView.setText(plant.getType());
            humidityView.setText(String.valueOf(plant.getRecommendedHumidity()));
            Glide.with(pictureView.getContext())
                    .load(plant.getPictureUrl())
                    .apply(new RequestOptions().circleCrop())
                    .into(pictureView);

            addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Get the position of the item
                    int position = getBindingAdapterPosition();

                    // Check if the position is valid
                    if (position != RecyclerView.NO_POSITION) {
                        // Get the clicked plant item
                        Plant clickedPlant = getItem(position);

                        // Retrieve the values you want to pass to the another form activity
                        String plantType = clickedPlant.getType();
                        String optimalHumidity = String.valueOf(clickedPlant.getRecommendedHumidity());
                        String pictureUrl = clickedPlant.getPictureUrl();

                        // Create the intent and pass the values as extras
                        Intent intent = new Intent(v.getContext(), AddUserPlantForm.class);
                        intent.putExtra("plantType", plantType);
                        intent.putExtra("optimalHumidity", optimalHumidity);
                        intent.putExtra("pictureUrl", pictureUrl);
                        v.getContext().startActivity(intent);
                    }
                }
            });

            /*pictureView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(YourActivity.this);
                    LayoutInflater inflater = LayoutInflater.from(YourActivity.this);
                    View dialogView = inflater.inflate(R.layout.dialog_image, null);
                    ImageView dialogImageView = dialogView.findViewById(R.id.dialog_image);

                    // Set the image resource or load the image using Glide or Picasso
                    dialogImageView.setImageResource(R.drawable.your_image);

                    builder.setView(dialogView);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            });*/
        }
    }
}
