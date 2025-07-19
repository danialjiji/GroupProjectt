package com.example.groupproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Register extends AppCompatActivity {

    EditText etUsername, etName, etGender, etEmail, etPassword;
    Button btnRegister, btnLogin, btnReset;
    DbHelper dbHelper;
    RadioGroup radioGroup;
    RadioButton male, female;
    String selectedgender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DbHelper(this);

        etUsername = findViewById(R.id.etRegisterUsername);
        etName = findViewById(R.id.etRegisterName);
        etEmail = findViewById(R.id.etRegisterEmail);
        etPassword = findViewById(R.id.etRegisterPassword);
        btnRegister = findViewById(R.id.btnRegister);
        btnReset = findViewById(R.id.btnReset);
        btnLogin = findViewById(R.id.btnLogin);

        radioGroup = findViewById(R.id.et_radiogp1);
        male = findViewById(R.id.rb_radiob1);
        female = findViewById(R.id.rb_radiob2);

        btnReset.setOnClickListener(v -> {
            etUsername.setText("");
            etName.setText("");
            etEmail.setText("");
            etPassword.setText("");
            radioGroup.clearCheck();
            Toast.makeText(this, "Reset Successful", Toast.LENGTH_SHORT).show();
        });

        btnRegister.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String name = etName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            int selectedId = radioGroup.getCheckedRadioButtonId();
            if (selectedId != -1) {
                RadioButton selectedRadio = findViewById(selectedId);
                selectedgender = selectedRadio.getText().toString();
            }

            if (username.isEmpty() || name.isEmpty() ||selectedgender.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else {
                // Insert data into USER table in database
                dbHelper.insertUser(username, name, email, password, selectedgender);
                Toast.makeText(this, "Registration Successful!", Toast.LENGTH_SHORT).show();

                // Optionally go to Login page
                Intent intent = new Intent(this, Login.class);
                startActivity(intent);
                finish();
            }
        });

        btnLogin.setOnClickListener(v -> {
            Intent intent = new Intent(this, Login.class);
            startActivity(intent);
        });

    }
}
