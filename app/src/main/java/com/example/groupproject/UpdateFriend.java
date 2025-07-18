package com.example.groupproject;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.view.View;
import android.app.DatePickerDialog.OnDateSetListener;
import android.widget.DatePicker;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class UpdateFriend extends AppCompatActivity {

    EditText editName, editPhone, editEmail, editAge, editDob;
    RadioGroup genderGroup;
    Button btnSave, btnReset;
    DbHelper db;

    int friendID;
    OnDateSetListener onDateSetListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_friend);

        db = new DbHelper(this);

        editName = findViewById(R.id.et_ufn);
        editPhone = findViewById(R.id.et_ufnum);
        editEmail = findViewById(R.id.et_ufemail);
        editAge = findViewById(R.id.et_ufage);
        editDob = findViewById(R.id.et_ufdob);
        genderGroup = findViewById(R.id.uradiogp1);
        btnSave = findViewById(R.id.ubtn_submit);
        btnReset = findViewById(R.id.ubtn_reset);

        // Get data from intent
        Intent intent = getIntent();
        int userid = intent.getIntExtra("userid", 1); // default to 1
        friendID = intent.getIntExtra("friendsID", 1);

        editName.setText(intent.getStringExtra("fname"));
        editPhone.setText(intent.getStringExtra("fnumber"));
        editEmail.setText(intent.getStringExtra("femail"));
        editAge.setText(intent.getStringExtra("fage"));
        editDob.setText(intent.getStringExtra("fdob"));
        String gender = intent.getStringExtra("fgender");

        if (gender.equalsIgnoreCase("Male")) {
            genderGroup.check(R.id.uradiob1);
        } else if (gender.equalsIgnoreCase("Female")) {
            genderGroup.check(R.id.uradiob2);
        }

        // Enable DatePicker for editDob
        editDob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(UpdateFriend.this,
                        onDateSetListener, year, month, day);
                datePickerDialog.show();
            }
        });

        onDateSetListener = new OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1; // 0-based month
                String date = dayOfMonth + "/" + month + "/" + year;
                editDob.setText(date);
            }
        };

        btnSave.setOnClickListener(v -> {
            String name = editName.getText().toString();
            String phone = editPhone.getText().toString();
            String email = editEmail.getText().toString();
            String ageStr = editAge.getText().toString();
            String dob = editDob.getText().toString();

            int selectedGenderId = genderGroup.getCheckedRadioButtonId();
            String selectedGender = (selectedGenderId == R.id.uradiob1) ? "Male" : "Female";

            if (name.isEmpty() || phone.isEmpty() || email.isEmpty() || ageStr.isEmpty() || dob.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            int age = Integer.parseInt(ageStr);

            int result = db.updateFriend(friendID, name, phone, email, age, dob, selectedGender);

            if (result > 0) {
                Toast.makeText(this, "Friend updated successfully!", Toast.LENGTH_SHORT).show();
                finish(); // Return to FriendList
            } else {
                Toast.makeText(this, "Failed to update friend", Toast.LENGTH_SHORT).show();
            }
        });

        btnReset.setOnClickListener(v -> {
            editName.setText("");
            editPhone.setText("");
            editEmail.setText("");
            editAge.setText("");
            editDob.setText("");
            genderGroup.clearCheck();
        });
    }
}
