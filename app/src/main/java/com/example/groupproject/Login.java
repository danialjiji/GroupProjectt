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
    Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(v -> {
            String username = etUsername.getText().toString();
            String password = etPassword.getText().toString();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show();
            } else {
                // Fix: Create instance of your DbHelper class
                DbHelper dbHelper = new DbHelper(this);
                SQLiteDatabase db = dbHelper.getReadableDatabase();

                // Run query to check login
                Cursor cursor = db.rawQuery("SELECT * FROM USER WHERE username=? AND password=?", new String[]{username, password});

                if (cursor.moveToFirst()) {
                    Toast.makeText(this, "Login Success", Toast.LENGTH_SHORT).show();

                    // Get userId from cursor
                    int userId = cursor.getInt(cursor.getColumnIndex("userID")); // change to your actual column name if different

                    // Pass it to AddFriend
                    Intent intent = new Intent(Login.this, AddFriend.class);
                    intent.putExtra("userId", userId);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(this, "Invalid Username or Password", Toast.LENGTH_SHORT).show();
                }

                cursor.close();
                db.close();
            }
        });
    }
}
