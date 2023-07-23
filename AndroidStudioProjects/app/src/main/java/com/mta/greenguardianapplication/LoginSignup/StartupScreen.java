package com.mta.greenguardianapplication.LoginSignup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.Manifest;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.mta.greenguardianapplication.R;


public class StartupScreen extends AppCompatActivity {
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // Permission granted. You can now handle the notification logic here
                    Toast.makeText(this, "Notification permission granted!", Toast.LENGTH_SHORT).show();
                } else {
                    // Permission denied or not granted. Handle the scenario accordingly.
                    Toast.makeText(this, "Notification permission denied!", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseMessaging firebaseMessaging = FirebaseMessaging.getInstance();
        askNotificationPermission();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_startup_screen);
    }

    public void callLoginScreen(View view) {

        Intent intent = new Intent(getApplicationContext(), LoginScreen.class);
        startActivity(intent);
        finish();
    }

    public void callSignupScreen(View view) {

        Intent intent = new Intent(getApplicationContext(), SignupScreen.class);
        startActivity(intent);
        finish();
    }

    public void callToken(View view) {

        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        Log.d("greenGuardian", s);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "fail to create Token", Toast.LENGTH_SHORT).show();
                });
    }

    private void askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                    PackageManager.PERMISSION_GRANTED) {
                // Permission already granted. You can proceed with the notification logic.
                Toast.makeText(this, "Notification permission already granted!", Toast.LENGTH_SHORT).show();
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                showEducationalUI();
                Toast.makeText(this, "Permission rationale required!", Toast.LENGTH_SHORT).show();
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }

    }

    private void showEducationalUI() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle("Enable Notifications");
        dialogBuilder.setMessage("By enabling notifications, you will receive important updates and alerts in real-time. Stay informed and never miss any critical information.");
        dialogBuilder.setPositiveButton("OK", (dialog, which) -> {
            // User clicked "OK," request the permission directly
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
        });
        dialogBuilder.setNegativeButton("No thanks", (dialog, which) -> {
            // User clicked "No thanks," continue without notifications
            dialog.dismiss();
            // TODO: Implement the logic to continue without notifications.
        });
        AlertDialog dialog = dialogBuilder.create();
        dialog.show();
    }
}