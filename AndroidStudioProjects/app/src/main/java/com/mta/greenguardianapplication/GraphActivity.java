package com.mta.greenguardianapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class GraphActivity extends AppCompatActivity {

    ArrayList<BarEntry> barArrayList = new ArrayList<BarEntry>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        barArrayList.add(new BarEntry(0, 0));
        setContentView(R.layout.activity_graph);
    }

    @Override
    protected void onResume() {
        super.onResume();
        BarChart barChart = findViewById(R.id.barchart);
        barArrayList = new ArrayList<BarEntry>();
        getData();
        BarDataSet barDataSet = new BarDataSet(barArrayList, "Graph");
        BarData barData = new BarData(barDataSet);
        barChart.setData(barData);
        barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(16f);
        barChart.getDescription().setEnabled(true);
        barChart.invalidate();
    }

    private void getData() {
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("plantId") && intent.hasExtra("userId")) {
            String plantId = intent.getStringExtra("plantId");
            String userId = intent.getStringExtra("userId");
            getDataFromFirebase(plantId, userId);
        } else {
            // Handle the case when there's no data passed.
            // For example, you can initialize barArrayList with some default data.
            barArrayList = new ArrayList<>();
        }
    }

    private void getDataFromFirebase(String plantId, String userId) {
        DatabaseReference statsHumidityRef = FirebaseDatabase.getInstance()
                .getReference("Users")
                .child(FirebaseAuth.getInstance().getUid())
                .child("Plants")
                .child(plantId)
                .child("statsHumidity");
        Query query = statsHumidityRef.orderByKey();
        statsHumidityRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Long> statsHumidityList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Cast the data to the appropriate type (Long in this case)
                    Long humidityValue = snapshot.getValue(Long.class);
                    statsHumidityList.add(humidityValue);
                }

                // Now you have the ArrayList of statsHumidity data
                // You can use statsHumidityList as needed
                // For example, update the BarChart with this data
                updateBarChart(statsHumidityList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors that occurred during data retrieval
            }
        });

    }

    private void updateBarChart(List<Long> statsHumidityList) {
        // Convert the List<Long> to List<BarEntry> for the bar chart
        List<BarEntry> barArrayList = new ArrayList<>();
        for (int i = 0; i < statsHumidityList.size(); i++) {
            barArrayList.add(new BarEntry(i, statsHumidityList.get(i)));
        }
    }
}