package com.mta.greenguardianapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mta.greenguardianapplication.adapter.UserAdapter;
import com.mta.greenguardianapplication.databinding.ActivityUserBinding;
import com.google.firebase.database.DataSnapshot;
import com.mta.greenguardianapplication.model.User;
import com.google.firebase.database.DatabaseError;
import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;


public class UserActivity extends AppCompatActivity {

    private ActivityUserBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListeners();
        getUsers();
    }

    private void setListeners(){
        binding.imageBack.setOnClickListener(v->onBackPressed());
    }


    private void getUsers(){
        // Get a reference to the "users" node in the database
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("Users");

// Attach a ValueEventListener to the "users" node
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method will be called when data is changed in the "users" node

                List<User> users = new ArrayList<>();

                // Iterate through all child nodes (users) and deserialize them into User objects
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    // Check if the user data exists
                    if (userSnapshot.exists()) {
                        // Get the email and name from the specific user's snapshot
                        String email = userSnapshot.child("email").getValue(String.class);
                        String name = userSnapshot.child("name").getValue(String.class);

                        User user = new User(name, email);
                        users.add(user);
                    }
                }

                // Here, the 'users' list contains all the users' information with names and emails
                // You can do whatever you want with this list, such as displaying the data in your app UI.
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle the error if something goes wrong
            }
        });

    }
    private void showErrorMessage(){
        binding.textErrorMessage.setText(String.format("%s", "No user available"));
        binding.textErrorMessage.setVisibility(View.VISIBLE);
    }

    private void loading(Boolean isLoading){
        if (isLoading){
            binding.progressBar.setVisibility(View.VISIBLE);
        }
        else {
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }
}