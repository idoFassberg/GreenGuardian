package com.mta.greenguardianapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
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
        mDatabase = FirebaseDatabase.getInstance().getReference("UserPlants");
        initializeComponents(plantType, optimalHumidity,pictureUrl);
    }

    private void initializeComponents(String plantType,String optimalHumidity,String pictureUrl) {
        TextInputEditText inputEditPlantType = findViewById(R.id.form_textFieldPlantType);
        TextInputEditText inputEditOptimalHumidity = findViewById(R.id.form_textFieldOptimalHumidity);
        TextInputEditText inputEditNickName = findViewById(R.id.form_textFieldNickName);
        TextInputEditText inputEditBoardId = findViewById(R.id.form_textFieldBoardId);
        ImageView imageView = findViewById(R.id.plant_image);
        MaterialButton buttonSaveUserPlant = findViewById(R.id.form_buttonSaveUserPlant);
        if(pictureUrl != "") {
            Glide.with(imageView.getContext())
                    .load(pictureUrl)
                    .apply(new RequestOptions().circleCrop())
                    .into(imageView);
        }
        imageView.setTag(pictureUrl);
        inputEditPlantType.setText(plantType);
        inputEditOptimalHumidity.setText(optimalHumidity);

        buttonSaveUserPlant.setOnClickListener(view -> {
            String type = String.valueOf(inputEditPlantType.getText());
            String nickName = String.valueOf(inputEditNickName.getText());
            String optimalHumidityStr = String.valueOf(inputEditOptimalHumidity.getText());
            String boardId = String.valueOf(inputEditBoardId.getText());

            addNewUserPlant(type, nickName,Integer.parseInt(optimalHumidityStr),boardId, (String)imageView.getTag());

            Intent intent = new Intent(AddUserPlantForm.this, UserPlantListActivity.class);
            startActivity(intent);
        });
    }

    public void addNewUserPlant(String type, String nickName, int optimalHumidity, String boardId, String pictureUrl) {
        UserPlant userPlant = new UserPlant(nickName, optimalHumidity, pictureUrl, type, "123", boardId,-1 );

        String plantId = mDatabase.push().getKey(); // Generate a unique ID

        mDatabase.child(plantId).setValue(userPlant); // Save the plant with the generated ID
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference("Users");
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String userId = mAuth.getCurrentUser().getUid();
        String plantKey = usersRef.child(userId).child("plants").push().getKey();

// Set the plant data at the generated key under the user's "plants" node
        usersRef.child(userId).child("plants").child(plantKey).setValue(userPlant)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Plant added successfully
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Failed to add the plant
                    }
                });

        Toast.makeText(AddUserPlantForm.this, "Save successful", Toast.LENGTH_SHORT).show();
    }
}