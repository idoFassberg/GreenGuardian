package com.mta.greenguardianapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.mta.greenguardianapplication.LoginSignup.StartupScreen;
/*import com.mta.greenguardianapplication.databinding.ActivityMainChatBinding;*/
import com.mta.greenguardianapplication.utilities.Constants;
import com.mta.greenguardianapplication.utilities.PreferenceManager;


public class MainChatActivity extends AppCompatActivity {

    /*private ActivityMainChatBinding binding;*/
    private PreferenceManager preferenceManager;
    private FirebaseUser user;
    private FirebaseAuth mAuth;

    DrawerLayout drawerLayout;
    ImageView menu;
    LinearLayout myPlants, chat, logout, plantsLibrary, addPlant, myProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*binding = ActivityMainChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());*/
        setContentView(R.layout.activity_main_chat);

        drawerLayout = findViewById(R.id.drawerLayout);
        menu = findViewById(R.id.menu);
        /*myPlants = findViewById(R.id.myPlants);
        chat = findViewById(R.id.chat);
        logout = findViewById(R.id.logoutNav);
        plantsLibrary = findViewById(R.id.plantsLibrary);
        addPlant = findViewById(R.id.add_plant);
        myProfile = findViewById(R.id.my_profile);*/

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDrawer(drawerLayout);
            }
        });


        preferenceManager = new PreferenceManager(getApplicationContext());
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid());
        /*loadUserDetails(userRef);*/
        /*getToken();*/


        setListeners();




        /*chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recreate();
            }
        });

        myPlants.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                redirectActivity(MainChatActivity.this, UserPlantListActivity.class);
            }
        });

        myProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(MainChatActivity.this,ProfileActivity.class);
            }
        });

        addPlant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(MainChatActivity.this,AddUserPlantForm.class);            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainChatActivity.this, "Logout", Toast.LENGTH_SHORT).show();
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), StartupScreen.class); //needs to change
                startActivity(intent);
                finish();
            }
        });*/


    }

    public static void openDrawer(DrawerLayout drawerLayout){
        drawerLayout.openDrawer(GravityCompat.START);
    }


    public static void closeDrawer(DrawerLayout drawerLayout){
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        closeDrawer(drawerLayout);
    }

    private void setListeners(){
        findViewById(R.id.fabNewChat).setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(), UserActivity.class)));
        /*binding.fabNewChat.setOnClickListener(v->
                startActivity(new Intent(getApplicationContext(), UserActivity.class)));*/
    }

    /*private void loadUserDetails(DatabaseReference userRef) {
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String name = dataSnapshot.child("name").getValue(String.class);
                    TextView textName = findViewById(R.id.textName);
                    textName.setText(name);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }

        });
    }*/

    private void showToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void getToken(){
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(this::updateToken);
    }

    private void updateToken(String token){
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference =
                database.collection(Constants.KEY_COLLECTION_USERS).document(
                        preferenceManager.getString(Constants.KEY_USER_ID)
                );
        documentReference.update(Constants.KEY_FCM_TOKEN, token)
                .addOnSuccessListener(unused -> showToast("Token updated successfully"))
                .addOnFailureListener(e -> showToast("Unable to update token"));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        redirectActivity(MainChatActivity.this, UserPlantListActivity.class);

    }

    public static void redirectActivity(Activity activity, Class secondActivity){
        Intent intent = new Intent(activity, secondActivity);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
        activity.finish();
    }


}