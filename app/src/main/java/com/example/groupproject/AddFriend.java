package com.example.groupproject;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.app.DatePickerDialog;

import com.google.android.material.navigation.NavigationView;

import java.util.Calendar;

public class AddFriend extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    EditText etFN, etFNum, etFage, etFDob, etFEmail;
    RadioGroup radiogp1;
    RadioButton radiob1, radiob2;

    Button btnSubmit, btnReset;
    DatePickerDialog.OnDateSetListener onDateSetListener;
    DbHelper db;

    DrawerLayout addfriend;
    NavigationView navigationView;
    ActionBarDrawerToggle drawerToggle;
    int currentUserId = -1;
    String username = "";
    String fgender = "";


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);

        // Get username and userId from intent
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        currentUserId = intent.getIntExtra("userID", -1); // ✔️ This is correct

        if (currentUserId == -1) {
            Toast.makeText(this, "Error: User not recognized", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Set toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Drawer setup
        addfriend = findViewById(R.id.main_recyclerview);
        navigationView = findViewById(R.id.navigation_view);
        Menu menu = navigationView.getMenu();
        MenuItem item = menu.findItem(R.id.nav_addfriend);
        if (item != null) {
            item.setChecked(true);
        }
        drawerToggle = new ActionBarDrawerToggle(this, addfriend,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        addfriend.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        navigationView.setNavigationItemSelectedListener(this);
        
        // Update nav header with username
        if (username != null && !username.isEmpty()) {
            View headerView = navigationView.getHeaderView(0);
            TextView navUsername = headerView.findViewById(R.id.nav_username);
            navUsername.setText(username);
        }

        db = new DbHelper(this);
        /*int userId = getIntent().getIntExtra("userId", -1); // -1 is default if not found

        if (userId != -1) {
            // use userId as needed
        } else {
            Toast.makeText(this, "User ID not found", Toast.LENGTH_SHORT).show();
        }*/


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
                db.insertFriend(fname, fnum, femail, fage, fdob, fgender, currentUserId);
                Toast.makeText(this, "Friend Added!", Toast.LENGTH_SHORT).show();
                Intent intentToFriendList = new Intent(this, FriendList.class);
                intentToFriendList.putExtra("username", username);
                intentToFriendList.putExtra("userId", currentUserId);
                startActivity(intentToFriendList);
                finish();

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return drawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Intent i = null;
        int id = item.getItemId();

        if (id == R.id.nav_dashboard) {
            i = new Intent(this, dashboard.class);
        } else if (id == R.id.nav_friends) {
            i = new Intent(this, FriendList.class);
        } else if (id == R.id.nav_search) {
            i = new Intent(this, SearchActivity.class);
        } else if (id == R.id.nav_addfriend) {
            i = new Intent(this, AddFriend.class);
        } else if (id == R.id.nav_chart) {
            i = new Intent(this, ChartActivity.class);
        } else if (id == R.id.nav_wheel) {
            i = new Intent(this, WheelActivity.class);
        }

        if (i != null) {
            i.putExtra("userID", currentUserId);
            i.putExtra("username", username);
            startActivity(i);
        }

        addfriend.closeDrawers();
        return true;
    }
}
