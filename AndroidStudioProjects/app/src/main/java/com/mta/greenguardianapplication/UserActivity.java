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
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users");

        // Attach a ValueEventListener to the "users" node
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method will be called when data is changed in the "users" node

                List<User> users = new ArrayList<>();

                // Iterate through all child nodes (users) and deserialize them into User objects
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    User user = userSnapshot.getValue(User.class);
                    users.add(user);
                }

                if (users.size()>0){
                    UserAdapter userAdapter =  new UserAdapter(users);
                    binding.userRecyclerView.setAdapter(userAdapter);
                    binding.userRecyclerView.setVisibility(View.VISIBLE);
                }
                else {
                    showErrorMessage();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // This method will be called if there's an error reading data from the database
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