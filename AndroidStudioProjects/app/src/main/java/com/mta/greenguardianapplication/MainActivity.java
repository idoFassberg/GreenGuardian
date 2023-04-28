package com.mta.greenguardianapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //mDatabase = FirebaseDatabase.getInstance().getReference();

    }
    public void onCLICKactivity(View v){
        Intent intent = new Intent("com.mta.greenguardianapplication.AddImageForm");
        startActivity(intent);
    }
}