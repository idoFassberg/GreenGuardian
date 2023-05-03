package com.mta.greenguardianapplication;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.mta.greenguardianapplication.adapter.UserPlantAdapter;
import com.mta.greenguardianapplication.model.UserPlant;

import java.util.List;


public class UserPlantListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DatabaseReference mDatabase;
    private UserPlantAdapter userPlantAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_plant_list);
        mDatabase = FirebaseDatabase.getInstance().getReference();

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
        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("UserPlant")
                .limitToLast(10);
        FirebaseRecyclerOptions< UserPlant> options =
                new FirebaseRecyclerOptions.Builder<UserPlant>()
                        .setQuery(query, UserPlant.class)
                        .build();
        userPlantAdapter = new UserPlantAdapter(options);
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
