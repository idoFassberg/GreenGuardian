package com.mta.greenguardianapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.auth.User;

public class MainActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private Button openAddPlant;
    private Button viewListPlants;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        openAddPlant = (Button)findViewById(R.id.openAddUserForm);
        viewListPlants = (Button)findViewById(R.id.openListPlants);

        openAddPlant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,AddUserPlantForm.class);
                startActivity(intent);
            }
        });

        viewListPlants.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, UserPlantListActivity.class);
                startActivity(intent);
            }
        });
    }

}