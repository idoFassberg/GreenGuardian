package com.mta.greenguardianapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mta.greenguardianapplication.adapter.UserAdapter;
import com.mta.greenguardianapplication.databinding.ActivityUserBinding;
import com.mta.greenguardianapplication.listeners.UserListener;
import com.mta.greenguardianapplication.model.User;

import java.util.ArrayList;
import java.util.List;



public class UserActivity extends AppCompatActivity implements UserListener {

    private ActivityUserBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListeners();

        // Bind the RecyclerView
        binding.userRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        UserAdapter userAdapter = new UserAdapter(new ArrayList<>(), this); // Create an empty adapter initially
        binding.userRecyclerView.setAdapter(userAdapter);

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

                loading(true);

                // Iterate through all child nodes (users) and deserialize them into User objects
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    // Check if the user data exists
                    if (userSnapshot.exists()) {
                        // Get the email and name from the specific user's snapshot
                        String email = userSnapshot.child("email").getValue(String.class);
                        String name = userSnapshot.child("name").getValue(String.class);
                        String id = userSnapshot.child("uid").getValue(String.class);
                        String profileImageUrl = userSnapshot.child("image").getValue(String.class);
                        User user = new User(id, name, email,profileImageUrl);
                        users.add(user);
                    }
                }

                loading(false);

                UserAdapter userAdapter = new UserAdapter(users, UserActivity.this);
                binding.userRecyclerView.setAdapter(userAdapter);

                userAdapter.notifyDataSetChanged();
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

    @Override
    public void onUserClicked(User user) {
        Intent intent =  new Intent(getApplicationContext(), ChatActivity.class);
        intent.putExtra("id", user.getId());
        intent.putExtra("name", user.getName());
        intent.putExtra("email", user.getEmail());
        intent.putExtra("image", user.getImage());
        startActivity(intent);
        finish();
    }
}