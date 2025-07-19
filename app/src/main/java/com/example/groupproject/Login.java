package com.example.groupproject;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class Login extends AppCompatActivity {

    EditText etUsername, etPassword;
    Button btnLogin, btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);

        btnLogin.setOnClickListener(v -> {
            String username = etUsername.getText().toString();
            String password = etPassword.getText().toString();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show();
            } else {

                DbHelper dbHelper = new DbHelper(this);
                SQLiteDatabase db = dbHelper.getReadableDatabase();

                // Run query to check login
                Cursor cursor = db.rawQuery("SELECT * FROM USER WHERE username=? AND password=?", new String[]{username, password});

                if (cursor.moveToFirst()) {
                    int columnIndex = cursor.getColumnIndex("userID");

                    if (columnIndex != -1) {
                        int userId = cursor.getInt(columnIndex);

                        Toast.makeText(this, "Login Success", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Login.this, dashboard.class);
                        intent.putExtra("userID", userId);
                        intent.putExtra("username", username);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(this, "Login Success, but failed to retrieve userID", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(this, "Invalid Username or Password", Toast.LENGTH_SHORT).show();
                }
                cursor.close();
            }
        });

        btnRegister.setOnClickListener(v -> {
            Intent intent = new Intent(Login.this, Register.class);
            startActivity(intent);
        });

    }
}
