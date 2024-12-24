package com.example.project3;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    private EditText etUsername, etPassword;
    private Button btnLogin;
    private TextView tvRegisterLink;
    private DatabaseHelper db;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegisterLink = findViewById(R.id.tvRegisterLink);
        db = new DatabaseHelper(this);

        btnLogin.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (db.loginUser(username, password)) {
                // Ambil userId berdasarkan usernameHDa
                int userId = db.getUserId(username);

                if (userId != -1) {
                    // Simpan user_id ke SharedPreferences
                    SharedPreferences preferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putInt("user_id", userId);
                    editor.putString("username", username);
                    editor.apply();

                    Toast.makeText(this, "Login successfully!", Toast.LENGTH_SHORT).show();

                    // Navigasi ke MainActivity dengan USER_ID dan username
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("USER_ID", userId);
                    intent.putExtra("username", username);
                    startActivity(intent);
                    finish(); // Tutup LoginActivity setelah login berhasil
                } else {
                    Toast.makeText(this, "User not found!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Login failed! Check your username or password.", Toast.LENGTH_SHORT).show();
            }
        });

        tvRegisterLink.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }
}
