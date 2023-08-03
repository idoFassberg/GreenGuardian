package com.mta.greenguardianapplication;

import android.Manifest;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mta.greenguardianapplication.LoginSignup.StartupScreen;
import com.mta.greenguardianapplication.model.UserPlant;

import java.util.Objects;

public class AddUserPlantForm extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private DatabaseReference mDatabase;
    DrawerLayout drawerLayout;
    ImageView menu, plantPicture;
    StorageReference storageReference;
    String cameraPermission[];
    String storagePermission[];
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;
    private static final int IMAGE_PICK_GALLERY_CODE = 300;
    private static final int IMAGE_PICK_CAMERA_CODE = 400;
    private static final int STORAGE_PERMISSION_REQUEST_CODE = 1001;
    LinearLayout myPlants, forum, logout, myProfile, addPlant, plantLibrary;
    private ActivityResultLauncher<Uri> cameraLauncher;
    private ActivityResultLauncher<String> galleryLauncher;
    private AlertDialog progressDialog;
    private String imageUrl;

    String storagePath = "Users_Plants_Img/";
    Uri image_uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.form_add_user_plant);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        String plantType = getIntent().getStringExtra("plantType");
        String optimalHumidity = getIntent().getStringExtra("optimalHumidity");
        String pictureUrl = getIntent().getStringExtra("pictureUrl");
        String boardId = getIntent().getStringExtra("boardId");
        String nickName = getIntent().getStringExtra("nickName");

        cameraPermission = new String[]{android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storageReference = FirebaseStorage.getInstance().getReference();

        drawerLayout = findViewById(R.id.drawerLayout);
        menu = findViewById(R.id.menu);
        myPlants = findViewById(R.id.myPlants);
        forum = findViewById(R.id.forum);
        logout = findViewById(R.id.logoutNav);
        myProfile = findViewById(R.id.my_profile);
        addPlant = findViewById(R.id.add_plant);
        plantLibrary = findViewById(R.id.plantsLibrary);
        plantPicture = findViewById(R.id.plant_image);

        plantPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCustomProgressDialog("updating plant image");
                showEditImageDialog();
            }
        });

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

        plantLibrary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(AddUserPlantForm.this, PlantListActivity.class);
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
        cameraLauncher = registerForActivityResult(new ActivityResultContracts.TakePicture(), result -> {
            if (result) {
                // Image capture successful, handle the image URI here
                uploadPlantPicture(image_uri);
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
                            Toast.makeText(AddUserPlantForm.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            // Image selection canceled or failed, handle accordingly
                            // For example, show a Toast or perform some action
                        }
                    }
                }
        );


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
        //ImageView imageView = findViewById(R.id.plant_image);
        MaterialButton buttonSaveUserPlant = findViewById(R.id.form_buttonSaveUserPlant);
        if(pictureUrl != "") {
            Glide.with(plantPicture.getContext())
                    .load(pictureUrl)
                    .apply(new RequestOptions().circleCrop())
                    .into(plantPicture);
        }
        else
        {
            Glide.with(plantPicture.getContext())
                    .load(R.drawable.ic_launcher_background)
                    .apply(new RequestOptions().circleCrop())
                    .into(plantPicture);
        }
        plantPicture.setTag(pictureUrl);
        inputEditPlantType.setText(plantType);
        inputEditOptimalHumidity.setText(optimalHumidity);
        inputEditBoardId.setText(boardId);
        inputEditNickName.setText(nickName);

        buttonSaveUserPlant.setOnClickListener(view -> {
            String type = String.valueOf(inputEditPlantType.getText());
            String nickNameStr = String.valueOf(inputEditNickName.getText());
            String optimalHumidityStr = String.valueOf(inputEditOptimalHumidity.getText());
            String boardIdStr = String.valueOf(inputEditBoardId.getText());
            if(!Objects.equals(pictureUrl, ""))
                imageUrl = pictureUrl;
            addNewUserPlant(type, nickNameStr,Integer.parseInt(optimalHumidityStr),boardIdStr, imageUrl);

            Intent intent = new Intent(AddUserPlantForm.this, UserPlantListActivity.class);
            startActivity(intent);
        });
    }
    private void uploadPlantPicture(Uri image_uri) {
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
                        imageUrl = downloadUrl;

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
                    Toast.makeText(AddUserPlantForm.this, "Profile picture updated successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    // There was an error saving the profile picture URL
                    Toast.makeText(AddUserPlantForm.this, "Failed to update profile picture", Toast.LENGTH_SHORT).show();
                });
    }
    private void handleImageCapture(int requestCode, Uri imageUri) {
        // Use the captured image URI (imageUri) to perform the desired operations
        // For example, you can display the image in an ImageView
        plantPicture.setImageURI(imageUri);
        progressDialog.dismiss();
        // Or you can upload the image to Firebase Storage or any other storage service
        // For example:
        // uploadImageToFirebase(imageUri);
        imageUrl = imageUri.toString();
        // Add this line to call uploadProfilePicture with the correct requestCode
        if (requestCode == IMAGE_PICK_GALLERY_CODE) {
            uploadPlantPicture(imageUri);
        }
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
                Toast.makeText(AddUserPlantForm.this, "Storage permission denied", Toast.LENGTH_SHORT).show();
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

    public void addNewUserPlant(String type, String nickName, int optimalHumidity, String boardId, String pictureUrl) {
        String plantId = getIntent().getStringExtra("plantId");
        if(plantId == null){
            plantId = mDatabase.push().getKey();
        }
        UserPlant userPlant = new UserPlant(plantId, nickName, optimalHumidity, pictureUrl == null ? "" : pictureUrl, type, "123", boardId,-1 );
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