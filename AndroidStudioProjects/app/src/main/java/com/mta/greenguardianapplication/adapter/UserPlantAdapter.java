package com.mta.greenguardianapplication.adapter;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.mta.greenguardianapplication.AddUserPlantForm;
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
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.user_plant_item,parent,false);
        return new UserPlantHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull UserPlantHolder holder, int position, @NonNull UserPlant model) {
        holder.bind(model);
    }

    class UserPlantHolder extends RecyclerView.ViewHolder {
        TextView nickName, plantType, optimalHumidity, currentHumidity;
        CircleImageView imageView;
        ProgressBar humidityDif;
        ImageButton editButton;


        public UserPlantHolder(@NonNull View itemView) {
            super(itemView);
            nickName = itemView.findViewById(R.id.form_userPlant_textFieldNickName);
            plantType = itemView.findViewById(R.id.form_userPlant_textFieldPlantType);
            optimalHumidity = itemView.findViewById(R.id.form_userPlant_textFieldOptimalHumidity);
            imageView = itemView.findViewById(R.id.form_userPlant_imageView);
            currentHumidity = itemView.findViewById(R.id.current_humidity_value);
            humidityDif = itemView.findViewById(R.id.positiveProgressBar);
            editButton = itemView.findViewById(R.id.editButton);
        }

        void bind(UserPlant userPlant) {
            nickName.setText(userPlant.getNickName());
            optimalHumidity.setText(String.valueOf(userPlant.getOptimalHumidity()));
            plantType.setText(String.valueOf(userPlant.getPlantType()));
            currentHumidity.setText(String.valueOf(userPlant.getCurrentHumidity()));
            calcProgress(userPlant.getCurrentHumidity(), userPlant.getOptimalHumidity());
            Glide.with(imageView.getContext())
                    .load(userPlant.getPictureUrl())
                    .apply(new RequestOptions().circleCrop())
                    .into(imageView);

            /*editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Handle button click event
                }
            });*/

            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Get the position of the item
                    int position = getBindingAdapterPosition();

                    // Check if the position is valid
                    if (position != RecyclerView.NO_POSITION) {
                        // Get the clicked plant item
                        UserPlant clickedPlant = getItem(position);
                        String plantId = getSnapshots().getSnapshot(position).getKey();

                        // Retrieve the values you want to pass to the another form activity
                        String optimalHumidity = String.valueOf(clickedPlant.getOptimalHumidity());

                        // Create the intent and pass the values as extras
                        Intent intent = new Intent(v.getContext(), AddUserPlantForm.class);
                        intent.putExtra("plantType", clickedPlant.getPlantType());
                        intent.putExtra("optimalHumidity", optimalHumidity);
                        intent.putExtra("pictureUrl", clickedPlant.getPictureUrl());
                        intent.putExtra("nickName", clickedPlant.getNickName());
                        intent.putExtra("boardId", clickedPlant.getBoardId());
                        intent.putExtra("currentHumidity", clickedPlant.getCurrentHumidity());
                        intent.putExtra("plantId", clickedPlant.getPlantId());
                        v.getContext().startActivity(intent);
                    }
                }
            });
        }

        void calcProgress(int currentHumidity, int optimalHumidity){
            humidityDif.setMax(optimalHumidity);

            if (currentHumidity <= optimalHumidity) {
                humidityDif.setProgress(currentHumidity);
            } else {
                humidityDif.setProgress(optimalHumidity-(currentHumidity-optimalHumidity));
            }
            setColor();
        }

        void setColor(){
            Drawable progressDrawable = humidityDif.getProgressDrawable();
            int progressPercentage = (int) ((humidityDif.getProgress() * 100.0f) / humidityDif.getMax());
            if(progressPercentage == 0){
                humidityDif.setProgress(1);
            }
            int progressColor;

            LayerDrawable layerDrawable = (LayerDrawable) progressDrawable;
            if(progressPercentage < 30){
                progressColor = Color.RED;
            } else if (progressPercentage<69) {
                progressColor = Color.parseColor("#FFA500");
            }
            else {
                progressColor= Color.parseColor("#A4C639");
            }

            layerDrawable.findDrawableByLayerId(android.R.id.progress).setColorFilter(progressColor, PorterDuff.Mode.SRC_IN);
            humidityDif.setProgressDrawable(layerDrawable);
        }
    }
}
