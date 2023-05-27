package com.mta.greenguardianapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mta.greenguardianapplication.LoginSignup.SignupScreen;
import com.mta.greenguardianapplication.LoginSignup.StartupScreen;

public class ProfileActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    Button btn_back;
    TextView userName,userEmail;
    DrawerLayout drawerLayout;
    ImageView menu;
    LinearLayout myPlants, forum, logout,addPlant,myProfile,plantsLibrary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        //ActionBar actionBar = getSupportActionBar();
        //actionBar.setTitle("Profile");
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        userName = findViewById(R.id.profileName);
        userEmail = findViewById(R.id.EmailUser);
        btn_back = findViewById(R.id.back_btn_profile);

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
                redirectActivity(ProfileActivity.this, UserPlantListActivity.class);
            }
        });

        plantsLibrary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                redirectActivity(ProfileActivity.this, PlantListActivity.class);
            }
        });

        forum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                redirectActivity(ProfileActivity.this,ForumActivity2.class);
            }
        });

        addPlant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(ProfileActivity.this,AddUserPlantForm.class);
            }
        });

        myProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recreate();
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ProfileActivity.this, "Logout", Toast.LENGTH_SHORT).show();
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), StartupScreen.class); //needs to change
                startActivity(intent);
                finish();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        checkUserStatus();
    }

    private void checkUserStatus(){
        user = mAuth.getCurrentUser();
        if(user != null){
            String userId = user.getUid();

            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
            usersRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String email = dataSnapshot.child("email").getValue(String.class);
                        String name = dataSnapshot.child("name").getValue(String.class);
                        userName.setText(name);
                        userEmail.setText(email);
                        // Do something with the email and name
                        // e.g., update UI, store in variables, etc.
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }

            });


        }
        else{//user is not sign in
            Intent intent = new Intent(getApplicationContext(), StartupScreen.class); //needs to change
            startActivity(intent);
            finish();
        }
    }

    public void OnClickBack(View view) {
        Intent intent =  new Intent(getApplicationContext(), UserPlantListActivity.class);
        startActivity(intent);
        finish();
    }

    public void onClickLogout(View view) {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getApplicationContext(), StartupScreen.class); //needs to change
        startActivity(intent);
        finish();
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
}