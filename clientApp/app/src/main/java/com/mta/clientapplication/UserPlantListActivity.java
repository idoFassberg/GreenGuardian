package com.mta.clientapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mta.clientapplication.adapter.UserPlantAdapter;
import com.mta.clientapplication.model.UserPlant;
import com.mta.clientapplication.retrofit.RetrofitService;
import com.mta.clientapplication.retrofit.UserPlantApi;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserPlantListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_plant_list);

        recyclerView = findViewById(R.id.userPlantList_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FloatingActionButton floatingActionButton = findViewById(R.id.userPlantList_fab);
        floatingActionButton.setOnClickListener(view -> {
            Intent intent = new Intent(this, AddUserPlantForm.class);
            startActivity(intent);
        });
        loadUserPlants();
    }

    private void loadUserPlants() {
        RetrofitService retrofitService = new RetrofitService();
        UserPlantApi userPlantApi = retrofitService.getRetrofit().create(UserPlantApi.class);
        userPlantApi.getAllUserPlants()
                .enqueue(new Callback<List<UserPlant>>() {
                    @Override
                    public void onResponse(Call<List<UserPlant>> call, Response<List<UserPlant>> response) {
                        populateListView(response.body());
                    }

                    @Override
                    public void onFailure(Call<List<UserPlant>> call, Throwable t) {
                        Toast.makeText(UserPlantListActivity.this, "Failed to load plants!", Toast.LENGTH_SHORT).show();
                        //Logger.getLogger()
                    }
                });
    }

    private void populateListView(List<UserPlant> plantList) {
        UserPlantAdapter userPlantAdapter = new UserPlantAdapter(plantList);
        recyclerView.setAdapter(userPlantAdapter);
    }
}