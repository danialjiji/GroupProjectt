package com.example.groupproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Register extends AppCompatActivity {

    EditText etUsername, etName, etGender, etEmail, etPassword;
    Button btnRegister;
    DbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DbHelper(this);

        etUsername = findViewById(R.id.etRegisterUsername);
        etName = findViewById(R.id.etRegisterName);
        etGender = findViewById(R.id.etRegisterGender);
        etEmail = findViewById(R.id.etRegisterEmail);
        etPassword = findViewById(R.id.etRegisterPassword);
        btnRegister = findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String name = etName.getText().toString().trim();
            String gender = etGender.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (username.isEmpty() || name.isEmpty() ||gender.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else {
                // Insert data into USER table in database
                dbHelper.insertUser(username, name, email, password, gender);
                Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show();

                // Optionally go to Login page
                Intent intent = new Intent(this, Login.class);
                startActivity(intent);
                finish();
            }
        });

    }
}
