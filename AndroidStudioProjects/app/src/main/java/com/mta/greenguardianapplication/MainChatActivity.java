package com.mta.greenguardianapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;

import com.firebase.ui.auth.data.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mta.greenguardianapplication.databinding.ActivityMainChatBinding;
import com.mta.greenguardianapplication.utilities.Constants;
import com.mta.greenguardianapplication.utilities.PreferenceManager;

import kotlin.jvm.internal.PackageReference;

public class MainChatActivity extends AppCompatActivity {

    private ActivityMainChatBinding binding;
    private PreferenceManager preferenceManager;
    private FirebaseUser user;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        user = mAuth.getCurrentUser();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid());
        loadUserDetails(userRef);
    }


    private void loadUserDetails(DatabaseReference userRef){
        /*binding.textName.setText(userRef.user);*/
        binding.textName.setText(preferenceManager.getString(Constants.KEY_NAME));
        /*byte[] bytes = Base64.decode(preferenceManager.getString(Constants.KEY_IMAGE), Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        binding.imageProfile.setImageBitmap(bitmap);*/
    }
}