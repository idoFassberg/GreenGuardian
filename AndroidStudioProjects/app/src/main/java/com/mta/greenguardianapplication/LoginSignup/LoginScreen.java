package com.mta.greenguardianapplication.LoginSignup;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mta.greenguardianapplication.R;
import com.mta.greenguardianapplication.UserPlantListActivity;


public class LoginScreen extends AppCompatActivity {

    private FirebaseAuth mAuth;

    Button login, signup;
    TextInputEditText te_emailUser,te_passwordUser;
    ProgressBar progressBar;
    TextView forgotPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login_screen);

        login = findViewById(R.id.loginBtn);
        signup = findViewById(R.id.signup_btn);
        te_emailUser = findViewById(R.id.userNameLogin);
        te_passwordUser = findViewById(R.id.passwordLogin);
        forgotPassword = findViewById(R.id.forgotPassword);
        /*progressBar = findViewById(R.id.progressBarLogin);*/

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder= new AlertDialog.Builder(LoginScreen.this);
                View dialogView = getLayoutInflater().inflate(R.layout.dialog_forgot_password, null);
                TextInputEditText emailBox = dialogView.findViewById(R.id.emailBox);

                builder.setView(dialogView);
                AlertDialog dialog = builder.create();

                dialogView.findViewById(R.id.resetPasswordBtn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String userEmail = emailBox.getText().toString();

                        if (TextUtils.isEmpty(userEmail) && !Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()){
                            Toast.makeText(LoginScreen.this, "Enter your registered email", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        mAuth.sendPasswordResetEmail(userEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    Toast.makeText(LoginScreen.this, "Check your email", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                } 
                                else{
                                    Toast.makeText(LoginScreen.this, "Unable to send, failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
                if (dialog.getWindow() != null){
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                }
                dialog.show();
            }
        });
    }

    public void callStartupScreen(View view){
        Intent intent =  new Intent(getApplicationContext(), StartupScreen.class);
        startActivity(intent);
        finish();
    }

    public void callSignupScreen(View view){
        Intent intent = new Intent(getApplicationContext(), SignupScreen.class);
        startActivity(intent);
        finish();
    }

    public void onLoginClick(View view) {
        String email = te_emailUser.getText().toString();
        String password = te_passwordUser.getText().toString();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
/*
                            progressBar.setVisibility(View.GONE);
*/
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            Toast.makeText(LoginScreen.this, "Login Successful.",
                                    Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), UserPlantListActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginScreen.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void onClickLogout(View view) {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getApplicationContext(), StartupScreen.class); //needs to change
        startActivity(intent);
        finish();
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent intent = new Intent(getApplicationContext(), StartupScreen.class); //needs to change
            startActivity(intent);
            finish();
        }
    }

    public void callLoginScreen(View view){

        Intent intent =  new Intent(getApplicationContext(), LoginScreen.class);
        startActivity(intent);
        finish();
    }
}