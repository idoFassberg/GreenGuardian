package com.mta.clientapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseApp;
import com.mta.clientapplication.model.UserPlant;
import com.mta.clientapplication.retrofit.RetrofitService;
import com.mta.clientapplication.retrofit.UserPlantApi;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.HttpException;
import retrofit2.Response;

public class AddUserPlantForm extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.form_add_user_plant);
        initializeComponents();
    }

    private void initializeComponents(){
        TextInputEditText inputEditUserId = findViewById(R.id.form_textFieldUserId);
        TextInputEditText inputEditPlantName = findViewById(R.id.form_textFieldPlantName);
        TextInputEditText inputEditNickName = findViewById(R.id.form_textFieldNickName);
        MaterialButton buttonSaveUserPlant = findViewById(R.id.form_buttonSaveUserPlant);

        RetrofitService retrofitService = new RetrofitService();
        UserPlantApi userPlantApi = retrofitService.getRetrofit().create(UserPlantApi.class);

        buttonSaveUserPlant.setOnClickListener(view -> {
            String userId = String.valueOf(inputEditUserId.getText());
            String plantName = String.valueOf(inputEditPlantName.getText());
            String nickName = String.valueOf(inputEditNickName.getText());

            UserPlant userPlant = new UserPlant();
            userPlant.setUserId(Integer.parseInt(userId)); //check for errors
            userPlant.setPlantName(plantName);
            userPlant.setNickName(nickName);
            userPlant.setPlantId(121111);
            userPlantApi.saveUserPlant(userPlant)
                    .enqueue(new Callback<UserPlant>() {
                        @Override
                        public void onResponse(Call<UserPlant> call, Response<UserPlant> response) {
                            System.out.printf("shay 2 2 2 here");
                            Toast.makeText(AddUserPlantForm.this, "Save successful", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(Call<UserPlant> call, Throwable t) {
                            Toast.makeText(AddUserPlantForm.this, "Save failed!!!", Toast.LENGTH_SHORT).show();
                            Logger.getLogger(AddUserPlantForm.class.getName()).log(Level.SEVERE, "Error while saving user plant", t);
                            Log.e("AddUserPlantForm", "Error: " + t.getMessage(), t);
                            if (t instanceof HttpException) {
                                HttpException error = (HttpException) t;
                                try {
                                    String errorBody = error.response().errorBody().string();
                                    Log.e("AddUserPlantForm", "Error body: " + errorBody);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                    });
        });
    }
}