package com.mta.greenguardianapplication;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.mta.greenguardianapplication.LoginSignup.StartupScreen;
import com.mta.greenguardianapplication.adapter.UserPlantAdapter;
import com.mta.greenguardianapplication.model.UserPlant;

import java.util.Objects;


public class UserPlantListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private UserPlantAdapter userPlantAdapter;

    DrawerLayout drawerLayout;
    ImageView menu;
    LinearLayout myPlants, chat, logout,addPlant,myProfile,plantsLibrary;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //updateHumidity();
        setContentView(R.layout.activity_user_plant_list);
        drawerLayout = findViewById(R.id.drawerLayout);
        menu = findViewById(R.id.menu);
        myPlants = findViewById(R.id.myPlants);
        chat = findViewById(R.id.chat);
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

        myPlants.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recreate();
            }
        });

        plantsLibrary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                redirectActivity(UserPlantListActivity.this, PlantListActivity.class);
            }
        });

        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                redirectActivity(UserPlantListActivity.this, MainChatActivity.class);
            }
        });

        addPlant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(UserPlantListActivity.this,AddUserPlantForm.class);
            }
        });

        myProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(UserPlantListActivity.this,ProfileActivity.class);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(UserPlantListActivity.this, "Logout", Toast.LENGTH_SHORT).show();
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), StartupScreen.class); //needs to change
                startActivity(intent);
                finish();
            }
        });
       /* ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                UserPlant post = dataSnapshot.getValue(UserPlant.class);
                // ..
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };
        mDatabase.addValueEventListener(postListener);*/

        recyclerView = findViewById(R.id.userPlantList_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        Query query = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid()).child("plants");

        //Add this later and change <MyUserId> to actual logged in user id
        //Query query = FirebaseDatabase.getInstance().getReference("UserPlants")
        //    .orderByChild("userId")
        //    .equalTo(<MyUserId>)
        //    .limitToLast(10);

        // Pass the Query object to the FirebaseRecyclerOptions.Builder
        FirebaseRecyclerOptions<UserPlant> options = new FirebaseRecyclerOptions.Builder<UserPlant>()
                .setQuery(query, UserPlant.class).build();

        // Create a new PlantAdapter with the options
        userPlantAdapter = new UserPlantAdapter(options, getSupportFragmentManager());

        // Set the PlantAdapter to the RecyclerView
        recyclerView.setAdapter(userPlantAdapter);
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
        userPlantAdapter.startListening();
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onStop() {
        super.onStop();
        if(userPlantAdapter != null) {
            userPlantAdapter.stopListening();
            Objects.requireNonNull(recyclerView.getAdapter()).notifyDataSetChanged();
        }
    }

}
