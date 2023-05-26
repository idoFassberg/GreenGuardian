package com.mta.greenguardianapplication;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mta.greenguardianapplication.model.UserPlant;

public class AddUserPlantForm extends AppCompatActivity {
    private DatabaseReference mDatabase;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.form_add_user_plant);
        String plantType = getIntent().getStringExtra("plantType");
        String optimalHumidity = getIntent().getStringExtra("optimalHumidity");
        String pictureUrl = getIntent().getStringExtra("pictureUrl");
        String boardId = getIntent().getStringExtra("boardId");
        String nickName = getIntent().getStringExtra("nickName");

        // Assign default values if the extras are null
        plantType = plantType != null ? plantType : "";
        optimalHumidity = optimalHumidity != null ? optimalHumidity : "";
        pictureUrl = pictureUrl != null ? pictureUrl : "";
        boardId = boardId != null ? boardId : "";
        nickName = nickName != null ? nickName : "";
        mDatabase = FirebaseDatabase.getInstance().getReference("UserPlants");
        initializeComponents(plantType, optimalHumidity,pictureUrl,boardId,nickName);
    }

    private void initializeComponents(String plantType, String optimalHumidity, String pictureUrl, String boardId, String nickName) {
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
        inputEditBoardId.setText(boardId);
        inputEditNickName.setText(nickName);

        buttonSaveUserPlant.setOnClickListener(view -> {
            String type = String.valueOf(inputEditPlantType.getText());
            String nickNameStr = String.valueOf(inputEditNickName.getText());
            String optimalHumidityStr = String.valueOf(inputEditOptimalHumidity.getText());
            String boardIdStr = String.valueOf(inputEditBoardId.getText());

            addNewUserPlant(type, nickNameStr,Integer.parseInt(optimalHumidityStr),boardIdStr, (String)imageView.getTag());

            Intent intent = new Intent(AddUserPlantForm.this, UserPlantListActivity.class);
            startActivity(intent);
        });
    }

    public void addNewUserPlant(String type, String nickName, int optimalHumidity, String boardId, String pictureUrl) {
        String plantId = getIntent().getStringExtra("plantId");
        if(plantId == null){
            plantId = mDatabase.push().getKey();
        }
        UserPlant userPlant = new UserPlant(plantId, nickName, optimalHumidity, pictureUrl, type, "123", boardId,-1 );
        mDatabase.child(plantId).setValue(userPlant); // Save the plant with the generated ID

        Toast.makeText(AddUserPlantForm.this, "Save successful", Toast.LENGTH_SHORT).show();
    }
}