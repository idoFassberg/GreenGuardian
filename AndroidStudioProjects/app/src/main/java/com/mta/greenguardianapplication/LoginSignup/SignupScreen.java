package com.mta.greenguardianapplication.LoginSignup;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mta.greenguardianapplication.AddUserPlantForm;
import com.mta.greenguardianapplication.ProfileActivity;
import com.mta.greenguardianapplication.R;

public class SignupScreen extends AppCompatActivity {

    //Variabls
    private FirebaseAuth mAuth;
    Button login, signup;
    TextView back;
    TextInputEditText te_userName,te_fullName,te_email,te_password;
    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_signup_screen);

        //Hooks
        back = findViewById(R.id.back_btn);
        login = findViewById(R.id.login_btn);
        signup = findViewById(R.id.signup_signupscreen_btn);
        progressBar = findViewById(R.id.progressBar);

        te_userName = findViewById(R.id.userNameSignup);
        te_fullName = findViewById(R.id.fullNameSignup);
        te_email = findViewById(R.id.emailSignup);
        te_password = findViewById(R.id.passwordSignup);

        /*signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = te_email.getText().toString();
                String password = te_password.getText().toString();
                progressBar.setVisibility(View.VISIBLE);


                if (Patterns.EMAIL_ADDRESS.matcher(email).matches() && isPasswordValid(password)) {
                    registerUser(email,password);
                } else {
                    Toast.makeText(getApplicationContext(), "Invalid email or password", Toast.LENGTH_SHORT).show();
                }
            }
        });*/
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent intent = new Intent(getApplicationContext(), ProfileActivity.class); //needs to change
            startActivity(intent);
            finish();
        }
    }
    // Email validation method
    private boolean isEmailValid(String email) {
        // You can use a regular expression or any other email validation logic here
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    // Password validation method
    private boolean isPasswordValid(String password) {
        // Add your password validation logic here
        return password.length() >= 6; // For example, minimum 6 characters
    }

    private void registerUser(String email,String password)
    {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            Toast.makeText(getApplicationContext(), "Account created", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                            startActivity(intent);
                            finish();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignupScreen.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    public void callSignup(View view){
        String email = te_email.getText().toString();
        String password = te_password.getText().toString();
        progressBar.setVisibility(View.VISIBLE);

        if (Patterns.EMAIL_ADDRESS.matcher(email).matches() && isPasswordValid(password)) {
            registerUser(email,password);
        } else {
            Toast.makeText(getApplicationContext(), "Invalid email or password", Toast.LENGTH_SHORT).show();
        }

        /*Intent intent = new Intent(getApplicationContext(), StartupScreen.class); //needs to change
        Pair[] pairs = new Pair[1];
        pairs[0] = new Pair<View, String>(signup, "transition_signup_signupscreen");
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SignupScreen.this, pairs);
        startActivity(intent, options.toBundle());*/
    }

    public void callLoginScreen(View view){

        Intent intent =  new Intent(getApplicationContext(), LoginScreen.class);

        Pair[] pairs = new Pair[1];
        pairs[0] = new Pair<View, String>(login,"transition_login_signupscreen");

        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SignupScreen.this, pairs);
        startActivity(intent, options.toBundle());
    }

    public void callStartupScreen(View view){
        Intent intent =  new Intent(getApplicationContext(), StartupScreen.class);

        Pair[] pairs = new Pair[1];
        pairs[0] = new Pair<View, String>(back,"transition_startup");

        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SignupScreen.this, pairs);
        startActivity(intent, options.toBundle());
    }

}