/*package com.example.groupproject;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.app.DatePickerDialog;

import java.util.Calendar;

public class AddFriend extends AppCompatActivity {

    EditText etFN, etFNum, etFage, etFDob, etFEmail;
    RadioGroup radiogp1;
    RadioButton radiob1, radiob2;

    Button btnSubmit, btnReset;
    DatePickerDialog.OnDateSetListener onDateSetListener;
    DbHelper db;

    String fgender = "";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);

        db = new DbHelper(this);

        int userid = 1;

//        int userid = getIntent().getIntExtra("userid", 1);  // Default to -1 if not found
//        if (userid == -1) {
//            Toast.makeText(this, "User ID not found. Please log in again.", Toast.LENGTH_SHORT).show();
//            finish(); // Optionally redirect to Login screen
//        }


        etFN = findViewById(R.id.et_fn);
        etFEmail = findViewById(R.id.et_femail);
        etFNum = findViewById(R.id.et_fnum);
        etFage = findViewById(R.id.et_fage);
        etFDob =  findViewById(R.id.et_fdob);

        radiogp1 = findViewById(R.id.radiogp1);
        radiob1 = findViewById(R.id.radiob1);
        radiob2 = findViewById(R.id.radiob2);

        btnSubmit = findViewById(R.id.btn_submit);
        btnReset = findViewById(R.id.btn_reset);

        etFDob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year= calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(AddFriend.this,
                        onDateSetListener,year,month,day);

                datePickerDialog.show();
            }
        });

        onDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1; //month = 0..11, 0->Jan so, 0+1=1->Jan
                String date = dayOfMonth + "/" + month+ "/" + year;
                etFDob.setText(date);
            }
        };


        radiogp1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radiob1) {
                    fgender = "Male";
                } else if (checkedId == R.id.radiob2) {
                    fgender = "Female";
                }
            }
        });

        btnSubmit.setOnClickListener(v -> {
            String fdob = etFDob.getText().toString().trim();
            String fname = etFN.getText().toString().trim();
            String femail = etFEmail.getText().toString().trim();
            String fageStr = etFage.getText().toString().trim();
            String fnum = etFNum.getText().toString().trim();

            if (fname.isEmpty() || fdob.isEmpty() || femail.isEmpty() || fageStr.isEmpty() || fnum.isEmpty() || fgender.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else {
                int fage = Integer.parseInt(fageStr);
                db.insertFriend(fname, fnum, femail, fage, fdob, fgender, userid);
                Toast.makeText(this, "Friend Added!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, FriendList.class);
                intent.putExtra("userid", 1);
                startActivity(intent);

            }
        });



    }
}*/