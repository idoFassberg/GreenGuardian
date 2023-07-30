/*
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


public class ImageUploadUtil {
    private static final int IMAGE_PICK_GALLERY_CODE = 300;
    private static final int STORAGE_PERMISSION_REQUEST_CODE = 1001;

    private Activity activity;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private ProgressDialog progressDialog;
    private StorageReference storageReference;
    private String storagePath = "Users_Profile_Img/";

    public ImageUploadUtil(Activity activity, FirebaseAuth mAuth, FirebaseUser user) {
        this.activity = activity;
        this.mAuth = mAuth;
        this.user = user;
        this.progressDialog = new ProgressDialog(activity);
        this.storageReference = FirebaseStorage.getInstance().getReference();
    }

    public void pickProfilePicture() {
        if (!checkStoragePermission()) {
            requestStoragePermission();
        } else {
            pickFromGallery();
        }
    }

    private boolean checkStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return ContextCompat.checkSelfPermission(activity, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED;
        } else {
            return true;
        }
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(activity,
                new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                STORAGE_PERMISSION_REQUEST_CODE);
    }

    private void pickFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        activity.startActivityForResult(galleryIntent, IMAGE_PICK_GALLERY_CODE);
    }

    public void handleActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == IMAGE_PICK_GALLERY_CODE && resultCode == Activity.RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            uploadProfilePicture(imageUri);
        }
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



    // ... other methods in the ImageUploadUtil class ...
}
*/
