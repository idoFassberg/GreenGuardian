package com.mta.greenguardianapplication;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.auth.User;
import com.mta.greenguardianapplication.LoginSignup.StartupScreen;
import com.mta.greenguardianapplication.adapter.PlantAdapter;
import com.mta.greenguardianapplication.adapter.UserPlantAdapter;
import com.mta.greenguardianapplication.model.Plant;
import com.mta.greenguardianapplication.model.UserPlant;



import java.util.List;


public class UserPlantListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private UserPlantAdapter userPlantAdapter;

    DrawerLayout drawerLayout;
    ImageView menu;
    LinearLayout myPlants, forum, logout,addPlant,myProfile,plantsLibrary;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateHumidity();
        setContentView(R.layout.activity_user_plant_list);
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

        forum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                redirectActivity(UserPlantListActivity.this,ForumActivity2.class);
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
        userPlantAdapter = new UserPlantAdapter(options);

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

    @Override
    protected void onStop() {
        super.onStop();
        userPlantAdapter.stopListening();
    }

    private static void updateHumidity(){
        // Create a Handler on the main (UI) thread
        Handler handler = new Handler(Looper.getMainLooper());

        // Define a Runnable to update the humidity
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                // Perform the humidity update here

                // Get the reference to the RealTimeData node in Firebase
                DatabaseReference realTimeDataRef = FirebaseDatabase.getInstance().getReference("RealTimeData");

                // Query the RealTimeData node for the necessary board ID (replace "boardId" with the actual board ID)
                Query query = realTimeDataRef.orderByKey();
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        // Iterate over the matching data snapshots
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            // Get the humidity value from the snapshot
                            Integer humidity = snapshot.child("humidity").getValue(Integer.class);

                            // Get the boardId associated with the snapshot
                            String boardId = snapshot.getKey();
                            DatabaseReference userPlantsRef = FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getUid()).child("plants");

                            // Update the current humidity of the corresponding plant in the UserPlants table
                            //DatabaseReference userPlantsRef = FirebaseDatabase.getInstance().getReference("UserPlants");
                            Query userPlantsQuery = userPlantsRef.orderByChild("boardId").equalTo(boardId);

                            userPlantsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot userPlantsDataSnapshot) {
                                    for (DataSnapshot userPlantsSnapshot : userPlantsDataSnapshot.getChildren()) {
                                        userPlantsSnapshot.getRef().child("currentHumidity").setValue(humidity);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    // Handle potential errors here
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle potential errors here
                    }
                });


                // Schedule the next update after 10 minutes
                handler.postDelayed(this, 10  * 1000); // 10 minutes (10 * 60 * 1000 milliseconds)
            }
        };

        // Start the initial update immediately
        runnable.run();
    }
}