package com.mta.greenguardianapplication;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mta.greenguardianapplication.LoginSignup.SignupScreen;
import com.mta.greenguardianapplication.LoginSignup.StartupScreen;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser user;

    StorageReference storageReference;
    String storagePath = "Users_Profile_Img/";
    //Button btn_back;
    TextView userName,userEmail;
    DrawerLayout drawerLayout;
    ImageView menu,profilePicture;
    LinearLayout myPlants, chat, logout,addPlant,myProfile,plantsLibrary;
    AppCompatButton btn_accountSetting;
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;
    private static final int IMAGE_PICK_GALLERY_CODE = 300;
    private static final int IMAGE_PICK_CAMERA_CODE = 400;
    private static final int STORAGE_PERMISSION_REQUEST_CODE = 1001;

    String cameraPermission[];
    String storagePermission[];

    private ActivityResultLauncher<Uri> cameraLauncher;
    private ActivityResultLauncher<String> galleryLauncher;


    Uri image_uri;
    private AlertDialog progressDialog;
    //ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        //ActionBar actionBar = getSupportActionBar();
        //actionBar.setTitle("Profile");
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        userName = findViewById(R.id.profileName);
        userEmail = findViewById(R.id.EmailUser);
        btn_accountSetting = findViewById(R.id.buttonAccountSetting);
        //ProgressBar progressBar = new ProgressBar(ProfileActivity.this);
        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storageReference = FirebaseStorage.getInstance().getReference();

        //btn_back = findViewById(R.id.back_btn_profile);

        drawerLayout = findViewById(R.id.drawerLayout);
        menu = findViewById(R.id.menu);
        myPlants = findViewById(R.id.myPlants);
        chat = findViewById(R.id.chat);
        logout = findViewById(R.id.logoutNav);
        plantsLibrary = findViewById(R.id.plantsLibrary);
        addPlant = findViewById(R.id.add_plant);
        myProfile = findViewById(R.id.my_profile);
        profilePicture = findViewById(R.id.imageView2);

        cameraLauncher = registerForActivityResult(new ActivityResultContracts.TakePicture(), result -> {
            if (result) {
                // Image capture successful, handle the image URI here
                uploadProfilePicture(image_uri);
            } else {
                // Image capture canceled or failed, handle accordingly
                // For example, show a Toast or perform some action
            }
        });

        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri result) {
                        if (result != null) {
                            // Image selection successful, handle the image URI here
                            handleImageCapture(IMAGE_PICK_GALLERY_CODE, result); // Add the requestCode to the handleImageCapture call
                        } else {
                            Toast.makeText(ProfileActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            // Image selection canceled or failed, handle accordingly
                            // For example, show a Toast or perform some action
                        }
                    }
                }
        );


        // In your Java code, after finding the TextView by its ID
        //TextView countOfPlantsTextView = findViewById(R.id.countOfPlants);
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid());

        userRef.child("image").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Get the download URL of the profile picture from the database
                    String profileImageUrl = dataSnapshot.getValue(String.class);

                    if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                        // Load and display the profile picture using Picasso
                        // Load and display the profile picture using Picasso
                        Picasso.get()
                                .load(profileImageUrl)
                                .placeholder(R.drawable.ic_account_profile2) // Placeholder image while loading
                                .error(R.drawable.ic_account_profile2) // Error image if the URL is invalid or loading fails
                                .into(profilePicture);
                    } else {
                        // If the "image" value is empty or null, set a default profile picture
                        profilePicture.setImageResource(R.drawable.ic_account_profile2);
                    }

                } else {
                    // If the "image" value does not exist in the database, handle the case here
                    // For example, you can set a default profile picture
                    profilePicture.setImageResource(R.drawable.ic_account_profile2);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle the error case if the database operation is cancelled
            }
        });
        DatabaseReference userPlantRef = userRef.child("plants");

        userPlantRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // dataSnapshot.getChildrenCount() gives you the number of children (plants)
                    long plantCount = dataSnapshot.getChildrenCount();
                    // Update the TextView with the plant count
                    TextView countOfPlantsTextView = findViewById(R.id.countOfPlants);
                    countOfPlantsTextView.setText("Have " + plantCount + " plants");
                } else {
                    // Handle the case where there are no plants
                    // For example, display "No plants" in the TextView
                    TextView countOfPlantsTextView = findViewById(R.id.countOfPlants);
                    countOfPlantsTextView.setText("No plants");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle the error case if the database operation is cancelled
            }
        });
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDrawer(drawerLayout);
            }
        });

        myPlants.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                redirectActivity(ProfileActivity.this, UserPlantListActivity.class);
            }
        });

        plantsLibrary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                redirectActivity(ProfileActivity.this, PlantListActivity.class);
            }
        });

        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                redirectActivity(ProfileActivity.this, MainChatActivity.class);
            }
        });

        addPlant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(ProfileActivity.this,AddUserPlantForm.class);
            }
        });

        myProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recreate();
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ProfileActivity.this, "Logout", Toast.LENGTH_SHORT).show();
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), StartupScreen.class); //needs to change
                startActivity(intent);
                finish();
            }
        });

        btn_accountSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditProfileDialog();
            }
        });

    }

    private void uploadProfilePicture(Uri image_uri) {
        String filePathAndName = storagePath + user.getUid();
        StorageReference storageReference1 = storageReference.child(filePathAndName);

        // Add an OnSuccessListener to handle the success case
        storageReference1.putFile(image_uri)
                .addOnSuccessListener(taskSnapshot -> {
                    // The image upload is successful, you can get the download URL here if needed
                    storageReference1.getDownloadUrl().addOnSuccessListener(uri -> {
                        // uri contains the download URL of the uploaded image, you can save it to the database or use it as needed
                        String downloadUrl = uri.toString();
                        // For example, you can save the download URL to the user's profile in the database
                        saveImageUrlToDatabase(downloadUrl);

                        // Dismiss the progress dialog once the update process is complete
                        progressDialog.dismiss();
                    });
                })
                .addOnFailureListener(exception -> {
                    // The image upload failed, handle the failure case here
                    // For example, show a Toast or perform some action

                    // Dismiss the progress dialog in the failure case as well
                    progressDialog.dismiss();
                });
    }
    private void saveImageUrlToDatabase(String downloadUrl) {
        // Get the current user's ID
        String userId = user.getUid();

        // Get a reference to the "Users" node in the database
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        // Get a reference to the specific user's profile in the database
        DatabaseReference userRef = usersRef.child(userId);

        // Update the "profilePictureUrl" field in the user's profile with the download URL
        userRef.child("image").setValue(downloadUrl)
                .addOnSuccessListener(aVoid -> {
                    // Profile picture URL is successfully saved in the database
                    Toast.makeText(ProfileActivity.this, "Profile picture updated successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    // There was an error saving the profile picture URL
                    Toast.makeText(ProfileActivity.this, "Failed to update profile picture", Toast.LENGTH_SHORT).show();
                });
    }



    private void showEditProfileDialog() {
        String option[] = {"Edit profile picture", "Edit name"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Action");
        builder.setItems(option, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) { // Edit profile picture
                    showCustomProgressDialog("Updating profile picture...");
                    showEditImageDialog();
                } else if (which == 1) { // Edit Name
                    showEditNameDialog();
                }
            }
        });
        builder.show();
    }

    private void showEditImageDialog() {
        String[] options = {"Camera", "Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick Image From");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) { // Camera
                    if (!checkCameraPermission()) {
                        Log.d("permission ","ask for camera permission");
                        requestCameraPermission();
                        progressDialog.dismiss();
                    } else {
                        Log.d("permission ","the user have camera permission! try open camera");
                        pickFromCamera();
                        progressDialog.dismiss();

                    }
                } else if (which == 1) { // Gallery
                    if (!checkStoragePermission()) {
                        requestStoragePermission();
                    } else {
                        pickFromGallery();
                    }
                }
            }
        });
        builder.show();
    }

    private void showEditNameDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Name");

        // Create an EditText to input the new name
        final EditText input = new EditText(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        builder.setView(input);

        // Set the positive button to update the name
        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newName = input.getText().toString().trim();
                if (!newName.isEmpty()) {
                    updateUserName(newName);
                } else {
                    Toast.makeText(ProfileActivity.this, "Name cannot be empty", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Set the negative button to cancel the dialog
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Show the dialog
        builder.show();
    }

    private void updateUserName(String newName) {
        // Get the current user's ID
        String userId = user.getUid();

        // Get a reference to the "Users" node in the database
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        // Get a reference to the specific user's profile in the database
        DatabaseReference userRef = usersRef.child(userId);

        // Update the "name" field in the user's profile with the new name
        userRef.child("name").setValue(newName)
                .addOnSuccessListener(aVoid -> {
                    // Name is successfully updated in the database
                    Toast.makeText(ProfileActivity.this, "Name updated successfully", Toast.LENGTH_SHORT).show();

                    // Update the UI with the new name
                    userName.setText(newName);
                })
                .addOnFailureListener(e -> {
                    // There was an error updating the name
                    Toast.makeText(ProfileActivity.this, "Failed to update name", Toast.LENGTH_SHORT).show();
                });
    }



    private void showCustomProgressDialog(String message) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.progress_dialog_layout, null);
        TextView progressText = view.findViewById(R.id.progressText);
        progressText.setText(message);
        dialogBuilder.setView(view);
        dialogBuilder.setCancelable(false);
        AlertDialog progressDialog = dialogBuilder.create();
        progressDialog.show();

        // Store the progressDialog instance as a member variable to use it later
        this.progressDialog = progressDialog;
    }

    private boolean checkStoragePermission() {
        // Check if the device is running Android 6.0 (Marshmallow) or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Check if the storage permission is already granted
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                // Storage permission is already granted
                return true;
            } else {
                // Storage permission is not granted, request it
                requestStoragePermission();
                return false;
            }
        } else {
            // If the device is running below Android 6.0, permission is granted by default
            return true;
        }
    }

    // Request the storage permission
    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                STORAGE_PERMISSION_REQUEST_CODE);
    }


    private boolean checkCameraPermission() {
        // Check if the device is running Android 6.0 (Marshmallow) or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Check if the camera permission is already granted
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED) {
                // Camera and storage permissions are already granted
                return true;
            } else {
                // Camera permission is not granted, request it
                requestCameraPermission();
                return false;
            }
        } else {
            // If the device is running below Android 6.0, permission is granted by default
            return true;
        }
    }
    // Request the camera permission
    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this,
                cameraPermission,
                CAMERA_REQUEST_CODE);
    }

    // Handle the permission request result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Storage permission is granted, proceed with image pick from gallery
                pickFromGallery();
            } else {
                // Storage permission is denied, show a message or perform some action
                Toast.makeText(ProfileActivity.this, "Storage permission denied", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == CAMERA_REQUEST_CODE) {
            pickFromCamera();
        }
    }


    private void pickFromGallery() {
        // Create a new ActivityResultLauncher for picking an image from the gallery
        // Start the gallery activity using the galleryLauncher
        galleryLauncher.launch("image/*");
    }

    private void pickFromCamera() {
        // Create the camera intent
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Check if there's a camera app available to handle the intent
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            // Launch the camera activity and pass null as the argument
            cameraLauncher.launch(null);
        } else {
            // If there's no camera app available, show a message to the user or handle the situation accordingly
            Toast.makeText(this, "No camera app found", Toast.LENGTH_SHORT).show();
        }
    }




    @Override
    protected void onStart() {
        super.onStart();
        checkUserStatus();
    }

    private void checkUserStatus(){
        user = mAuth.getCurrentUser();
        if(user != null){
            String userId = user.getUid();

            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
            usersRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String email = dataSnapshot.child("email").getValue(String.class);
                        String name = dataSnapshot.child("name").getValue(String.class);
                        userName.setText(name);
                        userEmail.setText(email);
                        // Do something with the email and name
                        // e.g., update UI, store in variables, etc.
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }

            });


        }
        else{//user is not sign in
            Intent intent = new Intent(getApplicationContext(), StartupScreen.class); //needs to change
            startActivity(intent);
            finish();
        }
    }

    public void OnClickBack(View view) {
        Intent intent =  new Intent(getApplicationContext(), UserPlantListActivity.class);
        startActivity(intent);
        finish();
    }

    /*public void onClickLogout(View view) {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getApplicationContext(), StartupScreen.class); //needs to change
        startActivity(intent);
        finish();
    }*/

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
    public void callSignupScreen(View view) {

        Intent intent = new Intent(getApplicationContext(), SignupScreen.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        closeDrawer(drawerLayout);
    }
    private void handleImageCapture(int requestCode, Uri imageUri) {
        // Use the captured image URI (imageUri) to perform the desired operations
        // For example, you can display the image in an ImageView
        profilePicture.setImageURI(imageUri);
        progressDialog.dismiss();
        // Or you can upload the image to Firebase Storage or any other storage service
        // For example:
        // uploadImageToFirebase(imageUri);

        // Add this line to call uploadProfilePicture with the correct requestCode
        if (requestCode == IMAGE_PICK_GALLERY_CODE) {
            uploadProfilePicture(imageUri);
        }
    }

    public void openPlantList(View view) {
        redirectActivity(ProfileActivity.this, UserPlantListActivity.class);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        redirectActivity(ProfileActivity.this, UserPlantListActivity.class);

    }

}