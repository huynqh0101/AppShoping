package com.example.appshopping;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    private EditText loginUsername, loginPassword;
    private Button loginButton;
    private TextView signupRedirectText;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    String[] options = {"Khách Hàng", "Quản Lý"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setSpiner();

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        loginUsername = findViewById(R.id.login_username);
        loginPassword = findViewById(R.id.login_password);
        loginButton = findViewById(R.id.login_button);
        signupRedirectText = findViewById(R.id.signupRedirectText);

        loginButton.setOnClickListener(v -> {
            String username = loginUsername.getText().toString();
            String password = loginPassword.getText().toString();
            loginUser(username, password);
        });


        signupRedirectText.setOnClickListener(v -> {

            startActivity(new Intent(LoginActivity.this, SignupActivity.class));
        });
    }

    // Hàm đăng nhập
    private void loginUser(String username, String password) {
        Spinner spinner = findViewById(R.id.spDangNhap);
        String selectedRole = spinner.getSelectedItem().toString();
        if(selectedRole.equals(options[0])) {
            db.collection("Users")
                    .whereEqualTo("username", username)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            if (!task.getResult().isEmpty()) {
                                DocumentSnapshot document = task.getResult().getDocuments().get(0);
                                String email = document.getString("email");

                                if (email != null) {
                                    mAuth.signInWithEmailAndPassword(email, password)
                                            .addOnCompleteListener(this, task1 -> {
                                                if (task1.isSuccessful()) {

                                                    FirebaseUser user = mAuth.getCurrentUser();
                                                    Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();

                                                    startActivity(new Intent(LoginActivity.this, ProfileActivity.class));
                                                    finish();
                                                } else {
                                                    Toast.makeText(LoginActivity.this, "Authentication failed: " + task1.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                } else {
                                    Toast.makeText(LoginActivity.this, "Email not found in database.", Toast.LENGTH_SHORT).show();
                                }
                            } else {

                                Toast.makeText(LoginActivity.this, "User not found.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(LoginActivity.this, "Failed to query Firestore: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            db.collection("Manager")
                    .whereEqualTo("username", username)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            if (!task.getResult().isEmpty()) {
                                DocumentSnapshot document = task.getResult().getDocuments().get(0);
                                String email = document.getString("email");

                                if (email != null) {
                                    mAuth.signInWithEmailAndPassword(email, password)
                                            .addOnCompleteListener(this, task1 -> {
                                                if (task1.isSuccessful()) {

                                                    FirebaseUser user = mAuth.getCurrentUser();
                                                    Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();

                                                    startActivity(new Intent(LoginActivity.this, ProfileActivity.class));
                                                    finish();
                                                } else {
                                                    Toast.makeText(LoginActivity.this, "Authentication failed: " + task1.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                } else {
                                    Toast.makeText(LoginActivity.this, "Email not found in database.", Toast.LENGTH_SHORT).show();
                                }
                            } else {

                                Toast.makeText(LoginActivity.this, "User not found.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(LoginActivity.this, "Failed to query Firestore: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void setSpiner() {
        Spinner spinner = findViewById(R.id.spDangNhap);

        // Sử dụng ArrayAdapter để kết nối dữ liệu với Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, options);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        // Xử lý sự kiện khi người dùng chọn một mục trong Spinner
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Lấy mục đã chọn
                String selectedItem = parent.getItemAtPosition(position).toString();
                // Hiển thị mục đã chọn bằng Toast
                Toast.makeText(LoginActivity.this, "Selected: " + selectedItem, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Khi không có mục nào được chọn
            }
        });
    }
}

