package com.mta.greenguardianapplication.adapter;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.github.mikephil.charting.data.BarEntry;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mta.greenguardianapplication.AddUserPlantForm;
import com.mta.greenguardianapplication.GraphActivity;
import com.mta.greenguardianapplication.R;
import com.mta.greenguardianapplication.StatsDialog;
import com.mta.greenguardianapplication.UserPlantListActivity;
import com.mta.greenguardianapplication.model.Plant;
import com.mta.greenguardianapplication.model.UserPlant;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserPlantAdapter extends FirebaseRecyclerAdapter<UserPlant,UserPlantAdapter.UserPlantHolder> {
    private FragmentManager fragmentManager;

    public UserPlantAdapter(@NonNull FirebaseRecyclerOptions<UserPlant> options, FragmentManager fragmentManager) {
        super(options);
        this.fragmentManager = fragmentManager;
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

    class UserPlantHolder extends RecyclerView.ViewHolder implements StatsDialog.DialogListener{
        TextView nickName, plantType, optimalHumidity, currentHumidity;
        CircleImageView imageView;
        ProgressBar humidityDif;
        ImageButton editButton, statsButton, historyButton;
        View view;


        public UserPlantHolder(@NonNull View itemView) {
            super(itemView);
            nickName = itemView.findViewById(R.id.form_userPlant_textFieldNickName);
            plantType = itemView.findViewById(R.id.form_userPlant_textFieldPlantType);
            optimalHumidity = itemView.findViewById(R.id.form_userPlant_textFieldOptimalHumidity);
            imageView = itemView.findViewById(R.id.form_userPlant_imageView);
            currentHumidity = itemView.findViewById(R.id.current_humidity_value);
            humidityDif = itemView.findViewById(R.id.positiveProgressBar);
            editButton = itemView.findViewById(R.id.editButton);
            statsButton = itemView.findViewById(R.id.statsButton);
            historyButton = itemView.findViewById(R.id.historyButton);
        }

        void bind(UserPlant userPlant) {
            nickName.setText(userPlant.getNickName());
            optimalHumidity.setText(String.valueOf(userPlant.getOptimalHumidity()));
            plantType.setText(String.valueOf(userPlant.getPlantType()));
            currentHumidity.setText(String.valueOf(userPlant.getCurrentHumidity()));
            String pictureUrl = userPlant.getPictureUrl();
            calcProgress(userPlant.getCurrentHumidity(), userPlant.getOptimalHumidity());
            if (!TextUtils.isEmpty(pictureUrl)) {
                Glide.with(imageView.getContext())
                        .load(pictureUrl)
                        .apply(new RequestOptions().circleCrop())
                        .into(imageView);
            } else {
                // Load the default image when pictureUrl is empty
                Glide.with(imageView.getContext())
                        .load(R.drawable.ic_launcher_background)
                        .apply(new RequestOptions().circleCrop())
                        .into(imageView);
            }

            DatabaseReference currentHumidityRef = FirebaseDatabase.getInstance()
                    .getReference("Users")
                    .child(userPlant.getUserId())
                    .child("plants")
                    .child(userPlant.getNickName())
                    .child("currentHumidity");

            currentHumidityRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // Retrieve the updated currentHumidity value
                        int updatedHumidity = dataSnapshot.getValue(Integer.class);

                        // Update the UI with the new humidity value
                        currentHumidity.setText(String.valueOf(updatedHumidity));

                        // Recalculate and set the progress
                        calcProgress(updatedHumidity, userPlant.getOptimalHumidity());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle error if necessary
                }
            });

            statsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    view = v;
                    openDialog();
                }
            });

            historyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getBindingAdapterPosition();

                    if (position != RecyclerView.NO_POSITION) {
                        UserPlant clickedPlant = getItem(position);
                        Intent intent = new Intent(v.getContext(), GraphActivity.class);
                        intent.putExtra("plantId", clickedPlant.getPlantId());
                        intent.putExtra("userId", clickedPlant.getUserId());
                        intent.putExtra("optimalHumidity", clickedPlant.getOptimalHumidity()); // Pass optimalHumidity
                        intent.putExtra("nickName", clickedPlant.getNickName());
                        intent.putExtra("history", true);
                        v.getContext().startActivity(intent);
                    }
                }
            });

            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Get the position of the item
                    int position = getBindingAdapterPosition();
                    Log.d("position is ", String.valueOf(position));
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
                        intent.putExtra("currentHumidity", clickedPlant.getCurrentHumidity());
                        intent.putExtra("plantId", clickedPlant.getPlantId());
                        v.getContext().startActivity(intent);
                    }
                }
            });
        }

        void calcProgress(int currentHumidity, int optimalHumidity) {
            humidityDif.setMax(optimalHumidity);
            humidityDif.setProgress(currentHumidity);

            int progressPercentage = (int) ((currentHumidity * 100.0f) / optimalHumidity);
            setColor(progressPercentage);
        }

        void setColor(int progressPercentage) {
            LayerDrawable layerDrawable = (LayerDrawable) humidityDif.getProgressDrawable();
            Drawable progressDrawable = layerDrawable.findDrawableByLayerId(android.R.id.progress);

            int progressColor;

            if (progressPercentage < 30) {
                progressColor = Color.RED;
            } else if (progressPercentage < 70) {
                progressColor = Color.parseColor("#FFA500"); // Orange
            } else {
                progressColor = Color.parseColor("#A4C639"); // Green
            }

            progressDrawable.setColorFilter(progressColor, PorterDuff.Mode.SRC_IN);
            humidityDif.setProgressDrawable(layerDrawable);
        }

        public void openDialog(){
            StatsDialog statsDialog = new StatsDialog();
            statsDialog.show(fragmentManager , "custom_dialog");

        }

        @Override
        public void onProceedClicked(String fromDate, String toDate, int selectedRadioId) {
            int position = getBindingAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                UserPlant clickedPlant = getItem(position);
                Intent intent = new Intent(view.getContext(), GraphActivity.class);
                intent.putExtra("plantId", clickedPlant.getPlantId());
                intent.putExtra("userId", clickedPlant.getUserId());
                intent.putExtra("optimalHumidity", clickedPlant.getOptimalHumidity()); // Pass optimalHumidity
                intent.putExtra("nickName", clickedPlant.getNickName());
                intent.putExtra("history", false);
                view.getContext().startActivity(intent);
            }
        }

        @Override
        public void onCancelClicked() {

        }
    }
}
