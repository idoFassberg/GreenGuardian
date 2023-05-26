package com.mta.greenguardianapplication;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mta.greenguardianapplication.LoginSignup.SignupScreen;
import com.mta.greenguardianapplication.LoginSignup.StartupScreen;

public class ProfileActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    Button btn_back;
    TextView userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        //ActionBar actionBar = getSupportActionBar();
        //actionBar.setTitle("Profile");
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        userName = findViewById(R.id.profileName);
        btn_back = findViewById(R.id.back_btn_profile);

    }

    @Override
    protected void onStart() {
        super.onStart();
        checkUserStatus();
    }

    private void checkUserStatus(){
        user = mAuth.getCurrentUser();
        if(user != null){
            userName.setText(user.getEmail());
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
}