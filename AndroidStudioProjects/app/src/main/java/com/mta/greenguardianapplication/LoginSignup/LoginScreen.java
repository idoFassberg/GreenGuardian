package com.mta.greenguardianapplication.LoginSignup;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.mta.greenguardianapplication.AddUserPlantForm;
import com.mta.greenguardianapplication.R;

public class LoginScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login_screen);
    }

    public void callStratupScreen(){
        Intent intent =  new Intent(getApplicationContext(), StartupScreen.class);

        Pair[] pairs = new Pair[1];
        pairs[0] = new Pair<View, String>(findViewById(R.id.back_btn_loginscreen),"transition_startup_loginscreen");

        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(LoginScreen.this, pairs);
        startActivity(intent, options.toBundle());
    }

    public void callSignupScreen(View view){

        Intent intent = new Intent(getApplicationContext(), SignupScreen.class);

        Pair[] pairs = new Pair[1];
        pairs[0] = new Pair<View, String>(findViewById(R.id.signup_btn),"transition_signup");

        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(LoginScreen.this, pairs);
        startActivity(intent, options.toBundle());
    }
}