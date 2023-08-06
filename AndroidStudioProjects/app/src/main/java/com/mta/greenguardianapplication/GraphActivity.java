package com.mta.greenguardianapplication;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mta.greenguardianapplication.LoginSignup.StartupScreen;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GraphActivity extends AppCompatActivity {

    ArrayList<BarEntry> barArrayList = new ArrayList<BarEntry>();

    DrawerLayout drawerLayout;
    ImageView menu;
    LinearLayout myPlants, forum, logout, plantsLibrary,addPlant,myProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*barArrayList.add(new BarEntry(1, 10));
        barArrayList.add(new BarEntry(2, 20));
        barArrayList.add(new BarEntry(3, 30));
        barArrayList.add(new BarEntry(4, 40));
        barArrayList.add(new BarEntry(5, 50));*/
        setContentView(R.layout.activity_graph);

        drawerLayout = findViewById(R.id.drawerLayout);
        menu = findViewById(R.id.menu);
        myPlants = findViewById(R.id.myPlants);
        forum = findViewById(R.id.forum);
        logout = findViewById(R.id.logoutNav);
        plantsLibrary = findViewById(R.id.plantsLibrary);
        addPlant = findViewById(R.id.add_plant);
        myProfile = findViewById(R.id.my_profile);

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDrawer(drawerLayout);
            }
        });

        plantsLibrary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                redirectActivity(GraphActivity.this, PlantListActivity.class);
            }
        });

        myPlants.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                redirectActivity(GraphActivity.this, UserPlantListActivity.class);
            }
        });

        forum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                redirectActivity(GraphActivity.this, ForumActivity2.class);
            }
        });
        myProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(GraphActivity.this,ProfileActivity.class);
            }
        });

        addPlant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(GraphActivity.this,AddUserPlantForm.class);            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(GraphActivity.this, "Logout", Toast.LENGTH_SHORT).show();
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), StartupScreen.class); //needs to change
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        BarChart barChart = findViewById(R.id.barchart);
        //barArrayList = new ArrayList<BarEntry>();
        getData();
        BarDataSet barDataSet = new BarDataSet(barArrayList, "Graph");
        BarData barData = new BarData(barDataSet);
        barChart.setData(barData);
        barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(16f);
        barChart.getDescription().setEnabled(true);
        barChart.invalidate();
        //setContentView(R.layout.activity_graph);
    }

    private void getData() {
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("plantId") && intent.hasExtra("userId")) {
            String plantId = intent.getStringExtra("plantId");
            String userId = intent.getStringExtra("userId");
            int optimalHumidity = intent.getIntExtra("optimalHumidity", 70);
            String nickName = intent.getStringExtra("nickName");
            getDataFromFirebase(nickName, userId, optimalHumidity);
        } else {
            // Handle the case when there's no data passed.
            // For example, you can initialize barArrayList with some default data.
            barArrayList = new ArrayList<>();
        }
    }

    private void getDataFromFirebase(String nickName, String userId,int optimalHumidity) {

        DatabaseReference statsHumidityRef = FirebaseDatabase.getInstance()
                .getReference("Users")
                .child(FirebaseAuth.getInstance().getUid())
                .child("plants")
                .child(nickName)
                .child("statsHumidity");

        Log.d("status", statsHumidityRef.getKey());
        //Query query = statsHumidityRef.orderByKey();
        statsHumidityRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Long> statsHumidityList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Cast the data to the appropriate type (Long in this case)
                    Long humidityValue = snapshot.getValue(Long.class);
                    String date = snapshot.getKey();
                    statsHumidityList.add(humidityValue);
                }

                // Now you have the ArrayList of statsHumidity data
                // You can use statsHumidityList as needed
                // For example, update the BarChart with this data
                updateBarChart(statsHumidityList, optimalHumidity);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors that occurred during data retrieval
            }
        });

    }

    private void updateBarChart(List<Long> statsHumidityList, int optimalHumidity) {
        // Clear the previous data
        barArrayList.clear();

        // Convert the List<Long> to List<BarEntry> for the bar chart
        for (int i = 0; i < statsHumidityList.size(); i++) {
            barArrayList.add(new BarEntry(i, statsHumidityList.get(i)));
        }

        BarChart barChart = findViewById(R.id.barchart);
        BarDataSet barDataSet = new BarDataSet(barArrayList, "Graph");

        ArrayList<Integer> colors = new ArrayList<>();

        // Set colors based on humidity level similar to progress bar colors
        for (Long humidityValue : statsHumidityList) {
            int progressPercentage = (int) ((humidityValue * 100.0f) / optimalHumidity);

            if (progressPercentage < 30) {
                colors.add(Color.RED);
            } else if (progressPercentage < 70) {
                colors.add(Color.parseColor("#FFA500")); // Orange
            } else {
                colors.add(Color.parseColor("#A4C639")); // Green
            }
        }

        barDataSet.setColors(colors);

        BarData barData = new BarData(barDataSet);
        barChart.setData(barData);
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(16f);
        barChart.getDescription().setEnabled(true);
        barChart.invalidate();
    }


    public  static void openDrawer(DrawerLayout drawerLayout){
        drawerLayout.openDrawer(GravityCompat.START);
    }

    public static void closeDrawer(DrawerLayout drawerLayout){
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    public  static void redirectActivity(Activity activity, Class secondActivity){
        Intent intent = new Intent(activity, secondActivity);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
        activity.finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        closeDrawer(drawerLayout);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}