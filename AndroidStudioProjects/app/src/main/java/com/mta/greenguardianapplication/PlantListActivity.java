package com.mta.greenguardianapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.mta.greenguardianapplication.LoginSignup.StartupScreen;
import com.mta.greenguardianapplication.adapter.PlantAdapter;
import com.mta.greenguardianapplication.model.Plant;

public class PlantListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private PlantAdapter plantAdapter;
    private ProgressBar progressBar;

    DrawerLayout drawerLayout;
    ImageView menu;
    LinearLayout myPlants, forum, logout, plantsLibrary,addPlant,myProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plant_list);

        drawerLayout = findViewById(R.id.drawerLayout);
        menu = findViewById(R.id.menu);
        myPlants = findViewById(R.id.myPlants);
        forum = findViewById(R.id.forum);
        logout = findViewById(R.id.logoutNav);
        plantsLibrary = findViewById(R.id.plantsLibrary);
        addPlant = findViewById(R.id.add_plant);
        myProfile = findViewById(R.id.my_profile);

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDrawer(drawerLayout);
            }
        });

        plantsLibrary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recreate();
            }
        });

        myPlants.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                redirectActivity(PlantListActivity.this, UserPlantListActivity.class);
            }
        });

        forum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                redirectActivity(PlantListActivity.this, MainChatActivity.class);
            }
        });
        myProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(PlantListActivity.this,ProfileActivity.class);
            }
        });

        addPlant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(PlantListActivity.this,AddUserPlantForm.class);            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(PlantListActivity.this, "Logout", Toast.LENGTH_SHORT).show();
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), StartupScreen.class); //needs to change
                startActivity(intent);
                finish();
            }
        });


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

    public  static void openDrawer(DrawerLayout drawerLayout){
        drawerLayout.openDrawer(GravityCompat.START);
    }

    public static void closeDrawer(DrawerLayout drawerLayout){
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    public  static void redirectActivity(Activity activity, Class secondActivity){
        Intent intent = new Intent(activity, secondActivity);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
        activity.finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        closeDrawer(drawerLayout);
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