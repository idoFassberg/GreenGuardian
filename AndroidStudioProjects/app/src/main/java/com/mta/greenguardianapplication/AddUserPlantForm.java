package com.mta.greenguardianapplication;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mta.greenguardianapplication.model.UserPlant;

public class AddUserPlantForm extends AppCompatActivity {
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.form_add_user_plant);
        String plantType = getIntent().getStringExtra("plantType");
        String optimalHumidity = getIntent().getStringExtra("optimalHumidity");
        String pictureUrl = getIntent().getStringExtra("pictureUrl");

        // Assign default values if the extras are null
        plantType = plantType != null ? plantType : "";
        optimalHumidity = optimalHumidity != null ? optimalHumidity : "";
        pictureUrl = pictureUrl != null ? pictureUrl : "";
        mDatabase = FirebaseDatabase.getInstance().getReference("UserPlant");
        initializeComponents(plantType, optimalHumidity,pictureUrl);
    }

    private void initializeComponents(String plantType,String optimalHumidity,String pictureUrl) {
        TextInputEditText inputEditUserId = findViewById(R.id.form_textFieldPlantType);
        TextInputEditText inputEditPlantName = findViewById(R.id.form_textFieldOptimalHumidity);
        TextInputEditText inputEditNickName = findViewById(R.id.form_textFieldNickName);
        ImageView pictureView = findViewById(R.id.plant_image);
        MaterialButton buttonSaveUserPlant = findViewById(R.id.form_buttonSaveUserPlant);
        if(pictureUrl != "") {
            Glide.with(pictureView.getContext())
                    .load(pictureUrl)
                    .apply(new RequestOptions().circleCrop())
                    .into(pictureView);
        }
        inputEditUserId.setText(plantType);
        inputEditPlantName.setText(optimalHumidity);

        buttonSaveUserPlant.setOnClickListener(view -> {
            String userId = String.valueOf(inputEditUserId.getText());
            String plantName = String.valueOf(inputEditPlantName.getText());
            String nickName = String.valueOf(inputEditNickName.getText());

            writeNewUser("12345",userId, plantName, nickName);

        });
    }
    public void writeNewUser(String plantId, String userId, String plantName, String nickName) {
        UserPlant userPlant = new UserPlant(plantId, userId, plantName, nickName);

        mDatabase.child(plantName).setValue(userPlant);
        //mDatabase.push().setValue(userPlant);
        Toast.makeText(AddUserPlantForm.this, "Save successful", Toast.LENGTH_SHORT).show();
    }
}