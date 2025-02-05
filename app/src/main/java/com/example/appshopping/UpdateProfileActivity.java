package com.example.appshopping;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class UpdateProfileActivity extends AppCompatActivity {
    private TextView nameProfile,phoneNumberProfile, emailProfile, usernameProfile, addressProfile;
    private Button updateButton;
    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;
    private StorageReference storageReference;
    private FirebaseUser user;
    private ImageView profileImg;
    private static final String TAG = "TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_update_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Intent data = getIntent();
        final String name = data.getStringExtra("name");
        String email = data.getStringExtra("email");
        String userName = data.getStringExtra("userName");
        String phoneNumber = data.getStringExtra("phoneNumber");
        String address = data.getStringExtra("address");

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        user = fAuth.getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference();

        nameProfile = findViewById(R.id.editName);
        emailProfile = findViewById(R.id.editEmail);
        addressProfile = findViewById(R.id.editAddress);
        usernameProfile = findViewById(R.id.editUsername);
        phoneNumberProfile = findViewById(R.id.editPhone);

        updateButton = findViewById(R.id.updateButton);
        profileImg = findViewById(R.id.profileImgEdit);
        StorageReference profileRef = storageReference.child("users/" + fAuth.getCurrentUser().getUid() + "/profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(UpdateProfileActivity.this)
                        .load(uri)
                        .into(profileImg);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG,"Error getting profile image URL", e);
            }
        });

        profileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGalleryIntent,1000);
            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Update button clicked.");

                if (nameProfile.getText().toString().isEmpty() || emailProfile.getText().toString().isEmpty() ||
                        phoneNumberProfile.getText().toString().isEmpty() || usernameProfile.getText().toString().isEmpty() ||
                        addressProfile.getText().toString().isEmpty()) {
                    Toast.makeText(UpdateProfileActivity.this, "One or Many fields are empty.", Toast.LENGTH_SHORT).show();
                    return;
                }

                final String name = nameProfile.getText().toString();
                final String email = emailProfile.getText().toString();
                final String phoneNumber = phoneNumberProfile.getText().toString();
                final String userName = usernameProfile.getText().toString();
                final String address = addressProfile.getText().toString();

                Log.d(TAG, "Attempting to update email to: " + email);
                user.updateEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        String userID = fAuth.getCurrentUser().getUid();
                        Log.d(TAG, "Email update successful. UserID: " + userID);

                        PersonClass personClass = new PersonClass(name, email, phoneNumber, userName, address);
                        DocumentReference docRef = fStore.collection("Users").document(userID);

                        Log.d(TAG, "Attempting to update Firestore document.");
                        docRef.set(personClass).addOnSuccessListener(aVoid -> {
                            Log.d(TAG, "Update Profile Success! UserID: " + userID);
                            startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                            finish();
                        }).addOnFailureListener(e -> {
                            Log.d(TAG, "Firestore update failed: " + e.toString());
                            Toast.makeText(UpdateProfileActivity.this, "Firestore Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Email update failed: " + e.toString());
                        Toast.makeText(UpdateProfileActivity.this, "Email Update Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        emailProfile.setText(email);
        nameProfile.setText(name);
        addressProfile.setText(address);
        phoneNumberProfile.setText(phoneNumber);
        usernameProfile.setText(userName);

        Log.d(TAG, "onCreate: " + name + " " + email + " " + phoneNumber + " " + userName + " " + address);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000) {
            if (resultCode == Activity.RESULT_OK) {
                Uri image = data.getData();
                uploadImageToFirebase(image);
            }
        }
    }

    private void uploadImageToFirebase(Uri imageUri) {
        // Upload image to Firebase Storage
        final StorageReference fileRef = storageReference.child("users/" + fAuth.getCurrentUser().getUid() + "/profile.jpg");
        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(getApplicationContext())
                                .load(uri)
                                .into(profileImg);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Failed.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}