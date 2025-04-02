package com.example.task2go;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {

    private EditText editTextUserID, editTextUserName, editTextEmail, editTextPhone, editTextAddress;
    private Button btnSaveChanges;
    private ImageView profileImage, btnChangePhoto, btnBack;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser user;
    private FirebaseUser currentUser;
    private StorageReference storageReference;
    private ProgressDialog progressDialog;

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    private static final String TAG = "EditProfileActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Initialize UI Components
        editTextUserName = findViewById(R.id.editTextUserName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPhone = findViewById(R.id.editTextPhone);
        editTextAddress = findViewById(R.id.editTextAddress);
        btnSaveChanges = findViewById(R.id.btnSaveChanges);
        profileImage = findViewById(R.id.profileImage);
        btnChangePhoto = findViewById(R.id.btnChangePhoto);


        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference("ProfileImages");

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Updating profile...");
        progressDialog.setCancelable(false);

        if (currentUser == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadUserProfile();

        // Save Changes Button
        btnSaveChanges.setOnClickListener(v -> saveUserProfile());

        // Change Profile Photo Button
        btnChangePhoto.setOnClickListener(v -> openGallery());
    }

    private void loadUserProfile() {
        if (user != null) {
            if (editTextEmail != null) {
                editTextEmail.setText(user.getEmail()); // Set email safely
            } else {
                Toast.makeText(this, "Email field not found", Toast.LENGTH_SHORT).show();
            }

            db.collection("users").document(user.getUid())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            if (editTextUserName != null)
                                editTextUserName.setText(documentSnapshot.getString("username"));

                            if (editTextPhone != null)
                                editTextPhone.setText(documentSnapshot.getString("mobile"));

                            if (editTextAddress != null)
                                editTextAddress.setText(documentSnapshot.getString("address"));

                            String profileUrl = documentSnapshot.getString("profileImageUrl");
                            if (profileUrl != null && !profileUrl.isEmpty() && profileImage != null) {
                                Glide.with(this).load(profileUrl).into(profileImage);
                            }
                        }
                    });
        }
    }


    private void saveUserProfile() {
        String userID = currentUser.getUid();
        String name = editTextUserName.getText().toString().trim();
        String phone = editTextPhone.getText().toString().trim();
        String address = editTextAddress.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(phone) || TextUtils.isEmpty(address)) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.show();

        Map<String, Object> userUpdates = new HashMap<>();
        userUpdates.put("name", name);
        userUpdates.put("mobile", phone);
        userUpdates.put("address", address);

        DocumentReference userRef = db.collection("Users").document(userID);
        userRef.update(userUpdates)
                .addOnSuccessListener(aVoid -> {
                    progressDialog.dismiss();
                    Toast.makeText(EditProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Log.e(TAG, "Error updating profile: ", e);
                    Toast.makeText(EditProfileActivity.this, "Failed to update profile", Toast.LENGTH_SHORT).show();
                });
    }

    // Open Gallery to Select Profile Photo
    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Profile Picture"), PICK_IMAGE_REQUEST);
    }

    // Handle Image Selection Result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            profileImage.setImageURI(imageUri); // Show selected image
            uploadProfileImage();
        }
    }

    // Upload Image to Firebase Storage
    private void uploadProfileImage() {
        if (imageUri != null) {
            progressDialog.setMessage("Uploading image...");
            progressDialog.show();

            StorageReference fileRef = storageReference.child(currentUser.getUid() + ".jpg");
            fileRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        updateProfileImage(uri.toString());
                    }))
                    .addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        Toast.makeText(EditProfileActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Image upload failed", e);
                    });
        }
    }

    // Update Firestore with New Profile Image URL
    private void updateProfileImage(String imageUrl) {
        DocumentReference userRef = db.collection("Users").document(currentUser.getUid());
        userRef.update("profilePic", imageUrl)
                .addOnSuccessListener(aVoid -> {
                    progressDialog.dismiss();
                    Toast.makeText(EditProfileActivity.this, "Profile photo updated", Toast.LENGTH_SHORT).show();
                    Picasso.get().load(imageUrl).into(profileImage);
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(EditProfileActivity.this, "Failed to update profile photo", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Profile photo update failed", e);
                });
    }
}
