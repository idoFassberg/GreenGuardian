package com.mta.greenguardianapplication;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mta.greenguardianapplication.LoginSignup.StartupScreen;
import com.mta.greenguardianapplication.model.UserPlant;

public class AddUserPlantForm extends AppCompatActivity {
    private DatabaseReference mDatabase;
    DrawerLayout drawerLayout;
    ImageView menu;
    LinearLayout myPlants, forum, logout,myProfile,addPlant;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.form_add_user_plant);
        String plantType = getIntent().getStringExtra("plantType");
        String optimalHumidity = getIntent().getStringExtra("optimalHumidity");
        String pictureUrl = getIntent().getStringExtra("pictureUrl");
        String boardId = getIntent().getStringExtra("boardId");
        String nickName = getIntent().getStringExtra("nickName");

        drawerLayout = findViewById(R.id.drawerLayout);
        menu = findViewById(R.id.menu);
        myPlants = findViewById(R.id.myPlants);
        forum = findViewById(R.id.forum);
        logout = findViewById(R.id.logoutNav);
        myProfile = findViewById(R.id.my_profile);
        addPlant = findViewById(R.id.add_plant);


        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDrawer(drawerLayout);
            }
        });

        forum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                redirectActivity(AddUserPlantForm.this, ForumActivity2.class);
            }
        });

        myPlants.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                redirectActivity(AddUserPlantForm.this, UserPlantListActivity.class);
            }
        });

        myProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(AddUserPlantForm.this,ProfileActivity.class);
            }
        });
        addPlant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recreate();
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(AddUserPlantForm.this, "Logout", Toast.LENGTH_SHORT).show();
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), StartupScreen.class); //needs to change
                startActivity(intent);
                finish();
            }
        });

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
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference("Users");
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String userId = mAuth.getCurrentUser().getUid();

// Set the plant data at the generated key under the user's "plants" node
        usersRef.child(userId).child("plants").child(plantId).setValue(userPlant)
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

    public  static void openDrawer(DrawerLayout drawerLayout){
        drawerLayout.openDrawer(GravityCompat.START);
    }

    public static void closeDrawer(DrawerLayout drawerLayout){
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    public static void redirectActivity(Activity activity, Class secondActivity){
        Intent intent = new Intent(activity, secondActivity);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
        activity.finish();
    }
}