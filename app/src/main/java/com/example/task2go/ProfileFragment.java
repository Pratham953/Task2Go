package com.example.task2go;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class ProfileFragment extends Fragment {

    private TextView txtUserID, txtUserName, txtEmail, txtMobile, txtAddress;
    private ImageView profileImage;
    private Button btnEditProfile,btnChangePassword,btnLogout;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    private static final String TAG = "ProfileFragment";

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        txtUserID = view.findViewById(R.id.txtUserID);
        txtUserName = view.findViewById(R.id.txtUserName);
        txtEmail = view.findViewById(R.id.txtEmail);
        txtMobile = view.findViewById(R.id.txtMobile);
        txtAddress = view.findViewById(R.id.txtAddress);
        profileImage = view.findViewById(R.id.profileImage);
        btnEditProfile = view.findViewById(R.id.btnEditProfile);
        btnChangePassword=view.findViewById(R.id.btnChangePassword);
        btnLogout=view.findViewById(R.id.btnLogout);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            return view;
        }

        loadUserProfile();

        btnEditProfile.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), EditProfileActivity.class);
            startActivity(intent);
        });

        btnChangePassword.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ChangePasswordActivity.class);
            startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> logoutUser());

        return view;
    }

    private void loadUserProfile() {
        db.collection("Users").document(currentUser.getUid()).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String userID = currentUser.getUid();
                        String name = documentSnapshot.getString("name");
                        String email = currentUser.getEmail();
                        String mobile = documentSnapshot.getString("mobile");
                        String address = documentSnapshot.getString("address");
                        String profilePic = documentSnapshot.getString("profilePic");

                        txtUserID.setText(userID);
                        txtUserName.setText(name != null ? name : "N/A");
                        txtEmail.setText(email != null ? email : "N/A");
                        txtMobile.setText(mobile != null ? mobile : "N/A");
                        txtAddress.setText(address != null ? address : "N/A");

                        if (profilePic != null && !profilePic.isEmpty()) {
                            Picasso.get().load(profilePic).into(profileImage);
                        } else {
                            profileImage.setImageResource(R.drawable.ic_profile_placeholder);
                        }
                    } else {
                        Log.e(TAG, "User data not found");
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error fetching user data", e));
    }

    private void logoutUser() {
        FirebaseAuth.getInstance().signOut(); // Logs out the user

        // Navigate to LoginActivity
        Intent intent = new Intent(getActivity(), login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clears activity stack
        startActivity(intent);
    }


}
