package com.mta.greenguardianapplication;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
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
import com.bumptech.glide.signature.ObjectKey;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mta.greenguardianapplication.LoginSignup.StartupScreen;
import com.mta.greenguardianapplication.model.UserPlant;

import java.io.File;
import java.io.FileOutputStream;
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
    LinearLayout myPlants, chat, logout, myProfile, addPlant, plantLibrary;
    private ActivityResultLauncher<Uri> cameraLauncher;
    private ActivityResultLauncher<String> galleryLauncher;
    private AlertDialog progressDialog;
    private String imageUrl;

    String storagePath = "Users_Plants_Img/";
    Uri image_uri;
    private static final int CAMERA_REQUEST = 1888;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.form_add_user_plant);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        String plantType = getIntent().getStringExtra("plantType");
        String optimalHumidity = getIntent().getStringExtra("optimalHumidity");
        String pictureUrl = getIntent().getStringExtra("pictureUrl");
        String oldNickName = getIntent().getStringExtra("nickName");

        cameraPermission = new String[]{android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storageReference = FirebaseStorage.getInstance().getReference();

        drawerLayout = findViewById(R.id.drawerLayout);
        menu = findViewById(R.id.menu);
        myPlants = findViewById(R.id.myPlants);
        chat = findViewById(R.id.chat);
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

        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                redirectActivity(AddUserPlantForm.this, MainChatActivity.class);
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
                Toast.makeText(AddUserPlantForm.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                // Image capture canceled or failed, handle accordingly
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
                        }
                    }
                }
        );

        // Assign default values if the extras are null
        plantType = plantType != null ? plantType : "";
        optimalHumidity = optimalHumidity != null ? optimalHumidity : "";
        pictureUrl = pictureUrl != null ? pictureUrl : "";
        oldNickName = oldNickName != null ? oldNickName : "";
        mDatabase = FirebaseDatabase.getInstance().getReference("UserPlants");
        initializeComponents(plantType, optimalHumidity,pictureUrl,oldNickName);
    }

    private void initializeComponents(String plantType, String optimalHumidity, String pictureUrl, String oldNickName) {
        TextInputEditText inputEditPlantType = findViewById(R.id.form_textFieldPlantType);
        TextInputEditText inputEditOptimalHumidity = findViewById(R.id.form_textFieldOptimalHumidity);
        TextInputEditText inputEditNickName = findViewById(R.id.form_textFieldNickName);
        MaterialButton buttonSaveUserPlant = findViewById(R.id.form_buttonSaveUserPlant);

        if (!Objects.equals(pictureUrl, "")) {
            Glide.with(plantPicture.getContext())
                    .load(pictureUrl)
                    .signature(new ObjectKey(System.currentTimeMillis())) // Use a unique identifier as the signature
                    .apply(new RequestOptions().circleCrop())
                    .into(plantPicture);
            imageUrl = pictureUrl;
        } else {
            Glide.with(plantPicture.getContext())
                    .load(R.drawable.ic_launcher_background)
                    .signature(new ObjectKey(System.currentTimeMillis())) // Use a unique identifier as the signature
                    .into(plantPicture);
        }

        plantPicture.setTag(pictureUrl);
        inputEditPlantType.setText(plantType);
        inputEditOptimalHumidity.setText(optimalHumidity);
        inputEditNickName.setText(oldNickName);

        buttonSaveUserPlant.setOnClickListener(view -> {
            String type = String.valueOf(inputEditPlantType.getText());
            String nickNameStr = String.valueOf(inputEditNickName.getText());
            String optimalHumidityStr = String.valueOf(inputEditOptimalHumidity.getText());

            if (TextUtils.isEmpty(type)) {
                inputEditPlantType.setError("Plant type is required.");
                return;
            }

            if (TextUtils.isEmpty(optimalHumidityStr)) {
                inputEditOptimalHumidity.setError("Optimal humidity is required.");
                return;
            }

            if (TextUtils.isEmpty(nickNameStr)) {
                inputEditNickName.setError("Nickname is required.");
                return;
            }

            addNewUserPlant(oldNickName, type, nickNameStr, Integer.parseInt(optimalHumidityStr), imageUrl);
            Intent intent = new Intent(AddUserPlantForm.this, UserPlantListActivity.class);
            startActivity(intent);
        });
    }

    private void uploadPlantPicture(Uri image_uri) {
        String filePathAndName = storagePath + user.getUid() + "/" +  System.currentTimeMillis();
        StorageReference storageReference1 = storageReference.child(filePathAndName);

        storageReference1.putFile(image_uri)
                .addOnSuccessListener(taskSnapshot -> {
                    // The image upload is successful, you can get the download URL here if needed
                    storageReference1.getDownloadUrl().addOnSuccessListener(uri -> {
                        // uri contains the download URL of the uploaded image, you can save it to the database or use it as needed
                        String downloadUrl = uri.toString();
                        imageUrl = downloadUrl;
                        // Dismiss the progress dialog once the update process is complete
                        progressDialog.dismiss();
                    });
                })
                .addOnFailureListener(exception -> {
                    // Dismiss the progress dialog in the failure case as well
                    progressDialog.dismiss();
                });
    }

    private void handleImageCapture(int requestCode, Uri imageUri) {
        plantPicture.setImageURI(imageUri);
        progressDialog.dismiss();
        imageUrl = imageUri.toString();
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
                        progressDialog.dismiss();
                    } else {
                        pickFromGallery();
                        progressDialog.dismiss();
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
                progressDialog.dismiss();
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
            //cameraLauncher.launch(null);
            cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, CAMERA_REQUEST);
        } else {
            // If there's no camera app available, show a message to the user or handle the situation accordingly
            Toast.makeText(this, "No camera app found", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            Uri myUri = bitmapToUriConverter(photo);
            plantPicture.setImageURI(myUri);
            progressDialog.dismiss();
            imageUrl = myUri.toString();
            uploadPlantPicture(myUri);
            //imageView.setImageBitmap(photo);

        }
    }
    private Uri bitmapToUriConverter(Bitmap bitmap) {
        Uri uri = null;
        try {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            // Ensure the bitmap is not compressed
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            // Create a temporary file to store the bitmap
            File file = new File(getCacheDir(), "temp_image");
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
            uri = Uri.fromFile(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return uri;
    }

    public void addNewUserPlant(String oldNickName, String type, String nickName, int optimalHumidity, String pictureUrl) {
        String plantId = getIntent().getStringExtra("plantId");
        if(plantId == null){
            plantId = mDatabase.push().getKey();
        }
        else {
            editExistsPlant(oldNickName, nickName, type, optimalHumidity,pictureUrl);
            return;
        }

        UserPlant userPlant = new UserPlant(plantId, nickName, optimalHumidity, pictureUrl == null ? "" : pictureUrl, type, "123", -1);
        mDatabase.child(nickName).setValue(userPlant); // Save the plant with the generated ID
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference("Users");
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String userId = mAuth.getCurrentUser().getUid();

        usersRef.child(userId).child("plants").child(nickName).setValue(userPlant)
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

    private void editExistsPlant(String oldNickName, String nickName, String type, int optimalHumidity, String pictureUrl) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String userId = mAuth.getCurrentUser().getUid();
        DatabaseReference userPlantsRef = FirebaseDatabase.getInstance().getReference("Users").child(userId).child("plants");

        // Read the data from the existing node
        userPlantsRef.child(oldNickName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Get the old plant data
                    UserPlant oldPlant = dataSnapshot.getValue(UserPlant.class);

                    if (!oldPlant.getNickName().equals(nickName)) {
                        // If nickName is changed, create a new node with the new name
                        userPlantsRef.child(nickName).setValue(oldPlant);
                        userPlantsRef.child(nickName).child("nickName").setValue(nickName);
                        userPlantsRef.child(nickName).child("optimalHumidity").setValue(optimalHumidity);
                        userPlantsRef.child(nickName).child("plantType").setValue(type);
                        userPlantsRef.child(nickName).child("pictureUrl").setValue(pictureUrl);
                        // Remove the old node
                        userPlantsRef.child(oldNickName).removeValue();
                    } else {
                        // If nickName is not changed, update the specific fields
                        userPlantsRef.child(nickName).child("optimalHumidity").setValue(optimalHumidity);
                        userPlantsRef.child(nickName).child("plantType").setValue(type);
                        userPlantsRef.child(nickName).child("pictureUrl").setValue(pictureUrl);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error if necessary
            }
        });
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        redirectActivity(AddUserPlantForm.this, UserPlantListActivity.class);
    }
}
