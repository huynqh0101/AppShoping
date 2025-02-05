package com.example.appshopping;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ProfileActivity extends AppCompatActivity {
    private TextView name,phoneNumber, email, username, titleName, titleUserName, address;
    private Button changePassWord, editButton;
    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;
    private StorageReference storageReference;
    private FirebaseUser user;
    private ImageView profileImg;
    private static final int GALLERY_INTENT_CODE = 1023;
    private static final String TAG = "TAG";
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        name = findViewById(R.id.profileName);
        titleName = findViewById(R.id.titleName);
        titleUserName = findViewById(R.id.titleUsername);
        phoneNumber = findViewById(R.id.profilePhoneNb);
        email = findViewById(R.id.profileEmail);
        username = findViewById(R.id.profileUser);
        address = findViewById(R.id.profileAddress);
        changePassWord = findViewById(R.id.changePassword);
        editButton = findViewById(R.id.editButton);
        profileImg = findViewById(R.id.profileImg);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference profileRef = storageReference.child("users/" + fAuth.getCurrentUser().getUid() + "/profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(ProfileActivity.this)
                        .load(uri)
                        .into(profileImg);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG,"Error getting profile image URL", e);
            }
        });

        userId = fAuth.getCurrentUser().getUid();
        user = fAuth.getCurrentUser();

        DocumentReference documentReference = fStore.collection("Users").document(userId);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value.exists()) {
                    name.setText(value.getString("name"));
                    titleName.setText(value.getString("name"));
                    username.setText(value.getString("username"));
                    titleUserName.setText(value.getString("username"));
                    email.setText(value.getString("email"));
                    address.setText(value.getString("address"));
                    phoneNumber.setText(value.getString("phoneNumber"));
                } else {
                    Log.d(TAG,"onEvent: Document does not exist");
                }
            }
        });

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), UpdateProfileActivity.class);
                myIntent.putExtra("name", name.getText().toString());
                myIntent.putExtra("userName", username.getText().toString());
                myIntent.putExtra("phoneNumber", phoneNumber.getText().toString());
                myIntent.putExtra("address", address.getText().toString());
                myIntent.putExtra("email", email.getText().toString());
                startActivity(myIntent);
            }
        });
    }
}