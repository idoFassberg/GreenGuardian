package com.mta.greenguardianapplication;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.auth.User;
import com.mta.greenguardianapplication.adapter.PlantAdapter;
import com.mta.greenguardianapplication.adapter.UserPlantAdapter;
import com.mta.greenguardianapplication.model.Plant;
import com.mta.greenguardianapplication.model.UserPlant;

import java.util.List;


public class UserPlantListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private UserPlantAdapter userPlantAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_plant_list);

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

        Query query = FirebaseDatabase.getInstance().getReference("UserPlants").limitToLast(10);

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
}
