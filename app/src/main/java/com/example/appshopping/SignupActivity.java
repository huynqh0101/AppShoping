package com.example.appshopping;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    private EditText signupName, signupUsername, signupEmail, signupPassword, signupPhone;
    private Button signupButton;
    private TextView loginScreen;
    private ProgressBar progressBar;
    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;
    private static final String TAG = "TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();


        signupButton = findViewById(R.id.signup_button);
        signupEmail = findViewById(R.id.signup_email);
        signupName = findViewById(R.id.signup_name);
        signupPhone = findViewById(R.id.signup_phone);
        signupPassword = findViewById(R.id.signup_password);
        signupUsername = findViewById(R.id.signup_username);
        loginScreen = findViewById(R.id.loginRedirectText);
        progressBar = findViewById(R.id.progressBar);

        signupButton.setOnClickListener(view -> registerUser());

        loginScreen.setOnClickListener(view -> {
            startActivity(new Intent(SignupActivity.this, LoginActivity.class));
        });
    }


    private void registerUser() {
        String email = signupEmail.getText().toString();
        String password = signupPassword.getText().toString();
        String name = signupName.getText().toString();
        String phone = signupPhone.getText().toString();
        String username = signupUsername.getText().toString();
        String address = "Not Updated";
        //String ID =


        if (!validateInput(email, password)) {
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {

                Toast.makeText(SignupActivity.this, "Tạo người dùng thành công.", Toast.LENGTH_SHORT).show();

                String userID = fAuth.getCurrentUser().getUid();
                PersonClass user = new PersonClass(name, email, phone, username, address);


                DocumentReference documentReference = fStore.collection("Users").document(userID);
                documentReference.set(user).addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Thành công: Hồ sơ người dùng đã được tạo cho " + userID);
                    progressBar.setVisibility(View.GONE);
                    fAuth.signOut();
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    finish();
                }).addOnFailureListener(e -> {
                    Log.d(TAG, "Thất bại: " + e.toString());
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(SignupActivity.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            } else {

                Toast.makeText(SignupActivity.this, "Lỗi: " + (task.getException() != null ? task.getException().getMessage() : "Đã xảy ra lỗi"), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private boolean validateInput(String email, String password) {
        if (TextUtils.isEmpty(email)) {
            signupEmail.setError("Email không được để trống");
            return false;
        }
        if (TextUtils.isEmpty(password)) {
            signupPassword.setError("Mật khẩu không được để trống");
            return false;
        }
        if (password.length() < 6) {
            signupPassword.setError("Mật khẩu phải có ít nhất 6 ký tự");
            return false;
        }
        return true;
    }

}

