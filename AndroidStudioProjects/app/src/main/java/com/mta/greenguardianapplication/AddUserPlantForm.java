package com.mta.greenguardianapplication;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mta.greenguardianapplication.model.UserPlant;

import java.util.logging.Level;
import java.util.logging.Logger;

public class AddUserPlantForm extends AppCompatActivity {
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.form_add_user_plant);
        mDatabase = FirebaseDatabase.getInstance().getReference("Plants");
        initializeComponents();
    }

    private void initializeComponents() {
        TextInputEditText inputEditUserId = findViewById(R.id.form_textFieldUserId);
        TextInputEditText inputEditPlantName = findViewById(R.id.form_textFieldPlantName);
        TextInputEditText inputEditNickName = findViewById(R.id.form_textFieldNickName);
        MaterialButton buttonSaveUserPlant = findViewById(R.id.form_buttonSaveUserPlant);

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