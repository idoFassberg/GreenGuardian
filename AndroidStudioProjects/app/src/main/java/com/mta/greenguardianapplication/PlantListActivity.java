package com.mta.greenguardianapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mta.greenguardianapplication.adapter.PlantAdapter;
import com.mta.greenguardianapplication.model.Plant;

public class PlantListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private PlantAdapter plantAdapter;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plant_list);

        recyclerView = findViewById(R.id.recycler_view_plant_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        progressBar = findViewById(R.id.progress_bar_plant_list);
        progressBar.setVisibility(View.VISIBLE);

        // Use a Query object to limit the number of plants retrieved
        Query query = FirebaseDatabase.getInstance().getReference("PlantsStorage").limitToLast(10);

        // Pass the Query object to the FirebaseRecyclerOptions.Builder
        FirebaseRecyclerOptions<Plant> options = new FirebaseRecyclerOptions.Builder<Plant>()
                .setQuery(query, Plant.class).build();

        // Create a new PlantAdapter with the options
        plantAdapter = new PlantAdapter(options);

        // Set the PlantAdapter to the RecyclerView
        recyclerView.setAdapter(plantAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (plantAdapter != null) {
            plantAdapter.startListening();
        }
        progressBar.setVisibility(View.GONE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (plantAdapter != null) {
            plantAdapter.stopListening();
        }
    }
}