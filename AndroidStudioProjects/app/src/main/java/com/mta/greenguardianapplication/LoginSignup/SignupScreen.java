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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.messaging.FirebaseMessaging;
import com.mta.greenguardianapplication.AddUserPlantForm;
import com.mta.greenguardianapplication.ProfileActivity;
import com.mta.greenguardianapplication.UserPlantListActivity;
import com.mta.greenguardianapplication.R;

import java.util.HashMap;

public class SignupScreen extends AppCompatActivity {

    //Variabls
    private FirebaseAuth mAuth;
    Button login, signup;
    TextView back;
    TextInputEditText te_userName,te_fullName,te_email,te_password;
    ProgressBar progressBar;


    public TextInputEditText getTe_fullName() {
        return te_fullName;
    }

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
        /*progressBar = findViewById(R.id.progressBar);*/

        /*te_userName = findViewById(R.id.userNameSignup);*/
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
            Toast.makeText(SignupScreen.this, "User already login", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), UserPlantListActivity.class); //needs to change
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

    private void registerUser(String email, String password, String fullName) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        /*progressBar.setVisibility(View.GONE);*/
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");

                            // Get the FCM token and create User DataBase
                            getTokenAndCreateUser(fullName);

                            Toast.makeText(getApplicationContext(), "Account created", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), UserPlantListActivity.class);
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

    private void getTokenAndCreateUser(String fullName)
    {
        // Get the FCM token asynchronously using addOnSuccessListener and addOnFailureListener
        FirebaseMessaging.getInstance().getToken()
                .addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String token) {
                        // The FCM token is successfully retrieved
                        // Call createUserDB with the FCM token after the user is successfully registered
                        createUserDB(token,fullName);

                        // You can perform any other actions here after both user data and FCM token are saved
                        Log.d("FCM Token", "Token retrieved: " + token);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // An error occurred while retrieving the FCM token
                        Log.e("FCM Token", "Error getting FCM token: " + e.getMessage());
                        // Handle the failure gracefully, you can choose to proceed without the token
                        // or try again later
                    }
                });


    }
    private void createUserDB(String fcmToken, String fullName) {
        // Get the current user's UID
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String uid = user.getUid();

            // Get a reference to the "Users" node in the Firebase Realtime Database
            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Users");

            // Create the user data HashMap
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("email", user.getEmail());
            hashMap.put("uid", uid);
            hashMap.put("name", fullName);
            hashMap.put("phone", "");
            hashMap.put("plants", "");
            hashMap.put("image", "");

            // Set user data in the database and add an onSuccessListener
            usersRef.child(uid).setValue(hashMap)
                    .addOnSuccessListener(aVoid -> {
                        // After the user data is successfully saved, save the FCM token to the database
                        saveTokenToDatabase(uid, fcmToken);

                        // You can perform any other actions here after both user data and FCM token are saved
                        Log.d("FCM Token", "Token saved to database: " + fcmToken);
                    })
                    .addOnFailureListener(e -> {
                        // An error occurred while saving user data
                        Log.e("Create User", "Error saving user data: " + e.getMessage());
                    });
        }
    }
    private void saveTokenToDatabase(String uid, String token) {
        // Get a reference to the "Users" node in the Firebase Realtime Database
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Users");

        // Update the user's data with the FCM token
        usersRef.child(uid).child("fcmToken").setValue(token)
                .addOnSuccessListener(aVoid -> {
                    // The FCM token was successfully saved to the database
                    Log.d("FCM Token", "Token saved to database: " + token);
                })
                .addOnFailureListener(e -> {
                    // An error occurred while saving the FCM token to the database
                    Log.e("FCM Token", "Error saving token to database: " + e.getMessage());
                });
    }



    public void callSignup(View view){
        String email = te_email.getText().toString();
        String password = te_password.getText().toString();
        String fullName = te_fullName.getText().toString();
        /*progressBar.setVisibility(View.VISIBLE);*/

        if (Patterns.EMAIL_ADDRESS.matcher(email).matches() && isPasswordValid(password)) {
            registerUser(email,password,fullName);
        } else {
            Toast.makeText(getApplicationContext(), "Invalid email or password", Toast.LENGTH_SHORT).show();
        }
    }

    public void callLoginScreen(View view){
        Intent intent =  new Intent(getApplicationContext(), LoginScreen.class);
        startActivity(intent);
        finish();
    }

    public void callStartupScreen(View view){
        Intent intent =  new Intent(getApplicationContext(), StartupScreen.class);
        startActivity(intent);
        finish();
    }
}