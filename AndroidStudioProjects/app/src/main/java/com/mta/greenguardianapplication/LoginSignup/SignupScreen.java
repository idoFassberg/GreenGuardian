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

import com.mta.greenguardianapplication.AddUserPlantForm;
import com.mta.greenguardianapplication.R;

public class SignupScreen extends AppCompatActivity {

    //Variabls
    Button login, signup;
    TextView back;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_signup_screen);

        //Hooks
        back = findViewById(R.id.back_btn);
        login = findViewById(R.id.login_btn);
        signup = findViewById(R.id.signup_signupscreen_btn);
    }

    public void callSignup(View view){
        Intent intent = new Intent(getApplicationContext(), StartupScreen.class); //needs to change

        Pair[] pairs = new Pair[1];

        pairs[0] = new Pair<View, String>(signup, "transition_signup_signupscreen");

        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SignupScreen.this, pairs);
        startActivity(intent, options.toBundle());
    }

    public void callLoginScreen(View view){

        Intent intent =  new Intent(getApplicationContext(), LoginScreen.class);

        Pair[] pairs = new Pair[1];
        pairs[0] = new Pair<View, String>(login,"transition_login_signupscreen");

        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SignupScreen.this, pairs);
        startActivity(intent, options.toBundle());
    }

    public void callStartupScreen(){
        Intent intent =  new Intent(getApplicationContext(), StartupScreen.class);

        Pair[] pairs = new Pair[1];
        pairs[0] = new Pair<View, String>(back,"transition_startup");

        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SignupScreen.this, pairs);
        startActivity(intent, options.toBundle());
    }

}